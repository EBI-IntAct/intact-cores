/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.imex;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexReleaseTaggerTest extends IntactBasicTestCase {

    @Test
    public void tag_publication() throws Exception {
        Publication publication = getMockBuilder().createPublication("1234567");

        DateTime date = new DateTime(1234567890);

        ImexReleaseTagger tagger = new ImexReleaseTagger(getIntactContext());
        tagger.tag(publication, date);

        Annotation annotation = AnnotatedObjectUtils.findAnnotationByTopicMiOrLabel(publication, CvTopic.LAST_IMEX_UPDATE);

        Assert.assertNotNull(annotation);
        Assert.assertEquals("1970/01/15", annotation.getAnnotationText());

    }

    @Test
    public void tag_publication_existing() throws Exception {
        Publication publication = getMockBuilder().createPublication("1234567");
        publication.getAnnotations().clear();
        publication.addAnnotation(getMockBuilder().createAnnotation("1915/11/01", null, CvTopic.LAST_IMEX_UPDATE));

        DateTime date = new DateTime(1234567890);

        ImexReleaseTagger tagger = new ImexReleaseTagger(getIntactContext());
        tagger.tag(publication, date);

        Annotation annotation = AnnotatedObjectUtils.findAnnotationByTopicMiOrLabel(publication, CvTopic.LAST_IMEX_UPDATE);

        Assert.assertNotNull(annotation);
        Assert.assertEquals("1970/01/15", annotation.getAnnotationText());
        Assert.assertEquals(1, publication.getAnnotations().size());

    }

    @Test
    public void tag_experiment() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty();

        DateTime date = new DateTime(1234567890);

        ImexReleaseTagger tagger = new ImexReleaseTagger(getIntactContext());
        tagger.tag(experiment, date);

        Annotation annotation = AnnotatedObjectUtils.findAnnotationByTopicMiOrLabel(experiment, CvTopic.LAST_IMEX_UPDATE);

        Assert.assertNotNull(annotation);
        Assert.assertEquals("1970/01/15", annotation.getAnnotationText());
    }

    @Test
    public void tag_interaction() throws Exception {
         Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        DateTime date = new DateTime(1234567890);

        ImexReleaseTagger tagger = new ImexReleaseTagger(getIntactContext());
        tagger.tag(interaction, date);

        Annotation annotation = AnnotatedObjectUtils.findAnnotationByTopicMiOrLabel(interaction, CvTopic.LAST_IMEX_UPDATE);

        Assert.assertNotNull(annotation);
        Assert.assertEquals("1970/01/15", annotation.getAnnotationText());
    }
}
