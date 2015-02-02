package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.Participant;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCausalRelationship;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

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

    @Override
    public Collection<I> findAll(CausalRelationship object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(CausalRelationship object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(I object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize relation type
        synchronizeRelationType(object, true);

        // synchronize target
        synchronizeTarget(object, true);
    }

    protected void synchronizeTarget(I object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        Entity target = object.getTarget();
        if (target instanceof Participant){
            object.setTarget(enableSynchronization ?
                    getContext().getParticipantSynchronizer().synchronize((Participant)target, false) :
                    getContext().getParticipantSynchronizer().convertToPersistentObject((Participant)target));
        }
        // TODO: what to do with participant set and candidates?
        else{
            throw new UnsupportedOperationException("The existing causal relationship synchronizer does not take into account entities that are not participants");
        }
    }

    protected void synchronizeRelationType(I object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        CvTerm type = object.getRelationType();
        object.setRelationType(getContext().getTopicSynchronizer().synchronize(type, true));
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
        return  intactClass.getConstructor(CvTerm.class, Entity.class).newInstance(object.getRelationType(), object.getTarget());
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
    protected boolean containsObjectInstance(CausalRelationship object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(CausalRelationship object) {
        // nothing to do
    }

    @Override
    protected I fetchMatchingObjectFromIdentityCache(CausalRelationship object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(I object) throws SynchronizerException, PersisterException, FinderException {
        // synchronize relation type
        synchronizeRelationType(object, false);

        // synchronize target
        synchronizeTarget(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(CausalRelationship originalObject, I persistableObject) {
         // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(CausalRelationship originalObject) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<CausalRelationship, I>(this));
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(I existingInstance, I originalObject) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do
    }
}
