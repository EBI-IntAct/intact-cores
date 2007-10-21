package uk.ac.ebi.intact.core.persister.standard;

import org.hibernate.Query;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.BehaviourType;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.persister.PersisterUnexpectedException;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.util.DebugUtil;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentPersisterTest extends AbstractPersisterTest
{
    private ExperimentPersister persister;

    @Before
    public void before() {
        persister = ExperimentPersister.getInstance();
    }

    @After
    public void after() {
        persister = null;
    }

    @Test
    public void persistExperiment_publication() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005");
        exp.setPublication(getMockBuilder().createPublication("1234567"));

        beginTransaction();

        persister.saveOrUpdate(exp);
        persister.commit();

        commitTransaction();

        beginTransaction();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        assertNotNull(refreshedExperiment);

        assertNotNull(refreshedExperiment.getPublication());
        assertEquals("1234567", refreshedExperiment.getPublication().getShortLabel());
        assertEquals(1, refreshedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test
    public void persistExperiment_assignedLabel() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-2");

        beginTransaction();

        persister.saveOrUpdate(exp);
        persister.commit();

        commitTransaction();

        beginTransaction();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-2");
        assertNotNull(refreshedExperiment);
        assertEquals(1, refreshedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test
    public void persistExperiment_institution() throws Exception {
        Institution anotherInstitution = getMockBuilder().createInstitution("IA:0000", "anotherInstitution");
        InstitutionPersister.getInstance().saveOrUpdate(anotherInstitution);
        InstitutionPersister.getInstance().commit();

        Experiment exp = new IntactMockBuilder(anotherInstitution).createExperimentRandom(1);
        String label = exp.getShortLabel();

        PersisterHelper.saveOrUpdate(exp);

        beginTransaction();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel(label);
        assertNotNull(refreshedExperiment);
        Assert.assertEquals(anotherInstitution.getShortLabel(), refreshedExperiment.getOwner().getShortLabel());

        commitTransaction();
    }

    @Test
    public void persistExperiment_similarLabels() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("tata-2005-2");

        Assert.assertEquals(1, exp.getXrefs().size());

        ExperimentXref xref = exp.getXrefs().iterator().next();

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("tata-2005");
        xref.setAc(null);
        exp2.getXrefs().clear();
        exp2.addXref(xref);

        beginTransaction();
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();

        beginTransaction();

        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("tata-2005-3");

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, reloadedExperiment.getXrefs().size());
        Assert.assertEquals(1, reloadedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test
    public void updateExperiment_avoidDuplications() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("nopub-2006-1");
        exp.setPublication(null);
        exp.getXrefs().clear();
        exp.addXref(getMockBuilder().createPrimaryReferenceXref(exp, "1234"));

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        Experiment reloadedExpBeforeUpdate = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNull(reloadedExpBeforeUpdate.getPublication());
        Assert.assertEquals(1, reloadedExpBeforeUpdate.getXrefs().size());
        Assert.assertEquals(1, reloadedExpBeforeUpdate.getInteractions().size());

        PersisterContext.getInstance().clear();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("nopub-2006-1");
        exp2.setPublication(getMockBuilder().createPublication("1234"));
        exp2.getXrefs().clear();
        exp2.addXref(getMockBuilder().createPrimaryReferenceXref(exp2, "1234"));

        beginTransaction();
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNotNull(reloadedExp.getPublication());
        Assert.assertEquals(1, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, reloadedExp.getInteractions().size());
    }

    @Test (expected = PersisterUnexpectedException.class)
    public void updateExperiment_sameLabelDifferentPubmeds() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-4");

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("lala-2005-4");

        beginTransaction();
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();
    }

    @Test
    public void syncedAndCandidateAreEqual_new() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("beha-2007-1");

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("beha-2007-2");

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();

        beginTransaction();
        Assert.assertEquals(BehaviourType.NEW, persister.syncedAndCandidateAreEqual(exp, exp2));
        commitTransaction();
    }

    @Test
    public void syncedAndCandidateAreEqual_update() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("beha-2007-1");

        Experiment exp2 = getMockBuilder().createExperimentRandom(3);
        exp2.setShortLabel("beha-2007-1");
        exp2.setPublication(exp.getPublication());

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();

        beginTransaction();
        Assert.assertEquals(BehaviourType.UPDATE, persister.syncedAndCandidateAreEqual(exp, exp2));
        commitTransaction();
    }

    @Test
    public void syncedAndCandidateAreEqual_ignore() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("beha-2007-1");

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        beginTransaction();
        Assert.assertEquals(BehaviourType.IGNORE, persister.syncedAndCandidateAreEqual(exp, exp));
        commitTransaction();
    }

    @Test
    public void existingExperimentWithoutPubInfo() throws Exception {
        Experiment expWithout = getMockBuilder().createExperimentRandom(1);
        expWithout.setShortLabel("nopub-2006-1");
        expWithout.setPublication(null);
        expWithout.getXrefs().clear();

        beginTransaction();
        persister.saveOrUpdate(expWithout);
        persister.commit();
        commitTransaction();

        PersisterContext.getInstance().clear();

        Experiment expWith = getMockBuilder().createExperimentRandom(1);
        expWith.setShortLabel("nopub-2006-1");

        beginTransaction();
        persister.saveOrUpdate(expWith);
        persister.commit();
        commitTransaction();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNotNull(reloadedExp.getPublication());
        Assert.assertEquals(1, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, reloadedExp.getInteractions().size());
    }

    @Test
    public void differentExperiment_samePubId() throws Exception {
        final String pubId = "1234567";
        Experiment exp1 = getMockBuilder().createExperimentEmpty("exp-2007-1", pubId);
        exp1.setPublication(null);
        Experiment exp2 = getMockBuilder().createExperimentEmpty("exp-2007-2", pubId);

        PersisterHelper.saveOrUpdate(exp1, exp2);

        beginTransaction();
        Assert.assertNotNull(getDaoFactory().getPublicationDao().getByShortLabel(pubId));

        List<Experiment> experiments = getDaoFactory().getExperimentDao().getByPubId(pubId);
        Assert.assertEquals(2, experiments.size());
        commitTransaction();
    }
}

