package uk.ac.ebi.intact.persistence.svc.impl;

import org.junit.Test;
import org.junit.Assert;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.persistence.dao.query.QueryTerm;
import uk.ac.ebi.intact.persistence.dao.query.QueryModifier;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleSearchServiceTest extends IntactBasicTestCase{

   @Test
    public void getByQuery_default() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        PersisterHelper.saveOrUpdate(prot1, prot2, prot3);

       SimpleSearchService searchService = new SimpleSearchService();
       Collection<ProteinImpl> results = searchService.search(ProteinImpl.class, "prot*", null, null);

       Assert.assertEquals(2, results.size());

        int i=0;
        for (ProteinImpl prot : results) {
            System.out.println(prot);
//            if (i ==0) Assert.assertEquals("pr0", prot.getShortLabel());
//            if (i ==1) Assert.assertEquals("prot1", prot.getShortLabel());
//            if (i ==2) Assert.assertEquals("prot2", prot.getShortLabel());
            i++;
        }
    }

    @Test
    public void getByQuery_sorted() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        PersisterHelper.saveOrUpdate(prot1, prot2, prot3);

       SimpleSearchService searchService = new SimpleSearchService("xrefs", false);
       Collection<ProteinImpl> results = searchService.search(ProteinImpl.class, "pr*", null, null);

       Assert.assertEquals(3, results.size());

        int i=0;
        for (ProteinImpl prot : results) {
            if (i ==0) Assert.assertEquals("prot2", prot.getShortLabel()); // Z
            if (i ==1) Assert.assertEquals("pr0", prot.getShortLabel());  // M
            if (i ==2) Assert.assertEquals("prot1", prot.getShortLabel()); // A
            i++;
        }
    }

}
