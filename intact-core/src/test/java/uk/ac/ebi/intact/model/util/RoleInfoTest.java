package uk.ac.ebi.intact.model.util;

import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.CvBiologicalRole;
import uk.ac.ebi.intact.model.CvExperimentalRole;

/**
 * RoleInfo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version
 */
public class RoleInfoTest {

    ////////////////////////////////
    // Compatibility with JUnit 3

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter( RoleInfoTest.class );
    }

    //////////////////////////
    // Initialisation

    static CvExperimentalRole bait;
    static CvExperimentalRole unspecified_exp;
    static CvBiologicalRole enzyme;
    static CvBiologicalRole unspecified_biol;

    @BeforeClass
    public static void setUp() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        bait = mockBuilder.createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        unspecified_exp = mockBuilder.createCvObject( CvExperimentalRole.class, CvExperimentalRole.UNSPECIFIED_PSI_REF, CvExperimentalRole.UNSPECIFIED );
        enzyme = mockBuilder.createCvObject( CvBiologicalRole.class, CvBiologicalRole.ENZYME_PSI_REF, CvBiologicalRole.ENZYME );
        unspecified_biol = mockBuilder.createCvObject( CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        bait = unspecified_exp = null;
        enzyme = unspecified_biol = null;
    }

    ////////////////////
    // Tests  

    @Test
    public void getBiologicalRole() throws Exception {
        RoleInfo ri = new RoleInfo( unspecified_biol, unspecified_exp );
        Assert.assertEquals( unspecified_biol, ri.getBiologicalRole() );
    }

    @Test
    public void getExperimentalRole() throws Exception {
        RoleInfo ri = new RoleInfo( unspecified_biol, unspecified_exp );
        Assert.assertEquals( unspecified_exp, ri.getExperimentalRole() );
    }

    @Test
    public void getBiologicalRoleMi() throws Exception {
        RoleInfo ri = new RoleInfo( unspecified_biol, unspecified_exp );
        Assert.assertEquals( CvBiologicalRole.UNSPECIFIED_PSI_REF, ri.getBiologicalRoleMi() );
    }

    @Test
    public void getExperimentalRoleMi() throws Exception {
        RoleInfo ri = new RoleInfo( unspecified_biol, unspecified_exp );
        Assert.assertEquals( CvExperimentalRole.UNSPECIFIED_PSI_REF, ri.getExperimentalRoleMi() );
    }

    @Test
    public void getRelevantName() throws Exception {
        RoleInfo ri;

        ri = new RoleInfo( unspecified_biol, unspecified_exp );
        Assert.assertEquals( CvExperimentalRole.UNSPECIFIED, ri.getRelevantName() );

        ri = new RoleInfo( unspecified_biol, bait );
        Assert.assertEquals( CvExperimentalRole.BAIT, ri.getRelevantName() );

        ri = new RoleInfo( enzyme, unspecified_exp );
        Assert.assertEquals( CvExperimentalRole.ENZYME, ri.getRelevantName() );

        ri = new RoleInfo( enzyme, bait );
        Assert.assertEquals( CvExperimentalRole.BAIT + "/" + CvExperimentalRole.ENZYME, ri.getRelevantName() );
    }

    @Test
    public void getRelevantMi() throws Exception {
        RoleInfo ri;

        ri = new RoleInfo( unspecified_biol, unspecified_exp );
        Assert.assertEquals( CvExperimentalRole.UNSPECIFIED_PSI_REF, ri.getRelevantMi() );

        ri = new RoleInfo( unspecified_biol, bait );
        Assert.assertEquals( CvExperimentalRole.BAIT_PSI_REF, ri.getRelevantMi() );

        ri = new RoleInfo( enzyme, unspecified_exp );
        Assert.assertEquals( CvExperimentalRole.ENZYME_PSI_REF, ri.getRelevantMi() );

        ri = new RoleInfo( enzyme, bait );
        Assert.assertEquals( CvExperimentalRole.BAIT_PSI_REF + "/" + CvExperimentalRole.ENZYME_PSI_REF, ri.getRelevantMi() );
    }
}
