package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvCellType;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTissue;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_BioSourceTest extends IntactBasicTestCase {

    @Test
    public void persist_sameBioSource() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        getCorePersister().saveOrUpdate( bs1 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(7, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(14, getDaoFactory().getXrefDao().countAll());

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        getCorePersister().saveOrUpdate( bs2 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(7, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(14, getDaoFactory().getXrefDao().countAll());
    }

    @Test
    public void persist_bioSource_differentTissues() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        getCorePersister().saveOrUpdate( bs1 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(7, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(14, getDaoFactory().getXrefDao().countAll());

        getEntityManager().clear();

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        bs2.setCvTissue(getMockBuilder().createCvObject(CvTissue.class, "IA:xxxx", "blood"));
        getCorePersister().saveOrUpdate( bs2 );

        Assert.assertEquals(2, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(8, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(16, getDaoFactory().getXrefDao().countAll());
    }

    @Test
    public void persist_bioSource_differentTissues_one_persistence() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        bs2.setCvTissue(getMockBuilder().createCvObject(CvTissue.class, "IA:xxxx", "blood"));
        getCorePersister().saveOrUpdate( bs1, bs2 );

        Assert.assertEquals(2, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(8, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(16, getDaoFactory().getXrefDao().countAll());
    }

     @Test
    public void persist_bioSource_withTissues_same() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        bs1.setCvTissue(getMockBuilder().createCvObject(CvTissue.class, "IA:xxxx", "blood"));

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        bs2.setCvTissue(getMockBuilder().createCvObject(CvTissue.class, "IA:xxxx", "blood"));

        getCorePersister().saveOrUpdate( bs1, bs2 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(8, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(15, getDaoFactory().getXrefDao().countAll());
    }

     @Test
    public void persist_bioSource_withTissues_same2() throws Exception {
         BioSource existingOrganism = getMockBuilder().createBioSource( 9606, "human" );

         CvDatabase cabriDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.CABRI_MI_REF, CvDatabase.CABRI);
         CvDatabase intactDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
         CvDatabase newtDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);

         final CvCellType cellType = getMockBuilder().createCvObject(CvCellType.class, "deleted", "deleted");
         cellType.getXrefs().clear();
         cellType.addXref(getMockBuilder().createIdentityXref(cellType, "ACC_10", cabriDb));
         cellType.addXref(getMockBuilder().createIdentityXref(cellType, "IA:0062", intactDb));

         existingOrganism.setCvCellType(cellType);

         getCorePersister().saveOrUpdate(existingOrganism);

         Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
         Assert.assertEquals(9, getDaoFactory().getCvObjectDao().countAll());
         Assert.assertEquals(17, getDaoFactory().getXrefDao().countAll());

         // candidate organism
         BioSource candidateOrganism = getMockBuilder().createBioSource( 9606, "human" );
         candidateOrganism.getXrefs().clear();

         final CvCellType cellType2 = getMockBuilder().createCvObject(CvCellType.class, "deleted", "deleted");
         cellType2.getXrefs().clear();
         cellType2.addXref(getMockBuilder().createIdentityXref(cellType2, "ACC_10", cabriDb));
         cellType2.addXref(getMockBuilder().createIdentityXref(cellType2, "9606", newtDb));

         candidateOrganism.setCvCellType(cellType2);

         getCorePersister().saveOrUpdate(candidateOrganism);

         Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
         Assert.assertEquals(9, getDaoFactory().getCvObjectDao().countAll());
         Assert.assertEquals(17, getDaoFactory().getXrefDao().countAll());
    }


    @Test
    public void persist_alwaysLowerCase() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "HUMAN" );
        getCorePersister().saveOrUpdate( bs1 );

        Assert.assertEquals("human", getDaoFactory().getBioSourceDao().getByTaxonIdUnique("9606").getShortLabel());
    }

}