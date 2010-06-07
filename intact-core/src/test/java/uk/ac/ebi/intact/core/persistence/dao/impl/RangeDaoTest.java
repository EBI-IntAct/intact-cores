package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persistence.dao.RangeDao;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
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
        Range range = new Range( owner, 1, 109, s);

        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
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
        Range range = new Range( owner, 0, 0, s);
        CvFuzzyType rangeFuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class,
        CvFuzzyType.N_TERMINAL_MI_REF,
        CvFuzzyType.N_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);
        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
        feature.addRange(range);
        getCorePersister().saveOrUpdate(feature);
        String ac = range.getAc();

        Range r = this.rangeDao.getByAc(ac);

        System.out.println("full sequence : " + r.getFullSequence().length());
        System.out.println("sequence : " + r.getSequence().length());
        System.out.println("upstream sequence : " + r.getUpStreamSequence());
        System.out.println("downstream sequence : " + r.getDownStreamSequence().length());

        Assert.assertEquals( s.substring(0, 100), r.getFullSequence());
        Assert.assertEquals(null, r.getUpStreamSequence());
        Assert.assertEquals( s.substring(100, 140), r.getDownStreamSequence());
    }

    @Test
    public void prepareSequenceWithCTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        String s = "MAEKVNNFPPLPKFIPLKPCFYQDFEADIPPQHVSMTKRLYYLWMLNSVTLAVNLVGCLAWLIGGGGATNFGLAFLWLILFTPCSYVCWFRPIYKAFKTDSSFSFMAFFFTFMAQLVISIIQAVGIPGWGVCGWIATISFFGTNIGSAVVMLIPTVMFTVMAVFSFIALSMVHKFYRGSGGSFSKAQEEWTTGAWKNPHVQQAAQNAAMGAAQGAMNQPQTQYSATPNYTYSNEM";
        Range range = new Range( owner, 0, 0, s);
        CvFuzzyType rangeFuzzyType = getMockBuilder().createCvObject( CvFuzzyType.class,
        CvFuzzyType.C_TERMINAL_MI_REF,
        CvFuzzyType.C_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);
        Feature feature = getMockBuilder().createFeature("test", getMockBuilder().createCvObject(CvFeatureType.class, "MI:0111", "feature_type"));
        feature.addRange(range);
        getCorePersister().saveOrUpdate(feature);
        String ac = range.getAc();

        Range r = this.rangeDao.getByAc(ac);

        System.out.println("full sequence : " + r.getFullSequence().length());
        System.out.println("sequence : " + r.getSequence().length());
        System.out.println("upstream sequence : " + r.getUpStreamSequence().length());
        System.out.println("downstream sequence : " + r.getDownStreamSequence());

        Assert.assertEquals( s.substring(s.length() - 100, s.length()), r.getFullSequence());
        Assert.assertEquals(s.substring(s.length() - 140, s.length() - 100), r.getUpStreamSequence());
        Assert.assertEquals(null, r.getDownStreamSequence());
    }
}
