package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * Xref Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since TODO artifact version
 * @version $Id$
 */
public class XrefTest extends IntactBasicTestCase {
    
    @Test
    public void equals() throws Exception {
        CvDatabase db = getMockBuilder().getPsiMiDatabase();
        final Institution owner = getMockBuilder().createInstitution("MI:0000", "ebi");

        Xref xref1 = new InteractorXref( owner, db, "12345", null );
        Xref xref2 = new InteractorXref( owner, db, "12345", null );

        Assert.assertTrue( xref1.equals( xref2 ) );

        xref1.setAc( "nana" );
        Assert.assertTrue( xref1.equals( xref2 ) );

        CvXrefQualifier qu = getMockBuilder().getIdentityQualifier();
        xref1.setCvXrefQualifier( qu );
        Assert.assertFalse( xref1.equals( xref2 ) );

        xref2.setCvXrefQualifier( qu );
        Assert.assertTrue( xref1.equals( xref2 ) );
    }
}
