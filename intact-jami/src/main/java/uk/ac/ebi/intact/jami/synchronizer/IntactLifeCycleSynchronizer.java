package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * Finder/persister for lifecycle events
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactLifeCycleSynchronizer<A extends AbstractLifecycleEvent> extends AbstractIntactDbSynchronizer<LifeCycleEvent, A> {

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> eventSynchronizer;
    private IntactDbSynchronizer<User, User> userSynchronizer;

    private static final Log log = LogFactory.getLog(IntactLifeCycleSynchronizer.class);

    public IntactLifeCycleSynchronizer(EntityManager entityManager, Class<? extends A> eventClass){
        super(entityManager, eventClass);
        this.eventSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.LIFECYCLE_EVENT_OBJCLASS);
        this.userSynchronizer = new IntactUserSynchronizer(entityManager);
    }

    public IntactLifeCycleSynchronizer(EntityManager entityManager, Class<? extends A> eventClass, IntactDbSynchronizer<CvTerm, IntactCvTerm> eventSynchronizer,
                                       IntactDbSynchronizer<User, User> userSynchronizer){
        super(entityManager, eventClass);
        this.eventSynchronizer = eventSynchronizer != null ? eventSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.LIFECYCLE_EVENT_OBJCLASS);
        this.userSynchronizer = userSynchronizer != null ? userSynchronizer : new IntactUserSynchronizer(entityManager);
    }

    public A find(LifeCycleEvent object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        // check event
        if (object.getEvent() != null){
            CvTerm event = object.getEvent();
            object.setEvent(eventSynchronizer.synchronize(event, true));
        }
        // check user
        if (object.getWho() != null){
            User who = object.getWho();
            object.setWho(userSynchronizer.synchronize(who, false));
        }
    }

    public void clearCache() {
        this.eventSynchronizer.clearCache();
        this.userSynchronizer.clearCache();
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<LifeCycleEvent, A>(this));
    }
}
