package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

/**
 * Range Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.7.1
 */

public class RangeTest {

    @Test
    @Ignore
    // TODO fix the ranges in intact that do have their first AA missing before fixing intact-core and enabling this test  
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
        Range range = new Range( owner, 1, 109, s);
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
}
