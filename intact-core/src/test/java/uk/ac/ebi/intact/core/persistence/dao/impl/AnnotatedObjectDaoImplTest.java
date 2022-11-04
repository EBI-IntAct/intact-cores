package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

import java.util.List;

/**
 * AnnotatedObjectDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class AnnotatedObjectDaoImplTest extends IntactBasicTestCase {

    @Test
    public void getByXrefLike() throws Exception {
        final Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        final Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        interaction.getXrefs().clear();
        interaction.addXref( getMockBuilder().createIdentityXref( interaction, "IM-1-2", getMockBuilder().getPsiMiDatabase() ) );
        getCorePersister().saveOrUpdate( interaction, interaction2 );

        final InteractionDao dao = getIntactContext().getDataContext().getDaoFactory().getInteractionDao();
        final List<InteractionImpl> list = dao.getByXrefLike( "IM-1%" );
        Assert.assertNotNull( list );
        Assert.assertEquals( 1, list.size());
    }

    @Test
    public void getByXref_primaryId() throws Exception {
        final Protein protein = getMockBuilder().createProtein("P12345", "test");
        Assert.assertEquals(1, protein.getXrefs().size());
        Xref x = protein.getXrefs().iterator().next();
        protein.addXref(new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()));

        final Protein protein2 = getMockBuilder().createProtein("Q99999", "test2");

        getCorePersister().saveOrUpdate(protein, protein2);

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size());

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike("P12345");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("test", list.iterator().next().getShortLabel());

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike("P12121").size());
    }

    @Test
    public void getByXref_primaryId_database() throws Exception {
        final Protein protein = getMockBuilder().createProtein("P12345", "test");
        Assert.assertEquals(1, protein.getXrefs().size());
        Xref x = protein.getXrefs().iterator().next();
        CvDatabase uniprot = x.getCvDatabase();
        protein.addXref(new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()));

        final Protein protein2 = getMockBuilder().createProtein("Q99999", "test2");

        getCorePersister().saveOrUpdate(protein, protein2);

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size());

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike(uniprot, "P12345");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("test", list.iterator().next().getShortLabel());

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(uniprot, "P12121").size());
        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(intact, "P12345").size());

    }

    @Test
    public void getByXref_primaryId_database_qualifier() throws Exception {
        final Protein protein = getMockBuilder().createProtein("P12345", "test");
        Assert.assertEquals(1, protein.getXrefs().size());
        Xref x = protein.getXrefs().iterator().next();
        CvDatabase uniprot = x.getCvDatabase();
        CvXrefQualifier identity = x.getCvXrefQualifier();
        protein.addXref(new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()));

        final Protein protein2 = getMockBuilder().createProtein("Q99999", "test2");

        getCorePersister().saveOrUpdate(protein, protein2, identity, uniprot);
        getCorePersister().commit();

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size());

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike(uniprot, identity, "P12345");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("test", list.iterator().next().getShortLabel());

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(uniprot, identity, "P12121").size());
        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        CvXrefQualifier secondary = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(uniprot, secondary, "P12345").size());
        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(intact, identity, "P12345").size());
    }

    @Test
    public void getByXref_primaryId_database_qualifier_mi_only() throws Exception {
        final Protein protein = getMockBuilder().createProtein("P12345", "test");
        Assert.assertEquals(1, protein.getXrefs().size());
        Xref x = protein.getXrefs().iterator().next();
        protein.addXref(new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()));

        final Protein protein2 = getMockBuilder().createProtein("Q99999", "test2");

        getCorePersister().saveOrUpdate(protein, protein2);
        getCorePersister().commit();

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size());

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike(CvDatabase.UNIPROT_MI_REF,
                CvXrefQualifier.IDENTITY_MI_REF,
                "P12345");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("test", list.iterator().next().getShortLabel());

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(CvDatabase.UNIPROT_MI_REF,
                CvXrefQualifier.IDENTITY_MI_REF,
                "P12121").size());

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(CvDatabase.UNIPROT_MI_REF,
                CvXrefQualifier.SECONDARY_AC_MI_REF,
                "P12345").size());

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike(CvDatabase.INTACT_MI_REF,
                CvXrefQualifier.IDENTITY_MI_REF,
                "P12345").size());
    }

    @Test
    public void getByShortlabelLike() throws Exception {
        getIntactContext().getConfig().setAutoUpdateExperimentLabel(false);
        Experiment exp1 = getMockBuilder().createExperimentEmpty("lala-2011-1");
        Experiment exp2 = getMockBuilder().createExperimentEmpty("lala-2011-5");

        getCorePersister().saveOrUpdate(exp1, exp2);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().getByShortLabelLike("lala-2011-%").size());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().getByShortLabelLike("lala-2011-1").size());
        Assert.assertEquals(0, getDaoFactory().getExperimentDao().getByShortLabelLike("lolo-2011-%").size());
    }

    @Test
    public void getByInstitutionAc() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setShortLabel("lalala");
        getCorePersister().saveOrUpdate(interaction);

        String institutionAc = getIntactContext().getInstitution().getAc();
        Assert.assertEquals("lalala", getDaoFactory().getInteractionDao().getByInstitutionAc(institutionAc, 0, Integer.MAX_VALUE)
                .iterator().next().getShortLabel());

    }

    @Test
    public void countByInstitutionAc() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        getCorePersister().saveOrUpdate(interaction);

        String institutionAc = getIntactContext().getInstitution().getAc();
        Assert.assertEquals(1L, getDaoFactory().getInteractionDao().countByInstitutionAc(institutionAc));
        Assert.assertEquals(2L, getDaoFactory().getProteinDao().countByInstitutionAc(institutionAc));
        Assert.assertEquals(1L, getDaoFactory().getExperimentDao().countByInstitutionAc(institutionAc));

    }

    @Test
    public void replaceInstitution() throws Exception {
        Institution sourceInstitution = getMockBuilder().createInstitution("IA:xxx1", "Lala Source Institute");
        Institution destInstitution = getMockBuilder().createInstitution("IA:xxx2", "Lala Destination Institute");

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setOwner(sourceInstitution);
        getCorePersister().saveOrUpdate(destInstitution, interaction);

        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        Assert.assertEquals(sourceInstitution.getAc(), reloadedInteraction.getOwner().getAc());

        int updatedCount = getDaoFactory().getInteractionDao().replaceInstitution(sourceInstitution, destInstitution);

        Assert.assertEquals(1, updatedCount);

        getEntityManager().flush();
        getEntityManager().clear();


        Interaction reloadedInteraction2 = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction2.getOwner().getAc());

    }

    @Test
    public void replaceInstitutionForCreator() throws Exception {
        Institution sourceInstitution = getMockBuilder().createInstitution("IA:xxx1", "Lala Source Institute");
        Institution destInstitution = getMockBuilder().createInstitution("IA:xxx2", "Lala Destination Institute");

        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        interaction1.setOwner(sourceInstitution);
        interaction1.setCreator("CREATOR 1");

        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        interaction2.setOwner(sourceInstitution);
        interaction2.setCreator("CREATOR 2");

        getCorePersister().saveOrUpdate(destInstitution, interaction1, interaction2);

        Interaction reloadedInteraction1 = getDaoFactory().getInteractionDao().getByAc(interaction1.getAc());
        Assert.assertEquals(sourceInstitution.getAc(), reloadedInteraction1.getOwner().getAc());
        Assert.assertEquals("CREATOR 1", reloadedInteraction1.getCreator());

        Interaction reloadedInteraction2 = getDaoFactory().getInteractionDao().getByAc(interaction2.getAc());
        Assert.assertEquals(sourceInstitution.getAc(), reloadedInteraction2.getOwner().getAc());
        Assert.assertEquals("CREATOR 2", reloadedInteraction2.getCreator());

        int updatedCount = getDaoFactory().getInteractionDao().replaceInstitution(destInstitution, "CREATOR 1");

        Assert.assertEquals(1, updatedCount);

        getEntityManager().flush();
        getEntityManager().clear();


        Interaction reloadedInteraction1bis = getDaoFactory().getInteractionDao().getByAc(reloadedInteraction1.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction1bis.getOwner().getAc());

        Interaction reloadedInteraction2bis = getDaoFactory().getInteractionDao().getByAc(reloadedInteraction2.getAc());
        Assert.assertEquals(sourceInstitution.getAc(), reloadedInteraction2bis.getOwner().getAc());

    }
}
