package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.lifecycle.AbstractLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * Finder/persister for lifecycle events
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class LifeCycleSynchronizerTemplate<A extends AbstractLifeCycleEvent> extends AbstractIntactDbSynchronizer<LifeCycleEvent, A>
implements LifecycleEventSynchronizer<A>{

    public LifeCycleSynchronizerTemplate(SynchronizerContext context, Class<? extends A> eventClass){
        super(context, eventClass);
    }

    public A find(LifeCycleEvent object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        // check event
        prepareEvent(object, true);

        // check user
        prepareUser(object, true);
    }

    protected void prepareUser(A object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (object.getWho() != null){
            User who = object.getWho();
            object.setWho(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(who, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(who));
        }
    }

    protected void prepareEvent(A object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        CvTerm event = object.getEvent().toCvTerm();
        object.setCvEvent(enableSynchronization ?
                getContext().getLifecycleEventSynchronizer().synchronize(event, true) :
                getContext().getLifecycleEventSynchronizer().convertToPersistentObject(event));
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(A object) {
        return object.getAc();
    }

    @Override
    protected A instantiateNewPersistentInstance(LifeCycleEvent object, Class<? extends A> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, User.class, Date.class, String.class).newInstance(object.getEvent(), object.getWho(), object.getWhen(), object.getNote());
    }

    @Override
    protected void storeInCache(LifeCycleEvent originalObject, A persistentObject, A existingInstance) {
        // nothing to do
    }

    @Override
    protected A fetchObjectFromCache(LifeCycleEvent object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(LifeCycleEvent object) {
        return false;
    }

    @Override
    protected boolean isObjectAlreadyConvertedToPersistableInstance(LifeCycleEvent object) {
        return false;
    }

    @Override
    protected A fetchMatchingPersistableObject(LifeCycleEvent object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(A object) throws SynchronizerException, PersisterException, FinderException {
        // check event
        prepareEvent(object, false);

        // check user
        prepareUser(object, false);
    }

    @Override
    protected void storePersistableObjectInCache(LifeCycleEvent originalObject, A persistableObject) {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<LifeCycleEvent, A>(this));
    }
}
