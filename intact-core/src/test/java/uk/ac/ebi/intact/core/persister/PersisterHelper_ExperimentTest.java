package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.clone.IntactCloner;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_ExperimentTest extends IntactBasicTestCase
{
    @Test
    public void persistExperiment_publication() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005");
        exp.setPublication(getMockBuilder().createPublication("1234567"));

        PersisterHelper.saveOrUpdate(exp);

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        assertNotNull(refreshedExperiment);

        assertNotNull(refreshedExperiment.getPublication());
        assertEquals("1234567", refreshedExperiment.getPublication().getShortLabel());
        assertEquals(1, refreshedExperiment.getInteractions().size());
    }

    @Test
    public void persistExperiment_assignedLabel() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-2");

        PersisterHelper.saveOrUpdate(exp);

        System.out.println(getDaoFactory().getExperimentDao().getAll());

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        assertNotNull(refreshedExperiment);
        assertEquals(1, refreshedExperiment.getInteractions().size());
    }

    @Test
    public void persistExperiment_institution() throws Exception {
        Institution anotherInstitution = getMockBuilder().createInstitution("IA:0000", "anotherInstitution");
        PersisterHelper.saveOrUpdate(anotherInstitution);

        Experiment exp = new IntactMockBuilder(anotherInstitution).createExperimentRandom(1);
        String label = exp.getShortLabel();

        PersisterHelper.saveOrUpdate(exp);

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByAc(exp.getAc());
        assertNotNull(refreshedExperiment);
        Assert.assertEquals(anotherInstitution.getShortLabel(), refreshedExperiment.getOwner().getShortLabel());
    }

    @Test
    public void persistExperiment_similarLabels() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("tata-2005-2");

        Assert.assertEquals(1, exp.getXrefs().size());

        ExperimentXref xref = exp.getXrefs().iterator().next();

        PersisterHelper.saveOrUpdate(exp);

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("tata-2005");
        xref.setAc(null);
        exp2.getXrefs().clear();
        exp2.addXref(xref);

        PersisterHelper.saveOrUpdate(exp2);

        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("tata-2005-1");

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, reloadedExperiment.getXrefs().size());
        Assert.assertEquals(1, reloadedExperiment.getInteractions().size());
    }

    @Test
    public void updateExperiment_avoidDuplications() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("nopub-2006-1");
        exp.setPublication(null);
        exp.getXrefs().clear();
        exp.addXref(getMockBuilder().createPrimaryReferenceXref(exp, "1234"));

        PersisterHelper.saveOrUpdate(exp);

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

        PersisterHelper.saveOrUpdate(exp2);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getPublicationDao().countAll());

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNotNull(reloadedExp.getPublication());
        Assert.assertEquals(1, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, reloadedExp.getInteractions().size());
    }

    @Test
    public void existingExperimentWithoutPubInfo() throws Exception {

        // create an experiment without publication or xrefs

        Experiment expWithout = getMockBuilder().createExperimentRandom(1);
        expWithout.setShortLabel("nopub-2006-1");
        expWithout.setPublication(null);
        expWithout.getXrefs().clear();

        PersisterHelper.saveOrUpdate(expWithout);

        Experiment expWith = getMockBuilder().createExperimentRandom(1);
        expWith.setShortLabel("nopub-2006-1");
        expWith.setBioSource(expWithout.getBioSource());
        expWith.setCvInteraction(expWithout.getCvInteraction());
        expWith.setCvIdentification(expWithout.getCvIdentification());
        expWith.setAc(expWithout.getAc());

        Assert.assertNotNull(expWith.getPublication());
        Assert.assertEquals(1, expWith.getXrefs().size());

        PersisterHelper.saveOrUpdate(expWith);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertNotNull(reloadedExp.getPublication());
        Assert.assertEquals(1, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void existingExperiment_annotations() throws Exception {
        Experiment expWithout = getMockBuilder().createExperimentRandom(1);
        expWithout.setShortLabel("nopub-2006-1");
        expWithout.setPublication(null);
        expWithout.getXrefs().clear();
        expWithout.getAliases().clear();
        expWithout.getAnnotations().clear();

        PersisterHelper.saveOrUpdate(expWithout);

        Experiment expWith = getMockBuilder().createExperimentRandom(1);
        expWith.setShortLabel("nopub-2006-1");
        expWith.setPublication(null);
        expWith.getXrefs().clear();
        expWith.getAliases().clear();
        expWith.getAnnotations().clear();

        expWith.addAnnotation( getMockBuilder().createAnnotation( "comment", "MI:xxxx", "topic" ) );

        PersisterHelper.saveOrUpdate(expWith);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel("nopub-2006-1");
        Assert.assertEquals(1, reloadedExp.getAnnotations().size());
    }

    @Test
    public void existingExperiment_aliases() throws Exception {
        Experiment expWithout = getMockBuilder().createExperimentRandom(1);
        expWithout.setShortLabel("nopub-2006-1");
        expWithout.setPublication(null);
        expWithout.getXrefs().clear();
        expWithout.getAliases().clear();
        expWithout.getAnnotations().clear();

        PersisterHelper.saveOrUpdate(expWithout);

        Experiment expWith = getMockBuilder().createExperimentRandom(1);
        expWith.setShortLabel("nopub-2006-1");
        expWith.setPublication(null);
        expWith.getXrefs().clear();
        expWith.getAliases().clear();
        expWith.getAnnotations().clear();

        expWith.addAlias( getMockBuilder().createAlias( expWith, "comment", "MI:xxxx", "topic" ) );

        PersisterHelper.saveOrUpdate(expWith);

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

        PersisterHelper.saveOrUpdate(exp1, exp2);

        Assert.assertNotNull(getDaoFactory().getPublicationDao().getByShortLabel(pubId));

        List<Experiment> experiments = getDaoFactory().getExperimentDao().getByPubId(pubId);
        Assert.assertEquals(2, experiments.size());
    }

    @Test
    public void newExperiment_existingInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Interaction loadedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Experiment experiment = getMockBuilder().createExperimentEmpty();
        experiment.addInteraction(loadedInteraction);

        PersisterHelper.saveOrUpdate(experiment);

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

        PersisterHelper.saveOrUpdate(interaction, experiment);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExperiment = getDaoFactory().getExperimentDao().getByAc(experiment.getAc());
        Interaction loadedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        loadedExperiment.addInteraction(loadedInteraction);

        PersisterHelper.saveOrUpdate(loadedExperiment);

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

        PersisterHelper.saveOrUpdate(experiment);

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

        PersisterHelper.saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());

        Experiment exp2 = reloadByAc(experiment);

        Assert.assertEquals(2, exp2.getInteractions().size());

        // remove interaction
        exp2.removeInteraction(interactionToDelete);

        Assert.assertEquals(1, exp2.getInteractions().size());

        PersisterHelper.saveOrUpdate(exp2);

        refresh(exp2);

        Assert.assertEquals(1, exp2.getInteractions().size());
    }

    @Test
    public void persist_sameYearDifferentPublication() throws Exception {
        int year = 2007;
        Experiment experiment = getMockBuilder().createExperimentEmpty("lala-2007-1", "123");
        experiment.getXrefs().clear();

        PersisterHelper.saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getPublicationDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getXrefDao(ExperimentXref.class).countAll());

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        Experiment expSameYearDiffPub = cloner.clone(experiment);
        expSameYearDiffPub.setShortLabel("lala-2007");
        expSameYearDiffPub.setPublication(getMockBuilder().createPublication("456"));

        PersisterHelper.saveOrUpdate(expSameYearDiffPub);

        System.out.println(getDaoFactory().getExperimentDao().getAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
    }

    @Test
    public void persist_correctLabeling() throws Exception {
        final Experiment exp1 = getMockBuilder().createExperimentEmpty("lala-2007", "17560331");
        exp1.getBioSource().setTaxId("1");
        PersisterHelper.saveOrUpdate(exp1);

        Assert.assertEquals("lala-2007-1", exp1.getShortLabel());

        final Experiment exp2 = getMockBuilder().createExperimentEmpty("lala-2007", "17560331");
        exp2.getBioSource().setTaxId("2");
        PersisterHelper.saveOrUpdate(exp2);
        
        Assert.assertEquals("lala-2007-2", exp2.getShortLabel());

        final Experiment exp3 = getMockBuilder().createExperimentEmpty("lala-2007", "17560331");
        exp3.getBioSource().setTaxId("3");
        PersisterHelper.saveOrUpdate(exp3);

        Assert.assertEquals("lala-2007-3", exp3.getShortLabel());

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        final Experiment exp3Dup = cloner.clone(exp3);
        PersisterHelper.saveOrUpdate(exp3Dup);

        Assert.assertEquals("lala-2007-3", exp3Dup.getShortLabel());

        final Experiment exp4 = getMockBuilder().createExperimentEmpty("lala-2007", "17690294");
        exp4.getBioSource().setTaxId("5");
        PersisterHelper.saveOrUpdate(exp4);

        Assert.assertEquals("lala-2007a-1", exp4.getShortLabel());

        final Experiment exp5 = getMockBuilder().createExperimentEmpty("lala-2007", "17923091");
        exp5.getBioSource().setTaxId("6");
        PersisterHelper.saveOrUpdate(exp5);

        Assert.assertEquals("lala-2007b-1", exp5.getShortLabel());
        
        Assert.assertEquals(5, getDaoFactory().getExperimentDao().countAll());

    }



    private Experiment reloadByAc(Experiment experiment) {
        return getDaoFactory().getExperimentDao().getByAc(experiment.getAc());
    }

    private void refresh(Experiment experiment) {
        getDaoFactory().getExperimentDao().refresh(experiment);
    }
}