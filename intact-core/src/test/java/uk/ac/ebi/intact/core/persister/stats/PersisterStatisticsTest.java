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
package uk.ac.ebi.intact.core.persister.stats;

import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterStatisticsTest extends IntactBasicTestCase {

    private PersisterStatistics stats;

    @Before
    public void init() {
        stats = new PersisterStatistics();
    }

    @After
    public void after() {
        stats = null;
    }

    @Test
    public void addPersisted() throws Exception {
        stats.addPersisted(getMockBuilder().createDeterministicInteraction());

        Assert.assertEquals(1, stats.getPersistedMap().size());
        Assert.assertEquals(1, stats.getPersistedCount(InteractionImpl.class, false));
        Assert.assertEquals(1, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(0, stats.getPersistedCount(Interactor.class, false));
    }
    
    @Test
    public void addMerged() throws Exception {
        stats.addMerged(getMockBuilder().createDeterministicInteraction());

        Assert.assertEquals(1, stats.getMergedMap().size());
        Assert.assertEquals(1, stats.getMergedCount(InteractionImpl.class, false));
        Assert.assertEquals(1, stats.getMergedCount(Interaction.class, true));
        Assert.assertEquals(0, stats.getMergedCount(Interactor.class, false));
    }

    @Test
    public void addDuplicate() throws Exception {
        stats.addDuplicate(getMockBuilder().createDeterministicInteraction());

        Assert.assertEquals(1, stats.getDuplicatesMap().size());
        Assert.assertEquals(1, stats.getDuplicatesCount(InteractionImpl.class, false));
        Assert.assertEquals(1, stats.getDuplicatesCount(Interaction.class, true));
        Assert.assertEquals(0, stats.getDuplicatesCount(Interactor.class, false));
    }

    @Test
    public void addTransient() throws Exception {
        stats.addTransient(getMockBuilder().createDeterministicInteraction());

        Assert.assertEquals(1, stats.getTransientMap().size());
        Assert.assertEquals(1, stats.getTransientCount(InteractionImpl.class, false));
        Assert.assertEquals(1, stats.getTransientCount(Interaction.class, true));
        Assert.assertEquals(0, stats.getTransientCount(Interactor.class, false));
    }


}