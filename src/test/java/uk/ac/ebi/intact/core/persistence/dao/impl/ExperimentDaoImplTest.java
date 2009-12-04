package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Test;
import uk.ac.ebi.intact.core.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ExperimentDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class ExperimentDaoImplTest extends IntactBasicTestCase {
        
    @Test
    public void getByLastImexUpdate() throws Exception {
        final Experiment e1 = getMockBuilder().createExperimentRandom(1);
        e1.setLastImexUpdate( parseDate( "2009-11-01" ) );

        final Experiment e2 = getMockBuilder().createExperimentRandom(2);
        e2.setLastImexUpdate( parseDate( "2009-11-05" ) );

        final Experiment e3 = getMockBuilder().createExperimentRandom(1);
        e3.setLastImexUpdate( parseDate( "2009-11-09" ) );

        final Experiment e4 = getMockBuilder().createExperimentRandom(1);
        e4.setLastImexUpdate( null );

        final Experiment e5 = getMockBuilder().createExperimentRandom(2);

        getCorePersister().saveOrUpdate( e1, e2, e3, e4, e5 );

        assertExperimentCountByLastImexUpdate( 0, "2009-08-01", "2009-09-01" );
        assertExperimentCountByLastImexUpdate( 3, "2009-08-01", "2010-09-01" );
        assertExperimentCountByLastImexUpdate( 0, "2009-08-01", "2009-09-01" );
        assertExperimentCountByLastImexUpdate( 1, "2009-08-01", "2009-09-02" );
    }

    private void assertExperimentCountByLastImexUpdate( int expectedExperimentCount,
                                                        String fromDate, String toDate ) throws ParseException {
        final ExperimentDao experimentDao = getDaoFactory().getExperimentDao();
        List<Experiment> experiments = experimentDao.getByLastImexUpdate( parseDate(fromDate),
                                                                           parseDate(toDate) );
        junit.framework.Assert.assertNotNull( experiments );
        junit.framework.Assert.assertEquals( "Expected to find " + expectedExperimentCount +
                             " experiment(s) having lastImexUpdate between " + fromDate + " and " +
                             toDate + ", instead found " + experiments.size() ,
                             expectedExperimentCount, experiments.size() );
    }

    private Date parseDate( String dateStr ) throws ParseException {
        return new SimpleDateFormat( "yyyy-mm-dd" ).parse( dateStr );
    }
}
