/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.unitdataset.LegacyPsiTestDatasetProvider;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MineInteractionDaoTest extends IntactAbstractTestCase
{

    @Test
    @Ignore
    @IntactUnitDataset(dataset = LegacyPsiTestDatasetProvider.INTACT_CORE, provider = LegacyPsiTestDatasetProvider.class)
    public void testGet() throws Exception
    {
        ProteinImpl prot1 = getDaoFactory().getProteinDao().getByShortLabel("cara_ecoli");
        ProteinImpl prot2 = getDaoFactory().getProteinDao().getByShortLabel("carb_ecoli");
        InteractionImpl interaction = getDaoFactory().getInteractionDao().getByShortLabel("cara-carb-4");
        CvTopic detMethod = getDaoFactory().getCvObjectDao(CvTopic.class).getByShortLabel("url");
        Experiment experiment = interaction.getExperiments().iterator().next();

        System.out.println(experiment);

        MineInteraction newMi = new MineInteraction(prot1, prot2, interaction);
        newMi.setDetectionMethod(detMethod);
        newMi.setGraphId(5);
        newMi.setExperiment(interaction.getExperiments().iterator().next());
        getDaoFactory().getMineInteractionDao().persist(newMi);

        getDataContext().flushSession();

        String ac1 = prot1.getAc();
        String ac2 = prot2.getAc();

        MineInteraction mineInt = getDaoFactory().getMineInteractionDao().get(ac1, ac2);
        MineInteraction mineIntSame = getDaoFactory().getMineInteractionDao().get(ac2, ac1);
        Assert.assertNotNull(mineInt);
        Assert.assertEquals(mineInt, mineIntSame);
    }

}
