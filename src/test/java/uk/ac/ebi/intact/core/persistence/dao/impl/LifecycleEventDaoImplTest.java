package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.persistence.dao.LifecycleEventDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvLifecycleEvent;
import uk.ac.ebi.intact.model.LifecycleEvent;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * LifecycleEventDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2,5
 */
public class LifecycleEventDaoImplTest extends IntactBasicTestCase {

    @Test
    @Ignore ("the core persister doesn't handle objects that are not at the very least AnnotatedObject")
    public void getByPublicationAc() throws Exception {

        final Publication publication = getMockBuilder().createPublication( "123" );

        //  add a few events

        User sandra = getMockBuilder().createUserSandra();

        CvLifecycleEvent createdEvent = getMockBuilder().createCvObject( CvLifecycleEvent.class, "PL:0016", "created" );

        final LifecycleEvent created = getMockBuilder().createLifecycleEvent( createdEvent, sandra );
        publication.addLifecycleEvent( created );

        CvLifecycleEvent reservedEvent = getMockBuilder().createCvObject( CvLifecycleEvent.class, "PL:0017", "reserved" );
        final LifecycleEvent reserved = getMockBuilder().createLifecycleEvent( reservedEvent, sandra );
        publication.addLifecycleEvent( reserved );

        CvLifecycleEvent selfAssignedEvent = getMockBuilder().createCvObject( CvLifecycleEvent.class, "PL:0018", "self assigned" );
        final LifecycleEvent selfAssigned = getMockBuilder().createLifecycleEvent( selfAssignedEvent, sandra );
        publication.addLifecycleEvent( selfAssigned );

        getCorePersister().saveOrUpdate( publication );

        final Publication reloaded = getDaoFactory().getPublicationDao().getByAc( publication.getAc() );
        Assert.assertNotNull( reloaded );
        Assert.assertEquals( 3, reloaded.getLifecycleEvents().size() );
    }

    @Test
    public void persist() throws Exception {

        final Publication publication = getMockBuilder().createPublication( "123" );
        getCorePersister().saveOrUpdate( publication );
        Assert.assertNotNull( publication.getAc() );

        User sandra = getMockBuilder().createUserSandra();
        getDaoFactory().getUserDao().persist( sandra );
        Assert.assertNotNull( sandra.getAc() );

        CvLifecycleEvent createdEvent = getMockBuilder().createCvObject( CvLifecycleEvent.class, "PL:0016", "created" );
        getCorePersister().saveOrUpdate( createdEvent );
        Assert.assertNotNull( createdEvent.getAc() );

        final LifecycleEvent created = getMockBuilder().createLifecycleEvent( createdEvent, sandra );
        publication.addLifecycleEvent( created );

        final LifecycleEventDao eventDao = getDaoFactory().getLifecycleEventDao();
        eventDao.persist( created );

        Assert.assertEquals( 2, eventDao.getAll().size() );
    }
}
