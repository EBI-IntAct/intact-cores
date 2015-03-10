package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.ResultingSequence;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactResultingSequence;
import uk.ac.ebi.intact.jami.model.extension.ModelledRange;
import uk.ac.ebi.intact.jami.model.extension.ModelledResultingSequence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Default synchronizer/finder for Ranges attached to modelled features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ModelledRangeSynchronizer extends RangeSynchronizerTemplate<ModelledRange> {

    public ModelledRangeSynchronizer(SynchronizerContext context){
        super(context, ModelledRange.class);
    }

    @Override
    protected void prepareResultingSequence(ModelledRange object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        // prepare ResultingSequence
        if (object.getResultingSequence() != null){
            if (!(object.getResultingSequence() instanceof ModelledResultingSequence)){
                ResultingSequence reSeq = object.getResultingSequence();
                object.setResultingSequence(new ModelledResultingSequence(reSeq.getOriginalSequence(), reSeq.getNewSequence()));
                object.getResultingSequence().getXrefs().addAll(reSeq.getXrefs());
            }

            // prepare xrefs of resulting sequence
            prepareXrefs((ModelledResultingSequence)object.getResultingSequence(), enableSynchronization);
        }
    }

    @Override
    protected void prepareXrefs(AbstractIntactResultingSequence intactObj, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactObj.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactObj.getXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref objRef = enableSynchronization ?
                        getContext().getModelledResultingSequenceXrefSynchronizer().synchronize(xref, false):
                        getContext().getModelledResultingSequenceXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                if (objRef != xref){
                    intactObj.getXrefs().remove(xref);
                    if (objRef != null && !intactObj.getXrefs().contains(objRef)){
                        intactObj.getXrefs().add(objRef);
                    }
                }
            }
        }
    }

    @Override
    protected IntactDbSynchronizer getParticipantSynchronizer(){
        return getContext().getModelledParticipantSynchronizer();
    }
}
