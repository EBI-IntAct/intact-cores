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
package uk.ac.ebi.intact.persistence.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.persister.CorePersister;
import uk.ac.ebi.intact.core.persister.finder.DefaultFinder;
import uk.ac.ebi.intact.core.persister.finder.NonCrcLegacyFinder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Arrays;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorDaoTest extends IntactBasicTestCase {

    @Test
    public void countAllInteractors() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        PersisterHelper.saveOrUpdate(getMockBuilder().createDeterministicInteraction());
        Assert.assertEquals(2, getDaoFactory().getInteractorDao().countAllInteractors());
    }

    @Test
    public void getInteractors() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        PersisterHelper.saveOrUpdate(getMockBuilder().createDeterministicInteraction());
        Assert.assertEquals(2, getDaoFactory().getInteractorDao().getInteractors(0, 5).size());
    }
}