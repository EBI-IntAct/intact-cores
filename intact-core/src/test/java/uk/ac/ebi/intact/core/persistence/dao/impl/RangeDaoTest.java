package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persistence.dao.RangeDao;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Jun-2010</pre>
 */

public class RangeDaoTest extends IntactBasicTestCase {

    @Autowired
    private PersisterHelper persisterHelper;

    @Autowired
    private RangeDao rangeDao;

    @Test
    public void prepareFeatureWithoutRangePositions() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        Range range = new Range( owner, 0, 0, null);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.N_TERMINAL_MI_REF,
                CvFuzzyType.N_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );

        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
        feature.addRange(range);
        getCorePersister().saveOrUpdate(feature);

        System.out.println(range.getAc());

        Assert.assertNull( range.getUpStreamSequence() );
        Assert.assertNull( range.getDownStreamSequence() );
        Assert.assertNull( range.getFullSequence() );
    }

    @Test
    public void prepareSequenceWithMoreThan100AA() throws Exception {
        final Institution owner = new Institution( "ebi" );
        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 109, null);
        CvFuzzyType certain = getMockBuilder().createCvObject(CvFuzzyType.class, "MI:0355", "certain");
        range.setFromCvFuzzyType(certain);
        range.setToCvFuzzyType(certain);

        range.prepareSequence(s);

        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
        feature.getRanges().clear();
        feature.addRange(range);
        getCorePersister().saveOrUpdate(feature);
        String ac = range.getAc();

        Range r = this.rangeDao.getByAc(ac);

        Assert.assertEquals( 110, s.length() );
        Assert.assertTrue( r.getFullSequence().startsWith( "GAV" ));
        Assert.assertEquals( s.substring(0, 109), r.getFullSequence());
        Assert.assertEquals(null, r.getUpStreamSequence());
        Assert.assertEquals( s.substring(109, 110), r.getDownStreamSequence());
    }

    @Test
    public void prepareSequenceWithNTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        String s = "MAEKVNNFPPLPKFIPLKPCFYQDFEADIPPQHVSMTKRLYYLWMLNSVTLAVNLVGCLAWLIGGGGATNFGLAFLWLILFTPCSYVCWFRPIYKAFKTDSSFSFMAFFFTFMAQLVISIIQAVGIPGWGVCGWIATISFFGTNIGSAVVMLIPTVMFTVMAVFSFIALSMVHKFYRGSGGSFSKAQEEWTTGAWKNPHVQQAAQNAAMGAAQGAMNQPQTQYSATPNYTYSNEM";
        Range range = new Range( owner, 1, 1, null);
        CvFuzzyType rangeFuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class,
        CvFuzzyType.N_TERMINAL_MI_REF,
        CvFuzzyType.N_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);
        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
        feature.getRanges().clear();
        feature.addRange(range);
        getCorePersister().saveOrUpdate(feature);
        String ac = range.getAc();

        Range r = this.rangeDao.getByAc(ac);

        System.out.println("full sequence : " + r.getFullSequence().length());
        System.out.println("sequence : " + r.getSequence().length());
        System.out.println("upstream sequence : " + r.getUpStreamSequence());
        System.out.println("downstream sequence : " + r.getDownStreamSequence().length());

        Assert.assertEquals( s.substring(0, 1), r.getFullSequence());
        Assert.assertEquals(null, r.getUpStreamSequence());
        Assert.assertEquals( s.substring(1, 41), r.getDownStreamSequence());
        Assert.assertEquals( 40, r.getDownStreamSequence().length());
    }

    @Test
    public void prepareSequenceWithCTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        String s = "MAEKVNNFPPLPKFIPLKPCFYQDFEADIPPQHVSMTKRLYYLWMLNSVTLAVNLVGCLAWLIGGGGATNFGLAFLWLILFTPCSYVCWFRPIYKAFKTDSSFSFMAFFFTFMAQLVISIIQAVGIPGWGVCGWIATISFFGTNIGSAVVMLIPTVMFTVMAVFSFIALSMVHKFYRGSGGSFSKAQEEWTTGAWKNPHVQQAAQNAAMGAAQGAMNQPQTQYSATPNYTYSNEM";
        Range range = new Range( owner, s.length(), s.length(), null);
        CvFuzzyType rangeFuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class,
        CvFuzzyType.C_TERMINAL_MI_REF,
        CvFuzzyType.C_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);
        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
        feature.getRanges().clear();
        feature.addRange(range);
        getCorePersister().saveOrUpdate(feature);
        String ac = range.getAc();

        Range r = this.rangeDao.getByAc(ac);

        System.out.println("full sequence : " + r.getFullSequence().length());
        System.out.println("sequence : " + r.getSequence().length());
        System.out.println("upstream sequence : " + r.getUpStreamSequence().length());
        System.out.println("downstream sequence : " + r.getDownStreamSequence());

        Assert.assertEquals( s.substring(s.length() - 1, s.length()), r.getFullSequence());
        Assert.assertEquals(s.substring(s.length() - 41, s.length() - 1), r.getUpStreamSequence());
        Assert.assertEquals(null, r.getDownStreamSequence());
    }
}
