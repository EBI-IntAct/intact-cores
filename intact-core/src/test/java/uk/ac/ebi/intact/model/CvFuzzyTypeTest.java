package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * CvFuzzyType Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.7.1
 */
public class CvFuzzyTypeTest extends IntactBasicTestCase {

    @Test
    public void isCTerminal() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.C_TERMINAL_MI_REF, CvFuzzyType.C_TERMINAL );
        Assert.assertTrue( fuzzyType.isCTerminal() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.C_TERMINAL_MI_REF, "foobar" );
        Assert.assertTrue( fuzzyType.isCTerminal() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.C_TERMINAL );
        Assert.assertTrue( fuzzyType.isCTerminal() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isCTerminal() );
    }

    @Test
    public void isNTerminal() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.N_TERMINAL_MI_REF, CvFuzzyType.N_TERMINAL );
        Assert.assertTrue( fuzzyType.isNTerminal() );
        Assert.assertFalse( fuzzyType.isCTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.N_TERMINAL_MI_REF, "foobar" );
        Assert.assertFalse( fuzzyType.isNTerminal() );
        Assert.assertTrue( fuzzyType.isCTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.N_TERMINAL );
        Assert.assertTrue( fuzzyType.isNTerminal() );
        Assert.assertFalse( fuzzyType.isCTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isNTerminal() );
    }

    @Test
    public void isUnertermined() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.UNDETERMINED_MI_REF, CvFuzzyType.UNDETERMINED );
        Assert.assertTrue( fuzzyType.isUndetermined() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.UNDETERMINED_MI_REF, "foobar" );
        Assert.assertTrue( fuzzyType.isUndetermined() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.UNDETERMINED );
        Assert.assertTrue( fuzzyType.isUndetermined() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isUndetermined() );
    }
}
