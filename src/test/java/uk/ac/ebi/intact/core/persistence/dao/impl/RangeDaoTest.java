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
}
