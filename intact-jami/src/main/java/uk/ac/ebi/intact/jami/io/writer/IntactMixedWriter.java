package uk.ac.ebi.intact.jami.io.writer;

import psidev.psi.mi.jami.datasource.InteractionWriter;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.Interaction;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.ModelledInteraction;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Writer for mixed interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactMixedWriter implements InteractionWriter<Interaction> {
    private IntactEvidenceWriter evidenceWriter;
    private IntactModelledWriter modelledWriter;

    public void initialiseContext(Map<String, Object> stringObjectMap) {
        this.evidenceWriter.initialiseContext(stringObjectMap);
        this.modelledWriter.initialiseContext(stringObjectMap);
    }

    public void start() throws MIIOException {
        // nothing to do
    }

    public void end() throws MIIOException {
        // nothing to do
    }

    public void write(Interaction interaction) throws MIIOException {
        if (interaction instanceof InteractionEvidence){
            this.evidenceWriter.write((InteractionEvidence)interaction);
        }
        else if (interaction instanceof ModelledInteraction){
            this.modelledWriter.write((ModelledInteraction)interaction);
        }
        else{
            throw new IllegalArgumentException("The IntAct mixed writer can only write modelled interactions or interaction evidences.");
        }
    }

    public void write(Collection<? extends Interaction> interactions) throws MIIOException {
        write(interactions.iterator());
    }

    public void write(Iterator<? extends Interaction> iterator) throws MIIOException {
        while(iterator.hasNext()){
            write(iterator.next());
        }
    }

    public void flush() throws MIIOException {
        // nothing to do
    }

    public void close() throws MIIOException {
        this.evidenceWriter.close();
        this.modelledWriter.close();
    }

    public void reset() throws MIIOException {
        this.evidenceWriter.reset();
        this.modelledWriter.reset();
    }
}
