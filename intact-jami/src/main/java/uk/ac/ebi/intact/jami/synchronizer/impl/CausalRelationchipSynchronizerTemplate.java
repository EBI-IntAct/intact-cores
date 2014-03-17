package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Participant;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCausalRelationship;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for causal relationship
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class CausalRelationchipSynchronizerTemplate<I extends AbstractIntactCausalRelationship> extends AbstractIntactDbSynchronizer<CausalRelationship, I> {

    public CausalRelationchipSynchronizerTemplate(SynchronizerContext context, Class<? extends I> intactClass){
        super(context, intactClass);
    }

    public I find(CausalRelationship object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(I object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize relation type
        CvTerm type = object.getRelationType();
        object.setRelationType(getContext().getTopicSynchronizer().synchronize(type, true));

        // synchronize target
        Participant target = object.getTarget();
        object.setTarget(getContext().getParticipantSynchronizer().synchronize(target, false));
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getId();
    }

    @Override
    protected I instantiateNewPersistentInstance(CausalRelationship object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return  intactClass.getConstructor(CvTerm.class, Participant.class).newInstance(object.getRelationType(), object.getTarget());
    }

    @Override
    protected void storeInCache(CausalRelationship originalObject, I persistentObject, I existingInstance) {
        // nothing to do
    }

    @Override
    protected I fetchObjectFromCache(CausalRelationship object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(CausalRelationship object) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<CausalRelationship, I>(this));
    }
}
