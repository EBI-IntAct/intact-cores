package uk.ac.ebi.intact.core.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.util.CvObjectBuilder;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactUnitTest extends IntactAbstractTestCase
{

    @Test
    public void testResetSchema() throws Exception {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvXrefQualifier cvXrefQual = builder.createIdentityCvXrefQualifier(getIntactContext());
        CvDatabase cvDatabase = builder.createPsiMiCvDatabase(getIntactContext());
        getDataContext().getDaoFactory().getCvObjectDao(CvXrefQualifier.class).persist(cvXrefQual);
        getDataContext().getDaoFactory().getCvObjectDao(CvDatabase.class).persist(cvDatabase);

        getDataContext().commitTransaction();
        getDataContext().beginTransaction();

        assertEquals(2, getDataContext().getDaoFactory().getCvObjectDao().countAll());

        getDataContext().commitTransaction();
        assertFalse(getDataContext().isTransactionActive());

        IntactUnit iu = new IntactUnit();
        iu.resetSchema();

        getDataContext().beginTransaction();
        assertEquals(0, getDataContext().getDaoFactory().getCvObjectDao().countAll());
        getDataContext().commitTransaction();
    }
}
