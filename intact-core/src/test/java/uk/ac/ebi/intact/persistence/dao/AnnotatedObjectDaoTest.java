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
import org.hibernate.LazyInitializationException;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.persister.CorePersister;
import uk.ac.ebi.intact.core.persister.finder.DefaultFinder;
import uk.ac.ebi.intact.core.persister.finder.NonCrcLegacyFinder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.InteractorImpl;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectDaoTest extends IntactBasicTestCase {

    @Test (expected = LazyInitializationException.class)
    public void getByAc_doNotPrefetch() throws Exception {
        getDataContext().beginTransaction();

        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction);

        getDataContext().commitTransaction();
        getDaoFactory().getEntityManager().close();

        getDataContext().beginTransaction();
        Interactor protein = getDaoFactory().getInteractorDao().getByShortLabel("fooprey");
        String ac = protein.getAc();
        getDataContext().commitTransaction();
        getDaoFactory().getEntityManager().close();

        getDataContext().beginTransaction();
        Interactor protWithAc = getDaoFactory().getInteractorDao().getByAc(ac);
        getDataContext().commitTransaction();
        getDaoFactory().getEntityManager().close();

        System.out.println(protWithAc.getXrefs());
    }
    
    @Test
    public void getByAc_prefetch() throws Exception {
        getDataContext().beginTransaction();

        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction);

        getDataContext().commitTransaction();
        getDaoFactory().getEntityManager().close();

        getDataContext().beginTransaction();
        Interactor protein = getDaoFactory().getInteractorDao().getByShortLabel("fooprey");
        String ac = protein.getAc();
        getDataContext().commitTransaction();
        getDaoFactory().getEntityManager().close();

        getDataContext().beginTransaction();
        Interactor protWithAc = getDaoFactory().getInteractorDao().getByAc(ac, true);
        getDataContext().commitTransaction();
        getDaoFactory().getEntityManager().close();

        Assert.assertEquals(1, protWithAc.getXrefs().size());
    }


}