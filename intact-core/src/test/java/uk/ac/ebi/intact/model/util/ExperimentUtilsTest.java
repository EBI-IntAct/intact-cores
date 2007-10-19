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
package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.mock.MockExperimentDao;
import uk.ac.ebi.intact.core.unit.mock.MockIntactContext;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentUtilsTest extends IntactBasicTestCase {

    @Test
    public void pubmedFromPublication() {
        final String expectedPubmedId = "1234567";

        Publication publication = getMockBuilder().createPublication(expectedPubmedId);
        Experiment experiment = getMockBuilder().createExperimentEmpty("myExp");
        experiment.setPublication(publication);

        String pubmedId = ExperimentUtils.getPubmedId(experiment);

        Assert.assertNotNull(pubmedId);
        Assert.assertEquals(expectedPubmedId, pubmedId);
    }

    @Test
    public void pubmedFromXref() {
        final String expectedPubmedId = "1234567";

        Experiment experiment = getMockBuilder().createExperimentEmpty("myExp", expectedPubmedId);

        String pubmedId = ExperimentUtils.getPubmedId(experiment);

        Assert.assertNotNull(pubmedId);
        Assert.assertEquals(expectedPubmedId, pubmedId);
    }

    @Test
    public void syncShortLabelWithDb_ungapped() {
        final String expPrefix = "lala-2007";

        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockExperimentDao(new MockExperimentDao(){
            public List<String> getShortLabelsLike(String labelLike)
            {
                return Arrays.asList(expPrefix+"-1", expPrefix+"-2", expPrefix+"-3");
            }
        });

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, null);

        Assert.assertEquals("lala-2007-4", syncedLabel);

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void syncShortLabelWithDb_gapped() {
        final String expPrefix = "lala-2007";

        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockExperimentDao(new MockExperimentDao(){
            public List<String> getShortLabelsLike(String labelLike)
            {
                return Arrays.asList(expPrefix+"-4", expPrefix+"-2", expPrefix+"-7");
            }
        });

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, null);

        Assert.assertEquals("lala-2007-8", syncedLabel);

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void syncShortLabelWithDb_first() {
        final String expPrefix = "lala-2007";

        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockExperimentDao(new MockExperimentDao(){
            public List<String> getShortLabelsLike(String labelLike)
            {
                return Collections.EMPTY_LIST;
            }
        });

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, null);

        Assert.assertEquals("lala-2007-1", syncedLabel);

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void syncShortLabelWithDb_alreadyWithSuffix() {
        final String expPrefix = "lala-2007-4";

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, null);
        Assert.assertEquals("lala-2007-4", syncedLabel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void syncShortLabelWithDb_wrong() {
        final String expPrefix = "lala-5";

        ExperimentUtils.syncShortLabelWithDb(expPrefix, null);
    }

    @Test
    public void syncShortLabelWithDb_pubmedExisting() {
        final String pubId = "1234567";
        final String expPrefix = "lala-2007";

        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockExperimentDao(new MockExperimentDao(){
            @Override
            public List<Experiment> getByPubIdAndLabelLike(String pubId, String labelLike) {
                return Arrays.asList(
                     getMockBuilder().createExperimentEmpty("lala-2007-1", "0"),
                     getMockBuilder().createExperimentEmpty("lala-2007a-1", "1"),
                     getMockBuilder().createExperimentEmpty("lala-2007a-2", "1"),
                     getMockBuilder().createExperimentEmpty("lala-2007b-1", "2")
                );
            }
        });

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, pubId);

        System.out.println(syncedLabel);
        Assert.assertEquals("lala-2007c-1", syncedLabel);

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void syncShortLabelWithDb_samePubmedYear() {
        final String pubId = "1234567";
        final String expPrefix = "lala-2007";

        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockExperimentDao(new MockExperimentDao(){
            @Override
            public List<Experiment> getByPubIdAndLabelLike(String pubId, String labelLike) {
                return Arrays.asList(
                     getMockBuilder().createExperimentEmpty("lala-2007-1", "0"),
                     getMockBuilder().createExperimentEmpty("lala-2007a-1", "1234567")
                );
            }
        });

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, pubId);

        System.out.println(syncedLabel);
        Assert.assertEquals("lala-2007a-2", syncedLabel);

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void syncShortLabelWithDb_differentPubmedYear() {
        final String pubId = "55";
        final String expPrefix = "lala-2007";

        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockExperimentDao(new MockExperimentDao(){
            @Override
            public List<Experiment> getByPubIdAndLabelLike(String pubId, String labelLike) {
                return Arrays.asList(
                     getMockBuilder().createExperimentEmpty("lala-2005-1", "0"),
                     getMockBuilder().createExperimentEmpty("lala-2005a-1", "1234567"),
                     getMockBuilder().createExperimentEmpty("lala-2007-1", "55")
                );
            }
        });

        String syncedLabel = ExperimentUtils.syncShortLabelWithDb(expPrefix, pubId);

        System.out.println(syncedLabel);
        Assert.assertEquals("lala-2007-2", syncedLabel);

        IntactContext.getCurrentInstance().close();
    }
    
    @Test
    public void matchesSyncedLabel() {
        Assert.assertTrue(ExperimentUtils.matchesSyncedLabel("mike-2007-3"));
        Assert.assertTrue(ExperimentUtils.matchesSyncedLabel("mike-2007a-3"));
        Assert.assertTrue(ExperimentUtils.matchesSyncedLabel("mike-2007-10"));
        Assert.assertFalse(ExperimentUtils.matchesSyncedLabel("mike-2007"));
        Assert.assertFalse(ExperimentUtils.matchesSyncedLabel("mike-20079"));
    }

    @Test
    public void matchesNotSyncedLabel() {
        Assert.assertTrue(ExperimentUtils.matchesMotSyncedLabel("mike-2007"));
        Assert.assertFalse(ExperimentUtils.matchesMotSyncedLabel("mike-2007-3"));
        Assert.assertFalse(ExperimentUtils.matchesMotSyncedLabel("mike-2007a-3"));
    }

    @Test
    public void isOnHold_false() {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        Assert.assertFalse(ExperimentUtils.isOnHold(exp));
    }

    @Test
    public void isOnHold_false_noPub() {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        exp.setPublication(null);
        Assert.assertFalse(ExperimentUtils.isOnHold(exp));
    }

    @Test
    public void isOnHold_true() {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        Annotation annot = getMockBuilder().createAnnotation("Reason", "IA:0", CvTopic.ON_HOLD);
        exp.addAnnotation(annot);

        Assert.assertTrue(ExperimentUtils.isOnHold(exp));
    }

    @Test
    public void isOnHold_true_noPub() {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        exp.setPublication(null);
        Annotation annot = getMockBuilder().createAnnotation("Reason", "IA:0", CvTopic.ON_HOLD);
        exp.addAnnotation(annot);

        Assert.assertTrue(ExperimentUtils.isOnHold(exp));
    }

    @Test
    public void isOnHold_false_manyExpsInPub() {
        Experiment exp = getMockBuilder().createExperimentEmpty();

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(exp.getPublication());
        exp.getPublication().addExperiment(exp2);

        Assert.assertFalse(ExperimentUtils.isOnHold(exp));
    }

    @Test
    public void isOnHold_true_manyExpsInPub() {
        Experiment exp = getMockBuilder().createExperimentEmpty();

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(exp.getPublication());
        exp.getPublication().addExperiment(exp2);
        Annotation annot = getMockBuilder().createAnnotation("Reason", "IA:0", CvTopic.ON_HOLD);
        exp2.addAnnotation(annot);

        Assert.assertTrue(ExperimentUtils.isOnHold(exp));
    }

    @Test
    public void toBeReviewed_false() {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        Assert.assertFalse(ExperimentUtils.isToBeReviewed(exp));
    }

    @Test
    public void toBeReviewed_true() {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        Annotation annot = getMockBuilder().createAnnotation("", "IA:0", CvTopic.TO_BE_REVIEWED);
        exp.addAnnotation(annot);

        Assert.assertTrue(ExperimentUtils.isToBeReviewed(exp));
    }
}