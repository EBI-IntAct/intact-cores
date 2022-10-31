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
package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;

/**
 * TODO comment this class header.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class BulkOperationsTest extends IntactBasicTestCase{

    @Autowired
    private BulkOperations bulkOperations;

    @Test
    public void testAddAnnotation_replace() throws Exception {

        Protein prot1 = getMockBuilder().createProteinRandom();
        prot1.getAnnotations().clear();

        Protein prot2 = getMockBuilder().createProteinRandom();
        prot2.getAnnotations().clear();

        Protein prot3 = getMockBuilder().createProteinRandom();
        prot3.getAnnotations().clear();

        Protein prot4 = getMockBuilder().createProteinRandom();
        prot4.getAnnotations().clear();
        prot4.addAnnotation(new Annotation(getMockBuilder().createCvObject(CvTopic.class, null, CvTopic.NON_UNIPROT), "Already there"));

        getCorePersister().saveOrUpdate(prot1, prot2, prot3, prot4);

        Assert.assertNotNull(prot1.getAc());
        Assert.assertNotNull(prot2.getAc());
        Assert.assertNotNull(prot3.getAc());
        Assert.assertNotNull(prot4.getAc());

        String[] acs = new String[] {prot1.getAc(), prot2.getAc(), prot4.getAc()};

        Annotation annot = new Annotation(getMockBuilder().createCvObject(CvTopic.class, null, CvTopic.NON_UNIPROT), "Don't update");

        bulkOperations.addAnnotation(annot, acs, ProteinImpl.class, true);

        Protein refreshedProt1 = getDaoFactory().getProteinDao().getByAc(prot1.getAc());
        Protein refreshedProt2 = getDaoFactory().getProteinDao().getByAc(prot2.getAc());
        Protein refreshedProt3 = getDaoFactory().getProteinDao().getByAc(prot3.getAc());
        Protein refreshedProt4 = getDaoFactory().getProteinDao().getByAc(prot4.getAc());

        Assert.assertEquals(1, refreshedProt1.getAnnotations().size());
        Assert.assertEquals(1, refreshedProt2.getAnnotations().size());
        Assert.assertEquals(0, refreshedProt3.getAnnotations().size());
        Assert.assertEquals(1, refreshedProt4.getAnnotations().size());

        Annotation annot1 = refreshedProt1.getAnnotations().iterator().next();
        Assert.assertEquals(CvTopic.NON_UNIPROT, annot1.getCvTopic().getShortLabel());
        Assert.assertEquals("Don't update", annot1.getAnnotationText());

        Annotation annot2 = refreshedProt2.getAnnotations().iterator().next();
        Assert.assertEquals(CvTopic.NON_UNIPROT, annot2.getCvTopic().getShortLabel());
        Assert.assertEquals("Don't update", annot2.getAnnotationText());

        Annotation annot4 = refreshedProt4.getAnnotations().iterator().next();
        Assert.assertEquals(CvTopic.NON_UNIPROT, annot4.getCvTopic().getShortLabel());
        Assert.assertEquals("Don't update", annot4.getAnnotationText());

        Assert.assertNotSame(annot1.getAc(), annot2.getAc());
    }

    @Test
    public void testAddAnnotation_not_replace() throws Exception {

        Protein prot1 = getMockBuilder().createProteinRandom();
        prot1.getAnnotations().clear();

        Protein prot2 = getMockBuilder().createProteinRandom();
        prot2.getAnnotations().clear();

        Protein prot3 = getMockBuilder().createProteinRandom();
        prot3.getAnnotations().clear();

        Protein prot4 = getMockBuilder().createProteinRandom();
        prot4.getAnnotations().clear();
        prot4.addAnnotation(new Annotation(getMockBuilder().createCvObject(CvTopic.class, null, CvTopic.NON_UNIPROT), "Already there"));

        getCorePersister().saveOrUpdate(prot1, prot2, prot3, prot4);

        Assert.assertNotNull(prot1.getAc());
        Assert.assertNotNull(prot2.getAc());
        Assert.assertNotNull(prot3.getAc());
        Assert.assertNotNull(prot4.getAc());

        String[] acs = new String[] {prot1.getAc(), prot2.getAc(), prot4.getAc()};

        Annotation annot = new Annotation(getMockBuilder().createCvObject(CvTopic.class, null, CvTopic.NON_UNIPROT), "Don't update");

        bulkOperations.addAnnotation(annot, acs, ProteinImpl.class, false);

        Protein refreshedProt1 = getDaoFactory().getProteinDao().getByAc(prot1.getAc());
        Protein refreshedProt2 = getDaoFactory().getProteinDao().getByAc(prot2.getAc());
        Protein refreshedProt3 = getDaoFactory().getProteinDao().getByAc(prot3.getAc());
        Protein refreshedProt4 = getDaoFactory().getProteinDao().getByAc(prot4.getAc());

        Assert.assertEquals(1, refreshedProt1.getAnnotations().size());
        Assert.assertEquals(1, refreshedProt2.getAnnotations().size());
        Assert.assertEquals(0, refreshedProt3.getAnnotations().size());
        Assert.assertEquals(2, refreshedProt4.getAnnotations().size());
    }
}
