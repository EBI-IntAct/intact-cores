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
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Polymer;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.unitdataset.LegacyPsiTestDatasetProvider;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: PolymerDaoTest.java 6881 2006-11-21 10:58:30Z baranda $
 * @since <pre>08-Aug-2006</pre>
 */
public class PolymerDaoTest extends IntactBasicTestCase
{
    @Test
    public void testGetSequenceByPolymerAc() throws Exception
    {
        String originalSeq = "MNSYFEQASGFYGHPHQATGMAMGSGGHHDQTASAAAAAYRGFPLSLGMSPYANHHLQRTTQDSPYDASITAACNKIYGDGAGAYKQDCLNIKADAVNGYKDIWNTGGSNGGGGGGGGGGGGGAGGTGGAGNANGGNAANANGQNNPAGGMPVRPSACTPDSRVGGYLDTSGGSPVSHRGGSAGGNVSVSGGNGNAGGVQSGVGVAGAGTAWNANCTISGAAAQTAAASSLHQASNHTFYPWMAIAGECPEDPTKSKIRSDLTQYGGISTDMGKRYSESLAGSLLPDWLGTNGLRRRGRQTYTRYQTLELEKEFHTNHYLTRRRRIEMAHALCLTERQIKIWFQNRRMKLKKEIQAIKELNEQEKQAQAQKAAAAAAAAAAVQGGHLDQ";

        Protein protein = getMockBuilder().createDeterministicProtein("P83949-1", "P83949-1");
        protein.setSequence(originalSeq);

        PersisterHelper.saveOrUpdate(protein);

        Polymer polymer = getDaoFactory().getPolymerDao().getByShortLabel("p83949-1");

        String seq = getIntactContext().getDataContext().getDaoFactory().getPolymerDao().getSequenceByPolymerAc(polymer.getAc());
        Assert.assertEquals(originalSeq, seq);
    }

}
