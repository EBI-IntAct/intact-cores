package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.*;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultChecksum;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.SequenceChunk;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * IntAct implementation of polymer
 *
 * NOTE: for backward compatibility with intact-core, the crc64 property is persistent.
 * We may want to remove this column in the future and this property in the future so we should avoid using this method
 * NOTE: for backward compatibility with intact-core, the sequence property is not persistent and the getSequenceChunks is how the sequence is persisted in
 * the database. getSequenceChunks should not be used in any applications and getSequence should always be used instead
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@DiscriminatorValue( "polymer" )
@Where(clause = "category = 'protein' or category = 'polymer' or category = 'nucleic_acid'")
public class IntactPolymer extends IntactMolecule implements Polymer{

    private transient String sequence;

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    private List<SequenceChunk> sequenceChunks;

    private transient Checksum crc64;

    protected IntactPolymer(){
        super();
    }

    public IntactPolymer(String name, CvTerm type) {
        super(name, type);
    }

    public IntactPolymer(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactPolymer(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactPolymer(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactPolymer(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactPolymer(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactPolymer(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactPolymer(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);
    }

    public IntactPolymer(String name) {
        super(name);
    }

    public IntactPolymer(String name, String fullName) {
        super(name, fullName);
    }

    public IntactPolymer(String name, Organism organism) {
        super(name, organism);
    }

    public IntactPolymer(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactPolymer(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactPolymer(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactPolymer(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactPolymer(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }

    @Transient
    /**
     * This sequence is generated from the sequence chunks for backward compatibility only. It may be good to persist this property as a Lob in the future
     * when intact-core is removed and remove the sequence chunks
     */
    public String getSequence() {
        if (this.sequence == null){
            initialiseSequence();
        }
        return this.sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
        convertSequence(this.sequence, getDbSequenceChunks());
    }

    @Deprecated
    /**
     * For backward compatibility with intact-core, we keep the crc64.
     * It will be removed when intact-core is removed
     * @deprecated look at checksums instead
     */
    protected String getCrc64() {
        return this.crc64 != null ? this.crc64.getValue() : null;
    }

    @Deprecated
    /**
     * For backward compatibility with intact-core, we keep the crc64.
     * It will be removed when intact-core is removed
     * @deprecated look at checksums instead. Only kept for bacward compatibility with intact-core
     */
    protected void setCrc64( String crc64 ) {
        Collection<Checksum> polymerChecksums = getChecksums();

        if (crc64 != null){
            CvTerm crc64Method = IntactUtils.createMITopic("crc64", null);
            // first remove old crc64
            if (this.crc64 != null){
                polymerChecksums.remove(this.crc64);
            }
            this.crc64 = new DefaultChecksum(crc64Method, crc64);
            polymerChecksums.add(this.crc64);
        }
        // remove all crc64 if the collection is not empty
        else if (!polymerChecksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(polymerChecksums, null, "crc64");
            this.crc64 = null;
        }
    }

    protected String convertToSequence(Collection<SequenceChunk> sequenceChunks){
        if (sequenceChunks.isEmpty()){
            return null;
        }
        StringBuilder sequence = new StringBuilder();
        for ( SequenceChunk sequenceChunk : sequenceChunks ) {
            sequence.append( sequenceChunk.getSequenceChunk() );
        }
        return sequence.toString();
    }

    protected void convertSequence( String aSequence, Collection<SequenceChunk> chunks ) {
        if (aSequence == null){
            chunks.clear();
        }
        else{
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
                    sc.setSequenceIndex(i);
                    chunks.add(sc);
                } else {
                    // create new chunk
                    chunks.add(new SequenceChunk(i, chunk));
                }
            }
        }
    }

    @OneToMany( orphanRemoval = true, cascade = {CascadeType.ALL})
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @IndexColumn( name = "sequence_index" )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Deprecated
    /**
     * NOTE: for backward compatibility with intact-core, the sequence property is not persistent and the getSequenceChunks is how the sequence is persisted in
     * the database. getSequenceChunks should not be used in any applications and getSequence should always be used instead
     * @deprecated only for backward compatibility with intact-core. Use getSequence instead
     */
    protected List<SequenceChunk> getDbSequenceChunks() {
        if (this.sequenceChunks == null){
            this.sequenceChunks = new ArrayList<SequenceChunk>();
        }
        return sequenceChunks;
    }

    protected void initialiseSequence(){
        if (!getDbSequenceChunks().isEmpty()){
            this.sequence = convertToSequence(sequenceChunks);
        }
    }

    @Override
    protected String generateObjClass() {
        return "uk.ac.ebi.intact.model.PolymerImpl";
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(Polymer.POLYMER, Polymer.POLYMER_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    protected void setDbSequenceChunks( List<SequenceChunk> sequenceChunks ) {
        this.sequenceChunks = sequenceChunks;
        this.sequence = null;
    }

    @Override
    protected void initialiseChecksums() {
        super.initialiseChecksumsWith(new PolymerChecksumList());
    }

    protected void processAddedChecksumEvent(Checksum added) {
        if (crc64 == null && ChecksumUtils.doesChecksumHaveMethod(added, null, "crc64")){
            crc64 = added;
        }
    }

    protected void processRemovedChecksumEvent(Checksum removed) {
        if (crc64 != null && crc64.equals(removed)){
            crc64 = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), null, "crc64");
        }
    }

    protected void clearPropertiesLinkedToChecksums() {
        this.crc64 = null;
    }

    protected class PolymerChecksumList extends AbstractListHavingProperties<Checksum> {
        public PolymerChecksumList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Checksum checksum) {
            processAddedChecksumEvent(checksum);
        }

        @Override
        protected void processRemovedObjectEvent(Checksum checksum) {
            processRemovedChecksumEvent(checksum);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToChecksums();
        }

    }
}
