package uk.ac.ebi.intact.model.event;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Institution;

/**
 * IntactObjectEventListener Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactObjectEventListenerTest extends IntactBasicTestCase {

    @Test
    public void auditInfo() throws Exception {
        Institution institution = getDaoFactory().getInstitutionDao().getAll().iterator().next();

        Assert.assertNotNull(institution.getCreated());
        Assert.assertNotNull(institution.getUpdated());
        Assert.assertEquals(institution.getCreated(), institution.getUpdated());
        Assert.assertEquals("SA", institution.getCreator());
        Assert.assertEquals("SA", institution.getUpdator());

        getIntactContext().getUserContext().setUserId("Sam");
        
        beginTransaction();
        institution.setFullName("Different full name");
        getDaoFactory().getInstitutionDao().merge(institution);
        commitTransaction();

        Assert.assertNotNull(institution.getCreated());
        Assert.assertNotNull(institution.getUpdated());
        Assert.assertTrue( "updated={"+institution.getUpdated()+"} created={"+institution.getCreated()+"}",
                           institution.getUpdated().after(institution.getCreated()));
        Assert.assertEquals("SA", institution.getCreator());
        Assert.assertEquals("SAM", institution.getUpdator());
    }
}
