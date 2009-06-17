/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryTerm;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryModifier;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SearchableDaoTest extends IntactBasicTestCase {

    @Test
    public void getByQuery_default() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        PersisterHelper.saveOrUpdate(prot1, prot2, prot3);

        SearchableQuery query = new SearchableQuery();
        query.setShortLabel(new QueryPhrase(Arrays.asList(new QueryTerm("prot", QueryModifier.WILDCARD_END))));
        Collection<ProteinImpl> results = (Collection<ProteinImpl>) getDaoFactory().getSearchableDao().getByQuery(ProteinImpl.class, query, null, null);

        Assert.assertEquals(2, results.size());
    }

    @Test
    public void getByQuery_sorted() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());

        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("M", "pr0");
        Protein prot3 = getMockBuilder().createProtein("Z", "prot2");

        PersisterHelper.saveOrUpdate(prot1, prot2, prot3);

        SearchableQuery query = new SearchableQuery();
        query.setShortLabel(new QueryPhrase(Arrays.asList(new QueryTerm("pr", QueryModifier.WILDCARD_END))));
        Collection<ProteinImpl> results = (Collection<ProteinImpl>) getDaoFactory().getSearchableDao()
                .getByQuery(ProteinImpl.class, query, null, null, "shortLabel", true);

        int i=0;
        for (ProteinImpl prot : results) {
            if (i ==0) Assert.assertEquals("pr0", prot.getShortLabel());
            if (i ==1) Assert.assertEquals("prot1", prot.getShortLabel());
            if (i ==2) Assert.assertEquals("prot2", prot.getShortLabel());
            i++;
        }
    }

    @Test
    public void getInteractors() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        PersisterHelper.saveOrUpdate(getMockBuilder().createDeterministicInteraction());
        Assert.assertEquals(2, getDaoFactory().getInteractorDao().getInteractors(0, 5).size());
    }
}