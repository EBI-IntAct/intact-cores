package uk.ac.ebi.intact.core.unit;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IntactUnitDataset(dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class)
public class IntactAbstractTestCase_TypeAnnotTest extends IntactAbstractTestCase
{
    @Test
    public void expectCvsToBeThere(){
        Assert.assertNotSame(0, getIntactContext().getDataContext().getDaoFactory().getCvObjectDao().countAll());
    }
}
