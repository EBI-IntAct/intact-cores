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
package uk.ac.ebi.intact.context;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.config.SequenceManager;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SequenceManagerTest extends IntactBasicTestCase {

    @Test
    public void createSequenceIfNotExists() throws Exception {
        SequenceManager seqManager = new SequenceManager(getIntactContext().getConfig().getDefaultDataConfig());

        Assert.assertFalse(seqManager.sequenceExists(getDaoFactory().getEntityManager(), "lala_seq"));

        beginTransaction();
        seqManager.createSequenceIfNotExists(getDaoFactory().getEntityManager(), "lala_seq");
        commitTransaction();

        Assert.assertTrue(seqManager.sequenceExists(getDaoFactory().getEntityManager(), "lala_seq"));
    }

    @Test
    public void getNextValueForSequence() throws Exception {
        SequenceManager seqManager = new SequenceManager(getIntactContext().getConfig().getDefaultDataConfig());

        beginTransaction();
        seqManager.createSequenceIfNotExists(getDaoFactory().getEntityManager(), "test2_seq", 5);
        commitTransaction();

        Assert.assertEquals(5L, seqManager.getNextValueForSequence(getDaoFactory().getEntityManager(), "test2_seq"));
        Assert.assertEquals(6L, seqManager.getNextValueForSequence(getDaoFactory().getEntityManager(), "test2_seq"));
        Assert.assertEquals(7L, seqManager.getNextValueForSequence(getDaoFactory().getEntityManager(), "test2_seq"));
        Assert.assertEquals(8L, seqManager.getNextValueForSequence(getDaoFactory().getEntityManager(), "test2_seq"));
        Assert.assertEquals(9L, seqManager.getNextValueForSequence(getDaoFactory().getEntityManager(), "test2_seq"));
        Assert.assertEquals(10L, seqManager.getNextValueForSequence(getDaoFactory().getEntityManager(), "test2_seq"));
    }
}
