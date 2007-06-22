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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IgnoreDatabase;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.unitdataset.LegacyPsiTestDatasetProvider;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IgnoreDatabase
public class InteractionShortLabelGeneratorTest extends IntactAbstractTestCase {

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
    @IntactUnitDataset(dataset = LegacyPsiTestDatasetProvider.INTACT_CORE, provider = LegacyPsiTestDatasetProvider.class)
    public void createCandidateShortLabel_fromInteraction() throws Exception {
        Interaction interaction = getDaoFactory().getInteractionDao().getByShortLabel("cara-carb-4");

        assertNotNull(interaction);

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(interaction);

        assertNotNull(candLabel);
        assertEquals("cara-carb", candLabel);
    }
}