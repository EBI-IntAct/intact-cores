package uk.ac.ebi.intact.model.clone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Interaction;

/**
 * IntactCloner Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.7.2
 */
public class IntactClonerTest extends IntactBasicTestCase {

    IntactCloner cloner;
    
    @Before
    public void init() {
        cloner = new IntactCloner();
    }

    @Test
    public void getClonedObject() throws Exception {
        final Interaction interaction = getMockBuilder().createDeterministicInteraction();
        final IntactObject clone = cloner.clone( interaction );

        Assert.assertNotSame( clone, interaction );
        Assert.assertEquals( clone, interaction );
    }
}
