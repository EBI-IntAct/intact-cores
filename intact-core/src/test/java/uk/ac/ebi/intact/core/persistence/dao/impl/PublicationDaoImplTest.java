package uk.ac.ebi.intact.core.persistence.dao.impl;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

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
