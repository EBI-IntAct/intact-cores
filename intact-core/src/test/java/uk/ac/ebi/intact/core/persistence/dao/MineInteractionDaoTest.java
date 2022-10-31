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
package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MineInteractionDaoTest extends IntactBasicTestCase {

    @Test
    public void testGet() throws Exception {
        Protein prot1 = getMockBuilder().createProteinRandom();
        Protein prot2 = getMockBuilder().createProteinRandom();

        Interaction interaction = getMockBuilder().createInteraction( prot1, prot2 );

        CvInteraction detMethod = getMockBuilder().createCvObject( CvInteraction.class, CvInteraction.COSEDIMENTATION_MI_REF, CvInteraction.COSEDIMENTATION );

        getCorePersister().saveOrUpdate( interaction );
        getCorePersister().saveOrUpdate( detMethod );

        MineInteraction newMi = new MineInteraction( ( ProteinImpl ) prot1, ( ProteinImpl ) prot2, ( InteractionImpl ) interaction );
        newMi.setDetectionMethod( detMethod );
        newMi.setGraphId( 5 );
        newMi.setExperiment( interaction.getExperiments().iterator().next() );

        getDaoFactory().getMineInteractionDao().persist( newMi );
        //getEntityManager().flush();

        String ac1 = prot1.getAc();
        String ac2 = prot2.getAc();

        MineInteraction mineInt = getDaoFactory().getMineInteractionDao().get( ac1, ac2 );
        MineInteraction mineIntSame = getDaoFactory().getMineInteractionDao().get( ac2, ac1 );
        Assert.assertNotNull( mineInt );
        Assert.assertEquals( mineInt, mineIntSame );
    }

}
