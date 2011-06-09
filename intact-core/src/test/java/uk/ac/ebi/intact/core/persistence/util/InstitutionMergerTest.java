/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persistence.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Interaction;

/**
 * TODO comment this class header.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionMergerTest extends IntactBasicTestCase {

    @Test
    public void testMerge() throws Exception {
        Institution sourceInstitution1 = getMockBuilder().createInstitution("IA:xxx1", "Lala1 Source Institute");
        Institution sourceInstitution2 = getMockBuilder().createInstitution("IA:xxx2", "Lala2 Source Institute");
        Institution destInstitution = getMockBuilder().createInstitution("IA:xxx3", "Lala Destination Institute");

        getCorePersister().saveOrUpdate(sourceInstitution1, sourceInstitution2, destInstitution);

        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        interaction1.setOwner(sourceInstitution1);

        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        interaction2.setOwner(sourceInstitution2);

        Interaction interaction3 = getMockBuilder().createInteractionRandomBinary();
        interaction3.setOwner(destInstitution);

        getCorePersister().saveOrUpdate(interaction1, interaction2, interaction3);

        getEntityManager().flush();
        getEntityManager().clear();

        InstitutionMerger merger = new InstitutionMerger();
        int updatedCount = merger.merge(new Institution[]{sourceInstitution1, sourceInstitution2}, destInstitution, false);

        Assert.assertEquals(2, updatedCount);

        getEntityManager().flush();
        getEntityManager().clear();


        Interaction reloadedInteraction1 = getDaoFactory().getInteractionDao().getByAc(interaction1.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction1.getOwner().getAc());

        Interaction reloadedInteraction2 = getDaoFactory().getInteractionDao().getByAc(interaction2.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction2.getOwner().getAc());

        Interaction reloadedInteraction3 = getDaoFactory().getInteractionDao().getByAc(interaction3.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction3.getOwner().getAc());

    }

    @Test
    public void testMerge2() throws Exception {
        Institution sourceInstitution1 = getMockBuilder().createInstitution("IA:xxx1", "Lala1 Source Institute");
        Institution sourceInstitution2 = getMockBuilder().createInstitution("IA:xxx2", "Lala2 Source Institute");
        Institution destInstitution = getMockBuilder().createInstitution("IA:xxx3", "Lala Destination Institute");

        getCorePersister().saveOrUpdate(sourceInstitution1, sourceInstitution2, destInstitution);

        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        interaction1.setOwner(sourceInstitution1);

        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        interaction2.setOwner(sourceInstitution2);

        Interaction interaction3 = getMockBuilder().createInteractionRandomBinary();
        interaction3.setOwner(destInstitution);

        getCorePersister().saveOrUpdate(interaction1, interaction2, interaction3);

        getEntityManager().flush();
        getEntityManager().clear();

        InstitutionMerger merger = new InstitutionMerger();
        int updatedCount = merger.merge(new Institution[]{sourceInstitution1, sourceInstitution2, destInstitution}, destInstitution, true);

        Assert.assertEquals(2, updatedCount);

        getEntityManager().flush();
        getEntityManager().clear();


        Interaction reloadedInteraction1 = getDaoFactory().getInteractionDao().getByAc(interaction1.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction1.getOwner().getAc());

        Interaction reloadedInteraction2 = getDaoFactory().getInteractionDao().getByAc(interaction2.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction2.getOwner().getAc());

        Interaction reloadedInteraction3 = getDaoFactory().getInteractionDao().getByAc(interaction3.getAc());
        Assert.assertEquals(destInstitution.getAc(), reloadedInteraction3.getOwner().getAc());

        Assert.assertNull(getDaoFactory().getInstitutionDao().getByAc(sourceInstitution1.getAc()));
        Assert.assertNull(getDaoFactory().getInstitutionDao().getByAc(sourceInstitution2.getAc()));
        Assert.assertNotNull(getDaoFactory().getInstitutionDao().getByAc(destInstitution.getAc()));

    }
}
