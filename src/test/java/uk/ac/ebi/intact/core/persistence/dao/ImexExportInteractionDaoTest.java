package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.meta.ImexExportInteraction;

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

    @Test
    public void getNonReleasedByInteractionAc() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary("IM-1");
        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary("IM-1");

        getCorePersister().saveOrUpdate(interaction, interaction2);
    }


}