package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

/**
 * Range Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.7.1
 */

public class RangeTest extends IntactBasicTestCase {

    @Test
    public void prepareSequence() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String seq = "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG";
        Range range = new Range( owner, 2, 5, seq);
        Assert.assertTrue( range.getFullSequence().startsWith( "QTIK" ));

        range.prepareSequence( "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" );
        Assert.assertTrue( range.getFullSequence().startsWith( "AVGK" ));

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.RANGE_MI_REF,
                CvFuzzyType.RANGE );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";

        range.prepareSequence( s );
        Assert.assertEquals( 110, s.length() );
        Assert.assertTrue( "Range's internal sequence should start with: 'AVGK' and not: " + range.getFullSequence(),
                range.getFullSequence().startsWith( "AVGK" ));
    }

    @Test
    public void prepareSequenceWithMoreThan100AA() throws Exception {
        final Institution owner = new Institution( "ebi" );
        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 109, null);

        CvFuzzyType certain = getMockBuilder().createCvObject(CvFuzzyType.class, CvFuzzyType.CERTAIN_MI_REF, CvFuzzyType.CERTAIN);
        range.setFromCvFuzzyType(certain);
        range.setToCvFuzzyType(certain);
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertTrue( range.getFullSequence().startsWith( "GAV" ));
        Assert.assertEquals( s.substring(0, 109), range.getFullSequence());
        Assert.assertEquals(null, range.getUpStreamSequence());
        Assert.assertEquals( s.substring(109, 110), range.getDownStreamSequence());
    }

    @Test
    public void prepareSequenceWithCTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 100, 110, s);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.C_TERMINAL_MI_REF,
                CvFuzzyType.C_TERMINAL );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        range.setFromCvFuzzyType( rangeFuzzyType2 );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(99, 110), range.getFullSequence());
        Assert.assertEquals( s.substring(59, 99), range.getUpStreamSequence());
        Assert.assertEquals( null, range.getDownStreamSequence());
    }

    @Test
    public void prepareSequenceWithNTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 109, s);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.N_TERMINAL_MI_REF,
                CvFuzzyType.N_TERMINAL );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(109, 110), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(0, 109), range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_undetermined_undetermined(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 0, 0, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.UNDETERMINED_MI_REF,
                CvFuzzyType.UNDETERMINED );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.UNDETERMINED_MI_REF,
                CvFuzzyType.UNDETERMINED );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( null, range.getDownStreamSequence());
        Assert.assertEquals( null, range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_undetermined_certain(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 0, 4, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.UNDETERMINED_MI_REF,
                CvFuzzyType.UNDETERMINED );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(4, 44), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(0, 4), range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_undetermined_cTerminal(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 0, 0, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.UNDETERMINED_MI_REF,
                CvFuzzyType.UNDETERMINED );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                null,
                CvFuzzyType.C_TERMINAL_REGION );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( null, range.getDownStreamSequence());
        Assert.assertEquals( null, range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_nTerminal_undetermined(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 0, 0, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                null,
                CvFuzzyType.N_TERMINAL_REGION );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.UNDETERMINED_MI_REF,
                CvFuzzyType.UNDETERMINED );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( null, range.getDownStreamSequence());
        Assert.assertEquals( null, range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_nTerminal_certain(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 4, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.N_TERMINAL_MI_REF,
                CvFuzzyType.N_TERMINAL );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(4, 44), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(0,4), range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_certain_cTerminal(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 90, 110, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.C_TERMINAL_MI_REF,
                CvFuzzyType.C_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( null, range.getDownStreamSequence());
        Assert.assertEquals( s.substring(89,110), range.getFullSequence());
        Assert.assertEquals( s.substring(49, 89), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_range_1(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 2, 2, 4, 4, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.RANGE_MI_REF,
                CvFuzzyType.RANGE );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.RANGE_MI_REF,
                CvFuzzyType.RANGE );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(4, 43), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(1,4), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 1), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_range_2(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 2, 3, 5, 6, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.RANGE_MI_REF,
                CvFuzzyType.RANGE );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.RANGE_MI_REF,
                CvFuzzyType.RANGE );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(6, 45), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(1,6), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 1), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_greaterThan_greaterThan(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 20, 20, 20, 20, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN_MI_REF,
                CvFuzzyType.GREATER_THAN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN_MI_REF,
                CvFuzzyType.GREATER_THAN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( null, range.getDownStreamSequence());
        Assert.assertEquals( s.substring(20,110), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 20), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_lessThan_lessThan(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 20, 20, 20, 20, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.LESS_THAN_MI_REF,
                CvFuzzyType.LESS_THAN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.LESS_THAN_MI_REF,
                CvFuzzyType.LESS_THAN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(19, 59), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(0,19), range.getFullSequence());
        Assert.assertEquals( null, range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_greaterThan_lessThan(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 20, 20, 40, 40, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN_MI_REF,
                CvFuzzyType.GREATER_THAN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.LESS_THAN_MI_REF,
                CvFuzzyType.LESS_THAN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(39, 59), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(20,39), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 20), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_lessThan_greaterThan(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 20, 20, 40, 40, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.LESS_THAN_MI_REF,
                CvFuzzyType.LESS_THAN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN_MI_REF,
                CvFuzzyType.GREATER_THAN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(41, 63), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(18,41), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 18), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_certain_lessThan(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 17, 17, 20, 20, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.LESS_THAN_MI_REF,
                CvFuzzyType.LESS_THAN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(19, 43), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(16,19), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 16), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_lessThan_certain(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 17, 17, 20, 20, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.LESS_THAN_MI_REF,
                CvFuzzyType.LESS_THAN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(20, 45), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(15,20), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 15), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_certain_greaterThan(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 17, 17, 20, 20, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN_MI_REF,
                CvFuzzyType.GREATER_THAN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(21, 45), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(16,21), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 16), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_greaterThan_certain(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 17, 17, 20, 20, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN_MI_REF,
                CvFuzzyType.GREATER_THAN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(20, 43), range.getDownStreamSequence());
        Assert.assertEquals( s.substring(17,20), range.getFullSequence());
        Assert.assertEquals( s.substring(0, 17), range.getUpStreamSequence());
    }

    @Test
    public void prepareSequence_certain_cTerminal_WithPosition0(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 90, 0, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.CERTAIN_MI_REF,
                CvFuzzyType.CERTAIN );
        CvFuzzyType rangeFuzzyType2 = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.C_TERMINAL_MI_REF,
                CvFuzzyType.C_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType2 );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( 110, range.getToIntervalStart() );
        Assert.assertEquals( 110, range.getToIntervalEnd() );
        Assert.assertEquals( null, range.getDownStreamSequence());
        Assert.assertEquals( s.substring(89,110), range.getFullSequence());
        Assert.assertEquals( s.substring(49, 89), range.getUpStreamSequence());
    }
}
