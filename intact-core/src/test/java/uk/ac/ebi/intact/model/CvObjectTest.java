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
package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectTest extends IntactBasicTestCase {

    @Test
    public void equal_differentXrefs() {
        CvObject cv1 = getMockBuilder().createCvObject(CvTopic.class, "MI:1234", "supertopic");
        CvObject cv2 = getMockBuilder().createCvObject(CvTopic.class, "MI:1234", "supertopic");

        Assert.assertTrue(cv1.equals(cv2));

        cv1.addXref(getMockBuilder().createIdentityXrefUniprot(cv1, "P12345"));
        cv2.addXref(getMockBuilder().createIdentityXrefUniprot(cv2, "Q00000"));

        Assert.assertFalse(cv1.equals(cv2));
    }
    
    @Test
    public void equal_differentAliases() {
        CvObject cv1 = getMockBuilder().createCvObject(CvTopic.class, "MI:1234", "supertopic");
        CvObject cv2 = getMockBuilder().createCvObject(CvTopic.class, "MI:1234", "supertopic");

        Assert.assertTrue(cv1.equals(cv2));

        cv1.addAlias(getMockBuilder().createAlias(cv1, "name1", CvAliasType.GENE_NAME_MI_REF, CvAliasType.GENE_NAME));
        cv2.addAlias(getMockBuilder().createAlias(cv2, "anotherName", CvAliasType.GENE_NAME_MI_REF, CvAliasType.GENE_NAME));

        Assert.assertFalse(cv1.equals(cv2));
    }

    @Test
    public void equal_differentAnnotations() {
        CvObject cv1 = getMockBuilder().createCvObject(CvTopic.class, "MI:1234", "supertopic");
        CvObject cv2 = getMockBuilder().createCvObject(CvTopic.class, "MI:1234", "supertopic");

        Assert.assertTrue(cv1.equals(cv2));
        
        cv1.addAnnotation(getMockBuilder().createAnnotation("Experiment", null, CvTopic.USED_IN_CLASS));
        cv2.addAnnotation(getMockBuilder().createAnnotation("Interaction", null, CvTopic.USED_IN_CLASS));

        Assert.assertFalse(cv1.equals(cv2));
    }
}
