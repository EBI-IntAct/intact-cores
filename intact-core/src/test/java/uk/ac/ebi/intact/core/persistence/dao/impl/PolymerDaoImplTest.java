/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.PolymerImpl;
import uk.ac.ebi.intact.model.Protein;

import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PolymerDaoImplTest extends IntactBasicTestCase {

    @Test
    public void getByCrcAndTaxId_prot() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        String crc = prot.getCrc64();
        String taxId = prot.getBioSource().getTaxId();

        Assert.assertNotNull(crc);
        Assert.assertNotNull(taxId);

        getCorePersister().saveOrUpdate(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        List<PolymerImpl> retrievedProts = getDaoFactory().getPolymerDao().getByCrcAndTaxId(crc, taxId);
        Assert.assertEquals(1, retrievedProts.size());
        Assert.assertEquals(prot.getAc(), retrievedProts.get(0).getAc());

        Assert.assertTrue(getDaoFactory().getPolymerDao().getByCrcAndTaxId("crcrcrc", "taxtaxtax").isEmpty());
    }

    @Test
    public void getByCrc_prot() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        String crc = prot.getCrc64();

        Assert.assertNotNull(crc);

        getCorePersister().saveOrUpdate(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        List<PolymerImpl> retrievedProts = getDaoFactory().getPolymerDao().getByCrc(crc);
        Assert.assertEquals(1, retrievedProts.size());
        Assert.assertEquals(prot.getAc(), retrievedProts.get(0).getAc());

        Assert.assertTrue(getDaoFactory().getPolymerDao().getByCrc("crcrcrc").isEmpty());
    }
}
