package uk.ac.ebi.intact.core.persistence.dao.impl;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.persistence.dao.PublicationDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * PublicationDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class PublicationDaoImplTest extends IntactBasicTestCase {

    @Test
    @Ignore
    public void getByLastImexUpdate() throws Exception {
        final Publication pub1 = getMockBuilder().createPublication( "1" );
        pub1.setLastImexUpdate( parseDate( "2009-11-01" ) );

        final Publication pub2 = getMockBuilder().createPublication( "2" );
        pub2.setLastImexUpdate( parseDate( "2009-11-05" ) );

        final Publication pub3 = getMockBuilder().createPublication( "3" );
        pub3.setLastImexUpdate( parseDate( "2009-11-09" ) );

        final Publication pub4 = getMockBuilder().createPublication( "4" );
        pub4.setLastImexUpdate( null );

        final Publication pub5 = getMockBuilder().createPublication( "5" );

        getCorePersister().saveOrUpdate( pub1, pub2, pub3, pub4, pub5 );

        assertPublicationCountByLastImexUpdate( 0, "2009-08-01", "2009-09-01" );
        assertPublicationCountByLastImexUpdate( 3, "2009-08-01", "2010-09-01" );
        assertPublicationCountByLastImexUpdate( 0, "2009-08-01", "2009-09-01" );
        assertPublicationCountByLastImexUpdate( 1, "2009-08-01", "2009-09-02" );
    }

    private void assertPublicationCountByLastImexUpdate( int expectedPublicationCount,
                                                         String fromDate, String toDate ) throws ParseException {
        final PublicationDao publicationDao = getDaoFactory().getPublicationDao();
        List<Publication> publications = publicationDao.getByLastImexUpdate( parseDate(fromDate),
                                                                             parseDate(toDate) );
        Assert.assertNotNull( publications );
        Assert.assertEquals( "Expected to find " + expectedPublicationCount +
                             " publication having lastImexUpdate between " + fromDate + " and " +
                             toDate + ", instead found " + publications.size() ,
                             expectedPublicationCount, publications.size() );
    }

    private Date parseDate( String dateStr ) throws ParseException {
        return new SimpleDateFormat( "yyyy-mm-dd" ).parse( dateStr );
    }

    @Test
    public void countExperimentsForPublicationAc() throws Exception {
        final Experiment e1 = getMockBuilder().createExperimentRandom( 9 );
        getCorePersister().saveOrUpdate( e1 );

        final List<Publication> allPublications = getDaoFactory().getPublicationDao().getAll();
        Assert.assertEquals( 1, allPublications.size() );
        Publication p = allPublications.iterator().next();

        Assert.assertEquals( 1, getDaoFactory().getPublicationDao().countExperimentsForPublicationAc( p.getAc() ) );
        Assert.assertEquals( 9, getDaoFactory().getPublicationDao().countInteractionsForPublicationAc( p.getAc() ) );
    }
}
