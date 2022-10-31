package uk.ac.ebi.intact.core.persistence.svc.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.model.SmallMolecule;

import java.util.Collection;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleSearchServiceTest extends IntactBasicTestCase {

    @Test
    @Rollback(true)
    public void getByQuery_default() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        getPersisterHelper().save(prot1, prot2, prot3);

        SimpleSearchService searchService = new SimpleSearchService();
        Collection<ProteinImpl> results = searchService.search(ProteinImpl.class, "prot*", null, null);

       Assert.assertEquals(2, results.size());
       Assert.assertEquals(results.size(), searchService.count(ProteinImpl.class, "prot*"));
    }

    @Test
    @Rollback(true)
    public void getByQuery_sorted() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        getPersisterHelper().save(prot1, prot2, prot3);

        SimpleSearchService searchService = new SimpleSearchService("xrefs.primaryId", false);
        Collection<ProteinImpl> results = searchService.search(ProteinImpl.class, "pr*", null, null);

        Assert.assertEquals(3, results.size());
        Assert.assertEquals(results.size(), searchService.count(ProteinImpl.class, "pr*"));

        int i=0;
        for (ProteinImpl prot : results) {
            if (i ==0) Assert.assertEquals("prot2", prot.getShortLabel()); // Z
            if (i ==1) Assert.assertEquals("pr0", prot.getShortLabel());  // M
            if (i ==2) Assert.assertEquals("prot1", prot.getShortLabel()); // A
            i++;
        }
    }

    @Test
    @Rollback(true)
    public void count_default() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        getPersisterHelper().save(prot1, prot2, prot3);

        SimpleSearchService searchService = new SimpleSearchService();
        Map<Class<? extends Searchable>,Integer> map = searchService.count(new Class[] {ProteinImpl.class, SmallMolecule.class}, "prot*");

        int resultsCount = 0;

        for (Integer count : map.values()) {
            resultsCount += count;
        }
        
       Assert.assertEquals(2, resultsCount);

    }

}
