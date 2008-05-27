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
package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Institution;

import java.util.Collection;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectUtilsTest {

    @Test
    public void searchXrefsByDatabase() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefsByDatabase(cvDatabase, CvDatabase.PSI_MI_MI_REF);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }

    @Test
    public void searchXrefsByQualifier() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefsByQualifier(cvDatabase, CvXrefQualifier.IDENTITY_MI_REF);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }
    
    @Test
    public void searchXrefs() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefs(cvDatabase, CvDatabase.PSI_MI_MI_REF, CvXrefQualifier.IDENTITY_MI_REF);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }
    
    @Test
    public void searchXrefs_nullQualifier() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefs(cvDatabase, CvDatabase.PSI_MI_MI_REF, null);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }

}
