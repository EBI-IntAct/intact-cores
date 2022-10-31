package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Interaction;

/**
 * InteractionDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.4.0
 */
public class InteractionDaoImplTest extends IntactBasicTestCase {

    @Test
    public void countAll() throws Exception {

        final Interaction positive = getMockBuilder().createInteractionRandomBinary();
        final Interaction negative = getMockBuilder().createInteractionRandomBinary();
        negative.addAnnotation( getMockBuilder().createAnnotation( "a negative annot", "IA:xxxx", CvTopic.NEGATIVE ) );

        getCorePersister().saveOrUpdate( positive, negative );

        final InteractionDao iDao = getDaoFactory().getInteractionDao();
        Assert.assertEquals( 2, iDao.countAll() );
        Assert.assertEquals( 2, iDao.countAll( true ) );
        Assert.assertEquals( 1, iDao.countAll( false ) );

    }
}
