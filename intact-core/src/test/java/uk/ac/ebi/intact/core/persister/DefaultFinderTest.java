package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Interaction;

/**
 * DefaultFinder Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since TODO artifact version
 * @version $Id$
 */
public class DefaultFinderTest extends IntactBasicTestCase {

    @Test
    public void findAcForInteraction() throws Exception {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate( i );

        Finder finder = new DefaultFinder();
        final String ac = finder.findAc( getMockBuilder().createDeterministicInteraction() );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }
}
