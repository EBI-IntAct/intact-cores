package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersisterHelper_ExperimentTest extends IntactBasicTestCase {
    
    @Test
    public void persistExperiment_publication() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005");
        exp.setPublication(getMockBuilder().createPublication("1234567"));

        getCorePersister().saveOrUpdate(exp);

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        Assert.assertNotNull(refreshedExperiment);

        Assert.assertNotNull(refreshedExperiment.getPublication());
        Assert.assertEquals("1234567", refreshedExperiment.getPublication().getShortLabel());
        Assert.assertEquals(1, refreshedExperiment.getInteractions().size());
    }

    @Test
    public void persistExperiment_publication3333() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(10);
        exp.setShortLabel("lala-2005");
        exp.setPublication(getMockBuilder().createPublication("1234567"));

        getCorePersister().saveOrUpdate(exp);

        int initialBioSources = getDaoFactory().getBioSourceDao().countAll();

        BioSource bioSource = getMockBuilder().createBioSource(1234, "lalabug");
        getCorePersister().saveOrUpdate(bioSource);

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        refreshedExperiment.setBioSource(bioSource);

        getCorePersister().saveOrUpdate(refreshedExperiment);

        Assert.assertNotNull(refreshedExperiment);

        Assert.assertNotNull(refreshedExperiment.getPublication());
        Assert.assertEquals("1234567", refreshedExperiment.getPublication().getShortLabel());

        Assert.assertEquals(initialBioSources+1, getDaoFactory().getBioSourceDao().countAll());
    }

    @Test
    public void persistExperiment_assignedLabel() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-2");

        getCorePersister().saveOrUpdate(exp);

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        Assert.assertNotNull(refreshedExperiment);
        Assert.assertEquals(1, refreshedExperiment.getInteractions().size());
    }

    @Test
    public void persistExperiment_institution() throws Exception {
        Institution anotherInstitution = getMockBuilder().createInstitution("IA:0000", "anotherInstitution");
        getCorePersister().saveOrUpdate(anotherInstitution);

        Experiment exp = new IntactMockBuilder(anotherInstitution).createExperimentRandom(1);
        String label = exp.getShortLabel();

        getCorePersister().saveOrUpdate(exp);

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByAc(exp.getAc());
        Assert.assertNotNull(refreshedExperiment);
        Assert.assertEquals(anotherInstitution.getShortLabel(), refreshedExperiment.getOwner().getShortLabel());
    }

    @Test
    public void persistExperiment_similarLabels() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("tata-2005-2");

        Assert.assertEquals(1, exp.getXrefs().size());

        ExperimentXref xref = exp.getXrefs().iterator().next();

        getCorePersister().saveOrUpdate(exp);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        getEntityManager().clear();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("tata-2005");
        xref.setAc(null);
        exp2.getXrefs().clear();
        exp2.addXref(xref);

        getCorePersister().saveOrUpdate(exp2);
        
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        
        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("tata-2005-1");

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, reloadedExperiment.getXrefs().size());
        Assert.assertEquals(1, reloadedExperiment.getInteractions().size());
    }

    @Test
    public void persistExperiment_sameUnassigned() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getExperimentDao().countAll());

        Experiment exp = getMockBuilder().createExperimentEmpty("lefant-2010-1", "unassigned545");
        exp.setBioSource(getMockBuilder().createBioSource(4932, "yeast"));
        exp.addXref(getMockBuilder().createXref(exp, "IMEX-1234",
                getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.IMEX_PRIMARY_MI_REF, CvXrefQualifier.IMEX_PRIMARY),
                getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.IMEX_MI_REF, CvDatabase.IMEX)));

        Interaction i1 = getMockBuilder().createInteractionRandomBinary();
        i1.getExperiments().clear();
        exp.addInteraction(i1);

        Assert.assertEquals(2, exp.getXrefs().size());

        getCorePersister().saveOrUpdate(i1);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Assert.assertEquals("Only one experiment expected after saving one interaction", 1, getDaoFactory().getExperimentDao().countAll());

        Experiment exp2 = getMockBuilder().createExperimentEmpty("lefant-2010-1", "unassigned545");
        exp2.setBioSource(getMockBuilder().createBioSource(4932, "yeast"));
        exp2.addXref(getMockBuilder().createXref(exp2, "IMEX-1234",
                getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.IMEX_PRIMARY_MI_REF, CvXrefQualifier.IMEX_PRIMARY),
                getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.IMEX_MI_REF, CvDatabase.IMEX)));

        Interaction i2 = getMockBuilder().createInteractionRandomBinary();
        i2.getExperiments().clear();
        exp2.addInteraction(i2);

        getCorePersister().saveOrUpdate(i2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals("Only one experiment expected after saving two interactions with the same experiment", 1, getDaoFactory().getExperimentDao().countAll());

        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lefant-2010-1");

        Assert.assertEquals(2, reloadedExperiment.getXrefs().size());

    }

    @Test
    public void updateExperiment_avoidDuplications() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("nopub-2006-1");
        exp.setPublication(null);
        exp.getXrefs().clear();
        exp.addXref(getMockBuilder().createPrimaryReferenceXref(exp, "1234"));

        getCorePersister().saveOrUpdate(exp);

        Experiment reloadedExpBeforeUpdate = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNull(reloadedExpBeforeUpdate.getPublication());
        Assert.assertEquals(1, reloadedExpBeforeUpdate.getXrefs().size());
        Assert.assertEquals(1, reloadedExpBeforeUpdate.getInteractions().size());

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setAc(exp.getAc());
        exp2.setShortLabel("nopub-2006-1");
        exp2.setPublication(getMockBuilder().createPublication("1234"));
        exp2.getXrefs().clear();
        exp2.addXref(getMockBuilder().createPrimaryReferenceXref(exp2, "1234"));

        getCorePersister().saveOrUpdate(exp2);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getPublicationDao().countAll());

        getEntityManager().clear();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNotNull(reloadedExp.getPublication());
        Assert.assertEquals(1, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, reloadedExp.getInteractions().size());
    }

    @Test
    public void existingExperimentWithoutPubInfo() throws Exception {
        TransactionStatus transactionStatus1 = getDataContext().beginTransaction();

        // create an experiment without publication or xrefs

        Experiment expWithout = getMockBuilder().createExperimentRandom(1);
        expWithout.setShortLabel("nopub-2006-1");
        expWithout.setPublication(null);
        expWithout.getXrefs().clear();

        getCorePersister().saveOrUpdate(expWithout);

        getDataContext().commitTransaction(transactionStatus1);
        TransactionStatus transactionStatus2 = getDataContext().beginTransaction();

        Experiment expWith = getMockBuilder().createExperimentRandom(1);
        expWith.setShortLabel("nopub-2006-1");
        expWith.setBioSource(expWithout.getBioSource());
        expWith.setCvInteraction(expWithout.getCvInteraction());
        expWith.setCvIdentification(expWithout.getCvIdentification());
        expWith.setAc(expWithout.getAc());

        Assert.assertNotNull(expWith.getPublication());
        Assert.assertEquals(1, expWith.getXrefs().size());

        getCorePersister().saveOrUpdate(expWith);

        getDataContext().commitTransaction(transactionStatus2);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNotNull(reloadedExp.getPublication());
        Assert.assertEquals(1, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
    }


    @Test
    public void existingExperiment_aliases() throws Exception {
        Experiment expWithout = getMockBuilder().createExperimentRandom(0);
        expWithout.setShortLabel("nopub-2006-1");
        expWithout.setPublication(null);
        expWithout.getXrefs().clear();
        expWithout.getAliases().clear();
        expWithout.getAnnotations().clear();
        expWithout.getInteractions().clear();

        getCorePersister().saveOrUpdate(expWithout);

        Experiment expWith = getMockBuilder().createExperimentRandom(0);
        expWith.setShortLabel("nopub-2006-1");
        expWith.setPublication(null);
        expWith.getXrefs().clear();
        expWith.getAliases().clear();
        expWith.getAnnotations().clear();

        expWith.addAlias( getMockBuilder().createAlias( expWith, "comment", "MI:xxxx", "topic" ) );

        CorePersister persister = getCorePersister();
        persister.setUpdateWithoutAcEnabled(true);
        persister.saveOrUpdate(expWith);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertEquals(1, reloadedExp.getAliases().size());
    }

    @Test
    public void differentExperiment_samePubId() throws Exception {
        final String pubId = "1234567";
        Experiment exp1 = getMockBuilder().createExperimentEmpty("exp-2007-1", pubId);
        exp1.setPublication(null);
        Experiment exp2 = getMockBuilder().createExperimentEmpty("exp-2007-2", pubId);

        getCorePersister().saveOrUpdate(exp1, exp2);

        Assert.assertNotNull(getDaoFactory().getPublicationDao().getByShortLabel(pubId));

        List<Experiment> experiments = getDaoFactory().getExperimentDao().getByPubId(pubId);
        Assert.assertEquals(2, experiments.size());
    }

    @Test
    public void newExperiment_existingInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Interaction loadedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Experiment experiment = getMockBuilder().createExperimentEmpty();
        experiment.addInteraction(loadedInteraction);

        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExperiment = getDaoFactory().getExperimentDao().getByAc(experiment.getAc());
        Assert.assertEquals(1, loadedExperiment.getInteractions().size());

        final Interaction interaction1 = loadedExperiment.getInteractions().iterator().next();

        Assert.assertEquals(2, interaction1.getExperiments().size());
    }

    @Test
    public void existingExperiment_existingInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        Experiment experiment = getMockBuilder().createExperimentEmpty();

        getCorePersister().saveOrUpdate(interaction, experiment);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExperiment = getDaoFactory().getExperimentDao().getByAc(experiment.getAc());
        Interaction loadedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        loadedExperiment.addInteraction(loadedInteraction);

        getCorePersister().saveOrUpdate(loadedExperiment);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByAc(loadedInteraction.getAc());
        Assert.assertEquals(2, reloadedInteraction.getExperiments().size());

        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByAc(loadedExperiment.getAc());
        Assert.assertEquals(1, reloadedExperiment.getInteractions().size());

    }

    @Test
    public void interactionInExpPersistedCorrectly() throws Exception {

        Experiment experiment = getMockBuilder().createExperimentEmpty();
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.getExperiments().clear();
        experiment.addInteraction(interaction);

        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());

        Experiment exp2 = getDaoFactory().getExperimentDao().getByAc(experiment.getAc());

        Assert.assertEquals(interaction, exp2.getInteractions().iterator().next());
    }

    @Test
    public void removingExperiments() throws Exception {

        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        Interaction interactionToDelete = getMockBuilder().createInteractionRandomBinary();
        interactionToDelete.setShortLabel("intToDel");
        interactionToDelete.getExperiments().clear();
        experiment.addInteraction(interactionToDelete);

        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());

        Experiment exp2 = reloadByAc(experiment);

        Assert.assertEquals(2, exp2.getInteractions().size());

        // remove interaction
        exp2.removeInteraction(interactionToDelete);

        Assert.assertEquals(1, exp2.getInteractions().size());

        getCorePersister().saveOrUpdate(exp2);

        refresh(exp2);

        Assert.assertEquals(1, exp2.getInteractions().size());
    }

    @Test
    public void persist_sameYearDifferentPublication() throws Exception {
        int year = 2007;
        Experiment experiment = getMockBuilder().createExperimentEmpty("lala-2007-1", "123");
        experiment.getXrefs().clear();

        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getPublicationDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getXrefDao(ExperimentXref.class).countAll());

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        Experiment expSameYearDiffPub = cloner.clone(experiment);
        expSameYearDiffPub.setShortLabel("lala-2007");
        expSameYearDiffPub.setPublication(getMockBuilder().createPublication("456"));

        getCorePersister().saveOrUpdate(expSameYearDiffPub);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
    }

    @Test
    public void persist_correctLabeling() throws Exception {
        TransactionStatus transactionStatus = getDataContext().beginTransaction();

        final Experiment exp1 = getMockBuilder().createExperimentEmpty("lala-2007", "17560331");
        exp1.getBioSource().setTaxId("1");
        getCorePersister().saveOrUpdate(exp1);

        Assert.assertEquals("lala-2007-1", exp1.getShortLabel());

        final Experiment exp2 = getMockBuilder().createExperimentEmpty("lala-2007", "17560331");
        exp2.getBioSource().setTaxId("2");
        getCorePersister().saveOrUpdate(exp2);
        
        Assert.assertEquals("lala-2007-2", exp2.getShortLabel());

        final Experiment exp3 = getMockBuilder().createExperimentEmpty("lala-2007", "17560331");
        exp3.getBioSource().setTaxId("3");
        getCorePersister().saveOrUpdate(exp3);

        Assert.assertEquals("lala-2007-3", exp3.getShortLabel());

        getDataContext().commitTransaction(transactionStatus);

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        final Experiment exp3Dup = cloner.clone(exp3);
        getCorePersister().saveOrUpdate(exp3Dup);

        Assert.assertEquals("lala-2007-3", exp3Dup.getShortLabel());

        final Experiment exp4 = getMockBuilder().createExperimentEmpty("lala-2007", "17690294");
        exp4.getBioSource().setTaxId("5");
        getCorePersister().saveOrUpdate(exp4);

        Assert.assertEquals("lala-2007a-1", exp4.getShortLabel());

        final Experiment exp5 = getMockBuilder().createExperimentEmpty("lala-2007", "17923091");
        exp5.getBioSource().setTaxId("6");
        getCorePersister().saveOrUpdate(exp5);

        Assert.assertEquals("lala-2007b-1", exp5.getShortLabel());
        
        Assert.assertEquals(5, getDaoFactory().getExperimentDao().countAll());
    }

    @Test
    public void addingInteractionsWithSlightlyTheSameExperiment() throws Exception {
        // this tests that if we add interactions that have the same experiment (different instances),
        // and have one additional annotation (the new experiments all have the same annotations, but they
        // have one more than the existing one), they should be the same.

        // this is based in a real case, don't try this at home!

        BioSource organism = getMockBuilder().createBioSourceRandom();

        Experiment tarassov1 = getMockBuilder().createExperimentEmpty("tarassov-2008-1", "12345");
        tarassov1.setBioSource(organism);
        tarassov1.setPublication(null);
        tarassov1.addAnnotation(getMockBuilder().createAnnotation("annot1", CvTopic.AUTHOR_LIST_MI_REF, CvTopic.AUTHOR_LIST));

        Experiment tarassov2 = getMockBuilder().createExperimentEmpty("tarassov-2008-1", "12345");
        tarassov2.setBioSource(organism);
        tarassov2.addAnnotation(getMockBuilder().createAnnotation("annot1", CvTopic.AUTHOR_LIST_MI_REF, CvTopic.AUTHOR_LIST));
        tarassov2.addAnnotation(getMockBuilder().createAnnotation("annot2", CvTopic.AUTHOR_LIST_MI_REF, CvTopic.AUTHOR_LIST));

        Experiment tarassov3 = getMockBuilder().createExperimentEmpty("tarassov-2008-1", "12345");
        tarassov3.setBioSource(organism);
        tarassov3.addAnnotation(getMockBuilder().createAnnotation("annot1", CvTopic.AUTHOR_LIST_MI_REF, CvTopic.AUTHOR_LIST));
        tarassov3.addAnnotation(getMockBuilder().createAnnotation("annot2", CvTopic.AUTHOR_LIST_MI_REF, CvTopic.AUTHOR_LIST));

        getCorePersister().saveOrUpdate(tarassov1);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getInteractionDao().countAll());
        Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("tarassov-2008-1"));

        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        interaction1.getExperiments().clear();
        interaction1.addExperiment(tarassov2);

        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        interaction2.getExperiments().clear();
        interaction2.addExperiment(tarassov3);
        
        getCorePersister().saveOrUpdate(interaction1, interaction2);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("tarassov-2008-1"));
        Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("tarassov-2008-2"));
    }

    @Test
    public void removeInteractionByAcFromExperiment() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty();
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.getExperiments().clear();
        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        interaction2.getExperiments().clear();
        experiment.addInteraction(interaction);
        experiment.addInteraction(interaction2);

        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getProteinDao().countAll());

        getDaoFactory().getInteractionDao().deleteByAc(interaction2.getAc());

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getProteinDao().countAll());
    }

    private Experiment reloadByAc(Experiment experiment) {
        return getDaoFactory().getExperimentDao().getByAc(experiment.getAc());
    }

    private void refresh(Experiment experiment) {
        getDaoFactory().getExperimentDao().refresh(experiment);
    }
}