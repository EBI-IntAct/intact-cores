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
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionShortLabelGeneratorTest extends IntactBasicTestCase {

    @Test
    public void createCandidateShortLabel() throws Exception {
        String baitLabel = "bait";
        String preyLabel ="prey";

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(baitLabel, preyLabel);

        assertNotNull(candLabel);
        assertEquals("bait-prey", candLabel);
    }

    @Test
    public void createCandidateShortLabel_truncate() throws Exception {
        String baitLabel = "IAmAHappyBayt";
        String preyLabel ="AndIAmAHappyPrey";

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(baitLabel, preyLabel);

        assertNotNull(candLabel);
        assertEquals("iamahappyb-andiamaha", candLabel);
    }

    @Test
    public void createCandidateShortLabel_labelWithHyphen() throws Exception {
        String baitLabel = "nana";
        String preyLabel ="EBI-12345";

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(baitLabel, preyLabel);

        assertNotNull(candLabel);
        assertEquals("nana-ebi_12345", candLabel);
    }

    @Test
    public void createCandidateShortLabel_labelUncommonChars() throws Exception {
        String baitLabel = "nana";
        String preyLabel ="BRC/ABL fusion";

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(baitLabel, preyLabel);

        assertNotNull(candLabel);
        assertEquals("nana-brc_abl_fusion", candLabel);
    }

    @Test
    public void createCandidateShortLabel_fromInteraction() throws Exception {
        Interactor interactorA = getMockBuilder().createProtein("P0A6F1", "cara");
        Interactor interactorB = getMockBuilder().createProtein("P00968", "carb");
        Interaction interaction = getMockBuilder().createInteraction("cara-carb-4", interactorA, interactorB, getMockBuilder().createExperimentEmpty("exp"));

        assertNotNull(interaction);

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(interaction);

        assertNotNull(candLabel);
        assertEquals("cara-carb", candLabel);
    }

    @Test
     public void selfInteractionShortLabel() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        Interactor interactor = getMockBuilder().createProteinRandom();
        final String geneName = "nana";
        interactor.getAliases().iterator().next().setName(geneName);

        Component comp = getMockBuilder().createComponentNeutral(interaction, interactor);
        interaction.setComponents(Arrays.asList(comp));

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(interaction);

        assertNotNull(candLabel);
        assertEquals(geneName, candLabel);
    }

    @Test
    public void createCandidateShortLabel_fromInteraction_null() throws Exception {
        Interactor interactorA = getMockBuilder().createProtein("Q13158", "fadd");
        Interactor interactorB = getMockBuilder().createProtein("Q14790", "casp8");
        Interaction interaction = getMockBuilder().createInteraction("fadd-casp8-2", interactorA, interactorB, getMockBuilder().createExperimentEmpty("exp"));

        assertNotNull(interaction);

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(interaction);

        assertNotNull(candLabel);
        Assert.assertEquals("fadd-casp8", candLabel);
    }

    @Test
    public void createCandidateShortLabel_badCharsInInteractor() throws Exception {
        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel("bis3-indolylmaleimid", "prey");
        Assert.assertEquals("bis3_indolylmal-prey", candLabel);
    }

    @Test
    public void prepareLabel() throws Exception {
        Assert.assertEquals("la_la", InteractionShortLabelGenerator.InteractionShortLabel.prepareLabel("la-la"));
        Assert.assertEquals("la_la", InteractionShortLabelGenerator.InteractionShortLabel.prepareLabel("la-la"));
        Assert.assertEquals("la_la", InteractionShortLabelGenerator.InteractionShortLabel.prepareLabel("la.la"));
        Assert.assertEquals("la_la", InteractionShortLabelGenerator.InteractionShortLabel.prepareLabel("la_la"));
    }

}