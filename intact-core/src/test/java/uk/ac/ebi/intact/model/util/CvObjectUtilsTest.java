package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 15-Feb-2007
 * Time: 13:12:21
 * To change this template use File | Settings | File Templates.
 */
@IntactUnitDataset(dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class)
public class CvObjectUtilsTest extends IntactAbstractTestCase {

    @Test
    public void testGetPsiMiIdentityXref(){
        Assert.assertFalse(0 == getIntactContext().getDataContext().getDaoFactory().getCvObjectDao().countAll());

        CvDatabase uniprotKb = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class,CvDatabase.UNIPROT_MI_REF);

        CvObjectXref cvObjectXref = CvObjectUtils.getPsiMiIdentityXref(uniprotKb);
        if(cvObjectXref == null){
            Assert.fail("The xref retuned should not be null");
        }
        Assert.assertEquals(CvDatabase.UNIPROT_MI_REF,cvObjectXref.getPrimaryId());
    }
}
