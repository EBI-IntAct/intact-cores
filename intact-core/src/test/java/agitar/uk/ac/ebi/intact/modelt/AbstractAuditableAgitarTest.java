/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:22:13
 * Time to generate: 00:14.343 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt;

import uk.ac.ebi.intact.model.*;
import com.agitar.lib.junit.AgitarTestCase;

import java.util.Date;

public class AbstractAuditableAgitarTest extends AgitarTestCase {

    public void testGetCreator() throws Throwable {
        AbstractAuditable cvCellCycle = new CvCellCycle( new Institution( "testAuditLabel" ), "testAuditLabel" );
        cvCellCycle.creator = "testString";
        String result = cvCellCycle.getCreator();
        assertEquals( "result", "testString", result );
    }

    public void testGetCreator1() throws Throwable {
        String result = new CvCellCycle( new Institution( "testAuditLabel" ), "testAuditLabel" ).getCreator();
        assertNull( "result", result );
    }

    public void testGetUpdator() throws Throwable {
        String result = new Institution( "testAuditLabel" ).getUpdator();
        assertNull( "result", result );
    }

    public void testGetUpdator1() throws Throwable {
        AbstractAuditable institution = new Institution( "testAuditLabel" );
        institution.updator = "`;\u001D\u0010y\u0007WM9\u0003W";
        String result = institution.getUpdator();
        assertEquals( "result", "`;\u001D\u0010y\u0007WM9\u0003W", result );
    }

    public void testSetCreated() throws Throwable {
        AbstractAuditable cvFuzzyType = new CvFuzzyType( new Institution( "testAuditLabel" ), "testAuditLabel" );
        Date created = new Date( 100L );
        cvFuzzyType.setCreated( created );
        assertSame( "(CvFuzzyType) cvFuzzyType.getCreated()", created, cvFuzzyType.getCreated() );
    }

    public void testSetCreator() throws Throwable {
        AbstractAuditable institution = new Institution( "testAuditLabel" );
        institution.setCreator( "testAbstractAuditableCreator" );
        assertEquals( "(Institution) institution.creator", "testAbstractAuditableCreator", ( ( Institution ) institution ).creator );
    }

    public void testSetUpdated() throws Throwable {
        Date updated = new Date();
        AbstractAuditable institution = new Institution( "testAuditLabel" );
        institution.setUpdated( updated );
        assertSame( "(Institution) institution.getUpdated()", updated, institution.getUpdated() );
    }

    public void testSetUpdator() throws Throwable {
        AbstractAuditable cvFuzzyType = new CvFuzzyType( new Institution( "testAuditLabel" ), "testAuditLabel" );
        cvFuzzyType.setUpdator( "testAbstractAuditableUpdator" );
        assertEquals( "(CvFuzzyType) cvFuzzyType.updator", "testAbstractAuditableUpdator", ( ( CvFuzzyType ) cvFuzzyType ).updator );
    }
}

