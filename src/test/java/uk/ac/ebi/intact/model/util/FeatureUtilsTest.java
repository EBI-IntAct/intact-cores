package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.Range;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Sep-2010</pre>
 */

public class FeatureUtilsTest  extends IntactBasicTestCase {

    @Test
    public void testRange_valid_without_Protein_sequence(){

        Range range = getMockBuilder().createRange(1, 1, 3, 3);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));

        Assert.assertFalse(FeatureUtils.isABadRange(range, null));
    }

    @Test
    public void testRange_valid_with_Protein_sequence(){

        Range range = getMockBuilder().createRange(1, 1, 3, 3);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));

        Assert.assertFalse(FeatureUtils.isABadRange(range, "AAGCTTPPM"));
    }

    @Test
    public void testRange_overlapping_Protein_sequence_1(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(1, 1, 20, 20);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_overlapping_Protein_sequence_2(){

        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(19, 19, 20, 20);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0335", "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_undetermined_nterminal(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(0, 0, 1, 1);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "undetermined"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "n-terminal"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_undetermined_cterminal(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(9, 9, 0, 0);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "undetermined"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_cterminal_nterminal(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(9, 9, 1, 1);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "n-terminal"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_certain_nterminal(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(3, 3, 1, 1);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "n-terminal"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_cterminal_certain(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(9, 9, 3, 3);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_greaterThan_lessThan_1(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 5, 5);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "greater-than"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "less-than"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_greaterThan_lessThan_2(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 4, 4);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "greater-than"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "less-than"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_greaterThan_certain(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 4, 4);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "greater-than"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_lessThan_certain(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 4, 4);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "less-than"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_certain_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(0, 0, 4, 4);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_certain_position_overlapping(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 2, 2);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_valid_range_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 5, 7, 8);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "range"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "range"));

        Assert.assertFalse(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_range_position_overlapping(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(5, 4, 7, 8);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "range"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "range"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_certain_position_withIntervals(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 5, 7, 7);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_undetermined_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 7, 7);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "undetermined"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_nTerminal_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 7, 7);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "n-terminal"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_cTerminal_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 7, 7);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_greaterThan_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(4, 4, 9, 9);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "greater-than"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_lessThan_position(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(1, 1, 7, 7);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "less-than"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_bad_cterminal_position_0(){
        String seq = "AAGCTTPPM";

        Range range = getMockBuilder().createRange(1, 1, 0, 0);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertTrue(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_valid_cterminal_position_Without_sequence(){
        String seq = null;

        Range range = getMockBuilder().createRange(1, 1, 1, 1);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));

        Assert.assertFalse(FeatureUtils.isABadRange(range, seq));
    }

    @Test
    public void testRange_valid_cterminal_position_sequence_null(){
        String seq = null;

        Range range = getMockBuilder().createRange(1, 1, 10,10);
        range.setFromCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "certain"));
        range.setToCvFuzzyType(getMockBuilder().createCvObject(CvFuzzyType.class, null, "c-terminal"));

        System.out.println(FeatureUtils.getBadRangeInfo(range, seq));
        Assert.assertFalse(FeatureUtils.isABadRange(range, seq));
    }
}
