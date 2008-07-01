package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvTissue;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_BioSourceTest extends IntactBasicTestCase
{

    @Test
    public void persist_sameBioSource() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs1 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getXrefDao().countAll());

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs2 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getXrefDao().countAll());
    }

    @Test
    public void persist_bioSource_differentTissues() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs1 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getXrefDao().countAll());

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        bs2.setCvTissue(getMockBuilder().createCvObject(CvTissue.class, "IA:xxxx", "blood"));
        PersisterHelper.saveOrUpdate( bs2 );

        Assert.assertEquals(2, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(7, getDaoFactory().getXrefDao().countAll());
    }

    @Test
    public void persist_alwaysLowerCase() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "HUMAN" );
        PersisterHelper.saveOrUpdate( bs1 );

        Assert.assertEquals("human", getDaoFactory().getBioSourceDao().getByTaxonIdUnique("9606").getShortLabel());
    }

}