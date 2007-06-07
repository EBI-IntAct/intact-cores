package uk.ac.ebi.intact.modelt.util;

import junit.framework.TestCase;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 15-Feb-2007
 * Time: 13:12:21
 * To change this template use File | Settings | File Templates.
 */
public class CvObjectUtilsTest extends TestCase {
    private DaoFactory daoFactory;

    public CvObjectUtilsTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

    }

    public void tearDown() throws Exception {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        daoFactory = null;
    }

    public void testGetPsiMiIdentityXref(){
        CvDatabase uniprotKb = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class,CvDatabase.UNIPROT_MI_REF);
        CvObjectXref cvObjectXref = CvObjectUtils.getPsiMiIdentityXref(uniprotKb);
        if(cvObjectXref == null){
            fail("The xref retuned should not be null");
        }
        assertEquals(CvDatabase.UNIPROT_MI_REF,cvObjectXref.getPrimaryId());
    }
}
