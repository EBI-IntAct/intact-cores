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

import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.mock.MockIntactContext;
import uk.ac.ebi.intact.core.unit.mock.MockInteractionDao;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionUtilsTest extends IntactBasicTestCase{

    @Before
    public void prepare() throws Exception {
        MockIntactContext.initMockContext();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void syncShortLabelWithDb_default() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new NormalInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("intera-interb");
        assertEquals("intera-interb-3", syncedLabel);
    }

    @Test
    public void syncShortLabelWithDb_non() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new NoneInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("intera-interb");
        assertEquals("intera-interb", syncedLabel);
    }

    @Test
    public void syncShortLabelWithDb_non_self() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new NoneInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("interaself-1");
        assertEquals("interaself-1", syncedLabel);
    }

    @Test
    public void syncShortLabelWithDb_non_self_noPrefix() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new NoneInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("interaself");
        assertEquals("interaself", syncedLabel);
    }

    @Test
    public void syncShortLabelWithDb_normal_self() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new NormalInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("interaself-1");
        assertEquals("interaself-3", syncedLabel);
    }

    @Test
    public void syncShortLabelWithDb_existing() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new ExistingInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("intera-interb");
        assertEquals("intera-interb-1", syncedLabel);
    }

    @Test
    public void syncShortLabelWithDb_gaps() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockInteractionDao(new GapInteractionLabelDao());

        String syncedLabel = InteractionUtils.syncShortLabelWithDb("intera-interb");
        assertEquals("intera-interb-5", syncedLabel);
    }

    @Test
    public void syncShortLabel_temporary() throws Exception {
        String syncedLabel = InteractionUtils.syncShortLabelWithDb("unk-unk-123232");
        Assert.assertEquals(syncedLabel, "unk-unk-123232");
    }

    @Test
    public void isTemporary() throws Exception {
        Assert.assertTrue(InteractionUtils.isTemporaryLabel("unk-unk-123232"));
        Assert.assertFalse(InteractionUtils.isTemporaryLabel("la-la-4"));
    }

    @Test
    public void getInteractorPrimaryIDs_default() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("A1", "A2");
        Assert.assertEquals(2, InteractionUtils.getInteractorPrimaryIDs(interaction).size());
    }

    @Test
    public void getInteractorPrimaryIDs_duplicatedIDs() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("A1", "A1");

        // remove any feature, so they are equal
        for (Component c : interaction.getComponents()) {
            c.getBindingDomains().clear();
        }

        Assert.assertEquals(1, InteractionUtils.getInteractorPrimaryIDs(interaction).size());
    }

    @Test
    public void getInteractorPrimaryIDs_moreThanOneIDInInteractor() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("A1", "A2");
        interaction.getComponents().iterator().next().getInteractor().getXrefs()
                .add(getMockBuilder().createIdentityXrefUniprot(interaction, "B2"));
        Assert.assertEquals(2, InteractionUtils.getInteractorPrimaryIDs(interaction).size());
    }

    private static String removePercent(String labelLike) {
        if (labelLike.contains("%")) {
            labelLike = labelLike.replaceAll("%", "");
        }
        return labelLike;
    }

    private class NoneInteractionLabelDao extends MockInteractionDao {

        @Override
        public List<String> getShortLabelsLike(String labelLike) {
            return Collections.EMPTY_LIST;
        }
    }

    private class NormalInteractionLabelDao extends MockInteractionDao {

        @Override
        public List<String> getShortLabelsLike(String labelLike) {
            labelLike = removePercent(labelLike);
            return Arrays.asList(labelLike+"-1",
                                 labelLike+"-2");
        }
    }

    private class ExistingInteractionLabelDao extends MockInteractionDao {

        @Override
        public List<String> getShortLabelsLike(String labelLike) {
            labelLike = removePercent(labelLike);
            return Arrays.asList(labelLike);
        }
    }

    private class GapInteractionLabelDao extends MockInteractionDao {

        @Override
        public List<String> getShortLabelsLike(String labelLike) {
            labelLike = removePercent(labelLike);
            return Arrays.asList(labelLike+"-1",
                                 labelLike+"-2",
                                 labelLike+"-4");
        }
    }
}