package uk.ac.ebi.intact.core.persister.standard;

import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.core.persister.PersisterHelper;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionPersisterTest extends AbstractPersisterTest
{

    @Test
    public void testPersistInstitution_default() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        PersisterHelper.saveOrUpdate(institution);

        beginTransaction();
        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());

        commitTransaction();
    }

}