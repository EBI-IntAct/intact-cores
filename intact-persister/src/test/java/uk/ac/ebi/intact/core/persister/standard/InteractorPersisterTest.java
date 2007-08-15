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
package uk.ac.ebi.intact.core.persister.standard;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.model.Interactor;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorPersisterTest extends AbstractPersisterTest {

    @Test
    public void aliasPersisted() throws Exception {
        Interactor interactor = getMockBuilder().createProteinRandom();

        InteractorPersister interactorPersister = InteractorPersister.getInstance();

        interactorPersister.saveOrUpdate(interactor);
        interactorPersister.commit();

        beginTransaction();

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

}