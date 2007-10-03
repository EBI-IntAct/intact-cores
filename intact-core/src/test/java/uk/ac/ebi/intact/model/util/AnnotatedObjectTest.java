package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.AnnotatedObjectImpl;
import uk.ac.ebi.intact.model.Protein;

/**
 * AnnotatedObject tester.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since 1.6.3
 */
public class AnnotatedObjectTest extends IntactBasicTestCase {

    @Test
    public void clone_shortlabel_length() {
        Protein ao = getMockBuilder().createProtein( "P12345", "1234567890" );
        Assert.assertNotNull( ao );
        Assert.assertEquals( "1234567890", ao.getShortLabel() );

        try {
            Protein aoc = (Protein ) (( AnnotatedObjectImpl )ao).clone();
            Assert.assertEquals( "1234567890-x", aoc.getShortLabel() );
        } catch ( CloneNotSupportedException e ) {
            Assert.fail();
        }

        // long shortlabel test
        ao = getMockBuilder().createProtein( "P12345", "1234567890123456789" ); // length = 19
        Assert.assertNotNull( ao );
        Assert.assertEquals( "1234567890123456789", ao.getShortLabel() );

        try {
            Protein aoc = (Protein ) (( AnnotatedObjectImpl )ao).clone();
            Assert.assertEquals( ao.getShortLabel(), aoc.getShortLabel() );  // shortlabel not updated.
        } catch ( CloneNotSupportedException e ) {
            Assert.fail();
        }
    }
}
