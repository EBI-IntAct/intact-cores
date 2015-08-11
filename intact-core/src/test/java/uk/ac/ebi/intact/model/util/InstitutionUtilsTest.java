/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this class header.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionUtilsTest extends IntactBasicTestCase {

    @Test
    public void retrieveCvDatabase1() throws Exception {
        CvDatabase lalaDb = getMockBuilder().createCvObject(CvDatabase.class, "MI:9999", "laladb");
        getCorePersister().saveOrUpdate(lalaDb);

        Institution institution = new Institution("laladb");
        institution.addXref(getMockBuilder().createIdentityXrefPsiMi(institution, "MI:9999"));

        CvDatabase cvDatabase = InstitutionUtils.retrieveCvDatabase(getIntactContext(), institution, CvDatabase.INTACT_MI_REF);

        Assert.assertNotNull(cvDatabase);
        Assert.assertEquals("MI:9999", cvDatabase.getIdentifier());
    }

    @Test
    public void retrieveCvDatabase2() throws Exception {
        CvDatabase lalaDb = getMockBuilder().createCvObject(CvDatabase.class, "MI:9999", "laladb");
        CvDatabase intactDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        getCorePersister().saveOrUpdate(lalaDb, intactDb);

        Institution institution = new Institution("laladb");
        institution.addXref(getMockBuilder().createIdentityXrefPsiMi(institution, "MI:00000"));

        CvDatabase cvDatabase = InstitutionUtils.retrieveCvDatabase(getIntactContext(), institution, CvDatabase.INTACT_MI_REF);

        Assert.assertNotNull(cvDatabase);
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvDatabase.getIdentifier());
    }

    @Test
    public void retrieveCvDatabase3() throws Exception {
        CvDatabase lalaDb = getMockBuilder().createCvObject(CvDatabase.class, "MI:9999", "laladb");
        getCorePersister().saveOrUpdate(lalaDb);

        Institution institution = new Institution("laladb");
        institution.addXref(getMockBuilder().createIdentityXrefPsiMi(institution, "MI:00000"));

        CvDatabase cvDatabase = InstitutionUtils.retrieveCvDatabase(getIntactContext(), institution);

        Assert.assertNull(cvDatabase);
    }
}
