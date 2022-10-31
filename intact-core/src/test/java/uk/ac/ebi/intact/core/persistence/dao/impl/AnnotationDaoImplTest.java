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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotationDaoImplTest extends IntactBasicTestCase {

    @Test
    public void persistTest() {
        CvTopic topic = getDaoFactory().getCvObjectDao(CvTopic.class).getByIdentifier(CvTopic.URL_MI_REF);

        Annotation annotation = new Annotation(topic, "http://lalahappy.com");
        getDaoFactory().getAnnotationDao().persist(annotation);

        Assert.assertNotNull(annotation.getAc());
        Assert.assertNotNull(annotation.getCreated());
        Assert.assertNotNull(annotation.getCreator());
        Assert.assertNotNull(annotation.getUpdated());
        Assert.assertNotNull(annotation.getUpdator());
    }

    @Test
    public void getParentsWithAnnotationAc() throws Exception {
        Annotation sharedAnnotation = getMockBuilder().createAnnotation("shared", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
        Annotation notSharedAnnotation = getMockBuilder().createAnnotation("not shared", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);

        Experiment experiment = getMockBuilder().createExperimentEmpty();
        experiment.getAnnotations().clear();
        experiment.addAnnotation(sharedAnnotation);
        experiment.addAnnotation(notSharedAnnotation);

        Experiment experiment2 = getMockBuilder().createExperimentEmpty();
        experiment2.getAnnotations().clear();
        experiment2.addAnnotation(sharedAnnotation);

        getCorePersister().saveOrUpdate(experiment, experiment2);

        Assert.assertNotNull(sharedAnnotation.getAc());
        Assert.assertNotNull(notSharedAnnotation.getAc());

        List<AnnotatedObject> parentsWithShared = getDaoFactory().getAnnotationDao().getParentsWithAnnotationAc(sharedAnnotation.getAc());
        List<AnnotatedObject> parentsWithNotShared = getDaoFactory().getAnnotationDao().getParentsWithAnnotationAc(notSharedAnnotation.getAc());

        Assert.assertEquals(2, parentsWithShared.size());
        Assert.assertEquals(1, parentsWithNotShared.size());
    }
}
