/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Institution;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionDaoImplTest extends IntactBasicTestCase {

    private String ac;

    @Before
    public void init() {
        ac = createMockupInstitution();
    }

    @After
    public void cleanup() {
        getDaoFactory().getInstitutionDao().deleteByAc(ac);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void testGetByAc_xrefsFetchedEagerly() throws Exception {
        final Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByAc(ac);

        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
    }

    @Test (expected = LazyInitializationException.class)
    @Transactional(propagation = Propagation.NEVER)
    public void testGetByAc_aliases() throws Exception {
        final Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByAc(ac);

        System.out.println(reloadedInstitution.getAliases());
    }

    @Test (expected = LazyInitializationException.class)
    @Transactional(propagation = Propagation.NEVER)
    public void testGetByAc_annotations() throws Exception {
        final Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByAc(ac);

        System.out.println(reloadedInstitution.getAnnotations());
    }

    private String createMockupInstitution() {
        final TransactionStatus transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Institution institution = getMockBuilder().createInstitution("LALA:12345", "lalaCorp");
        getCorePersister().saveOrUpdate(institution);
        String ac = institution.getAc();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);
        return ac;
    }

}
