package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.meta.ImexExportInteraction;
import uk.ac.ebi.intact.model.meta.ImexExportRelease;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexExportInteractionDaoTest extends IntactBasicTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ImexExportInteractionDao imexExportInteractionDao;

    @Autowired
    private ImexExportInteractionDao imexExportReleaseDao;

    @Test
    public void getDeletedAfter() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary("IM-1");

        getCorePersister().saveOrUpdate(interaction);

        imexExportInteractionDao.saveAsDeleted(interaction);

        entityManager.flush();
        entityManager.clear();

        List<ImexExportInteraction> deletedAfter = imexExportInteractionDao.getDeletedAfter(new SimpleDateFormat("yyyyMMdd").parse("20080101"));
        Assert.assertEquals(1, deletedAfter.size());

        Assert.assertNotNull(deletedAfter.iterator().next().getDeleted());

        Assert.assertEquals(0, imexExportInteractionDao.getDeletedAfter(new Date()).size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void getNonReleasedByInteractionAc_exception() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary("IM-1");
        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary("IM-1");

        getCorePersister().saveOrUpdate(interaction, interaction2);
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());

        ImexExportInteraction iei = new ImexExportInteraction(interaction);
        ImexExportInteraction iei2 = new ImexExportInteraction(interaction2);

        getDaoFactory().getImexExportInteractionDao().persist(iei);
        getDaoFactory().getImexExportInteractionDao().persist(iei2);

        // this will try to flush, hence throwing the integrity exception
        Assert.assertEquals(2, getDaoFactory().getImexExportInteractionDao().countAll());

    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void getNonReleasedByInteractionAc() throws Exception {
        final TransactionStatus transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary("IM-1");
        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary("IM-1");

        getCorePersister().saveOrUpdate(interaction, interaction2);
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());

        ImexExportInteraction iei = new ImexExportInteraction(interaction);


        ImexExportRelease release = new ImexExportRelease();
        release.getImexExportInteractions().add(iei);
        //iei.setImexExportRelease(release);

        getDaoFactory().getImexExportReleaseDao().persist(release);

        Assert.assertEquals(1, getDaoFactory().getImexExportReleaseDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getImexExportInteractionDao().countAll());

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);

        final TransactionStatus transactionStatus2 = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        ImexExportInteraction iei2 = new ImexExportInteraction(interaction2);
        getDaoFactory().getImexExportInteractionDao().persist(iei2);

        Assert.assertEquals(1, getDaoFactory().getImexExportReleaseDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getImexExportInteractionDao().countAll());
        
        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus2);

    }


}