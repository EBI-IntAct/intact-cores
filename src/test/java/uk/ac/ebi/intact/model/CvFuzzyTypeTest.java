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
        Assert.assertTrue( fuzzyType.isNTerminal() );
        Assert.assertFalse( fuzzyType.isCTerminal() );

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

    @Test
    public void isLessThan() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.LESS_THAN_MI_REF, CvFuzzyType.LESS_THAN );
        Assert.assertTrue( fuzzyType.isLessThan() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.LESS_THAN_MI_REF, "foobar" );
        Assert.assertTrue( fuzzyType.isLessThan() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.LESS_THAN );
        Assert.assertTrue( fuzzyType.isLessThan() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isLessThan() );
    }

    @Test
    public void isGreaterThan() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.GREATER_THAN_MI_REF, CvFuzzyType.GREATER_THAN );
        Assert.assertTrue( fuzzyType.isGreaterThan() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.GREATER_THAN_MI_REF, "foobar" );
        Assert.assertTrue( fuzzyType.isGreaterThan() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.GREATER_THAN );
        Assert.assertTrue( fuzzyType.isGreaterThan() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isGreaterThan() );
    }

    @Test
    public void isRange() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.RANGE_MI_REF, CvFuzzyType.RANGE );
        Assert.assertTrue( fuzzyType.isRange() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.RANGE_MI_REF, "foobar" );
        Assert.assertTrue( fuzzyType.isRange() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.RANGE );
        Assert.assertTrue( fuzzyType.isRange() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isRange() );
    }

    @Test
    public void isCertain() throws Exception {
        CvFuzzyType fuzzyType;

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.CERTAIN_MI_REF, CvFuzzyType.CERTAIN );
        Assert.assertTrue( fuzzyType.isCertain() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.CERTAIN_MI_REF, "foobar" );
        Assert.assertTrue( fuzzyType.isCertain() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", CvFuzzyType.CERTAIN );
        Assert.assertTrue( fuzzyType.isCertain() );
        Assert.assertFalse( fuzzyType.isNTerminal() );

        fuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class, "MI:xxxx", "foobar" );
        Assert.assertFalse( fuzzyType.isCertain() );
    }
}
