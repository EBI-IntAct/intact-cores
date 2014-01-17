package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.SequenceChunk;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This listener listen to Polymer object pre update/persist/load events
 * and set sequence accordingly to existing annotations
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class PolymerSequenceListener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactPolymer intactPolymer) {
        if (intactPolymer.getSequence() != null){
            String oldSeq = convertToSequence(intactPolymer.getSequenceChunks());
            if (oldSeq == null || !oldSeq.equals(intactPolymer.getSequence())){
                convertSequence(intactPolymer.getSequence(), intactPolymer.getSequenceChunks());
            }
        }
        else if (!intactPolymer.getSequenceChunks().isEmpty()) {
            intactPolymer.getSequenceChunks().clear();
        }
    }

    @PostLoad
    public void postLoad(IntactPolymer intactPolymer) {
        if (!intactPolymer.getSequenceChunks().isEmpty() && intactPolymer.getSequence() == null){
            intactPolymer.setSequence(convertToSequence(intactPolymer.getSequenceChunks()));
        }
    }

    private String convertToSequence(Collection<SequenceChunk> sequenceChunks){
        StringBuilder sequence = new StringBuilder();
        for ( SequenceChunk sequenceChunk : sequenceChunks ) {
            sequence.append( sequenceChunk.getSequenceChunk() );
        }
        return sequence.toString();
    }

    private void convertSequence( String aSequence, Collection<SequenceChunk> chunks ) {
        // Save work if the new sequence is identical to the old one.
        // The container to hold redundant chunks.
        ArrayList<SequenceChunk> chunkPool = null;

        // All old data are kept, we try to recycle as much chunk as possible
        if ( !chunks.isEmpty() ) {
            // There is existing chunk ... prepare them for recycling.
            chunkPool = new ArrayList<SequenceChunk>( chunks.size() );
            chunkPool.addAll( chunks );
            int count = chunkPool.size();

            // clean chunk to recycle
            for ( int i = 0; i < count; i++ ) {
                SequenceChunk sc = chunkPool.get( i );
                chunks.remove(sc);
            }
        }

        // Note the use of integer operations
        int chunkCount = aSequence.length() / IntactUtils.MAX_SEQ_LENGTH_PER_CHUNK;
        if ( aSequence.length() % IntactUtils.MAX_SEQ_LENGTH_PER_CHUNK > 0 ) {
            chunkCount++;
        }

        for ( int i = 0; i < chunkCount; i++ ) {
            String chunk = aSequence.substring( i * IntactUtils.MAX_SEQ_LENGTH_PER_CHUNK,
                    Math.min( ( i + 1 ) * IntactUtils.MAX_SEQ_LENGTH_PER_CHUNK,
                            aSequence.length() ) );

            if ( chunkPool != null && chunkPool.size() > 0 ) {
                // recycle chunk
                SequenceChunk sc = chunkPool.remove( 0 );
                sc.setSequenceChunk( chunk );
                sc.setSequenceIndex( i );
                chunks.add(sc);
            } else {
                // create new chunk
                chunks.add(new SequenceChunk(i, chunk));
            }
        }
    }
}
