package uk.ac.ebi.intact.core.unit;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IgnoreDatabase
public class IntactAbstractTestCase_TypeIgnoreDbTest extends IntactAbstractTestCase
{
    @BeforeClass
    public static void resetDbBeforeClass() throws Exception{
        IntactUnit iu = new IntactUnit();
        iu.createSchema();
    }

    @Test
    @IntactUnitDataset(dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
    public void expectCvsToBeThere(){
         Assert.assertNotSame(0, getDaoFactory().getCvObjectDao().countAll());
    }
}