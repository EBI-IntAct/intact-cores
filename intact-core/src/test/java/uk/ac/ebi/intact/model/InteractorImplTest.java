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
package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorImplTest extends IntactBasicTestCase {

    @Test
    public void correctObjClass1() throws Exception {
        InteractorImpl interactor = (InteractorImpl) getMockBuilder().createProteinRandom();

        Assert.assertEquals(ProteinImpl.class.getName(), interactor.getObjClass());
        interactor.correctObjClass();
        Assert.assertEquals(ProteinImpl.class.getName(), interactor.getObjClass());
    }

    @Test
    public void correctObjClass1_peptide() throws Exception {
        InteractorImpl interactor = (InteractorImpl) getMockBuilder().createPeptideRandom();

        Assert.assertEquals(ProteinImpl.class.getName(), interactor.getObjClass());
        interactor.correctObjClass();
        Assert.assertEquals(ProteinImpl.class.getName(), interactor.getObjClass());
    }
    
    @Test
    public void correctObjClass2() throws Exception {
        InteractorImpl interactor = (InteractorImpl) getMockBuilder().createProteinRandom();
        interactor.setCvInteractorType(getMockBuilder().createCvObject(CvInteractorType.class, "MI:0904", "polysaccharide"));

        Assert.assertEquals(ProteinImpl.class.getName(), interactor.getObjClass());
        interactor.correctObjClass();
        Assert.assertEquals(PolymerImpl.class.getName(), interactor.getObjClass());
    }

    @Test
    public void correctObjClass3() throws Exception {
        final CvInteractorType nucAcidType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.NUCLEIC_ACID_MI_REF, CvInteractorType.NUCLEIC_ACID);
        final CvInteractorType dnaType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.DNA_MI_REF, CvInteractorType.DNA);
        dnaType.addParent(nucAcidType);

        InteractorImpl interactor = new InteractorImpl("interactor", new Institution("nanaInst"), dnaType);

        Assert.assertEquals(InteractorImpl.class.getName(), interactor.getObjClass());
        interactor.correctObjClass();
        Assert.assertEquals(NucleicAcidImpl.class.getName(), interactor.getObjClass());
    }

    @Test
    public void correctObjClass4() throws Exception {
        final CvInteractorType interactorType = getMockBuilder().createCvObject(CvInteractorType.class, "MI:nana", "nanavirus");

        InteractorImpl interactor = new InteractorImpl("interactor", new Institution("nanaInst"), interactorType);

        Assert.assertEquals(InteractorImpl.class.getName(), interactor.getObjClass());
        interactor.correctObjClass();
        Assert.assertEquals(InteractorImpl.class.getName(), interactor.getObjClass());
    }

    @Test
    @DirtiesContext
    public void synchShortlabelEnabled() throws Exception {

        final Protein baitProtein = getMockBuilder().createProtein( "P12345", "bait" );
        final Component bait = getMockBuilder().createComponentBait( baitProtein );

        final Protein preyProtein = getMockBuilder().createProtein( "P12345", "prey" );
        final Component prey = getMockBuilder().createComponentBait( preyProtein );

        getIntactContext().getConfig().setAutoUpdateInteractionLabel( true );
        
        final Interaction interaction1 = getMockBuilder().createInteraction( bait, prey );
        interaction1.setShortLabel( "bait-prey" );
        getPersisterHelper().save( interaction1 );

        final Interaction interaction2 = getMockBuilder().createInteraction( bait, prey );
        interaction2.setShortLabel( "bait-prey" );
        getPersisterHelper().save( interaction2 );

        Assert.assertEquals( 2, getDaoFactory().getInteractionDao().countAll() );

        final InteractionImpl i1 = getDaoFactory().getInteractionDao().getByShortLabel( "bait-prey" );
        Assert.assertNotNull( i1 );

        final InteractionImpl i2 = getDaoFactory().getInteractionDao().getByShortLabel( "bait-prey-1" );
        Assert.assertNotNull( i2 );
    }

    @Test
    @DirtiesContext
    public void synchShortlabelDisabled() throws Exception {

        final Protein baitProtein = getMockBuilder().createProtein( "P12345", "bait" );
        final Component bait = getMockBuilder().createComponentBait( baitProtein );

        final Protein preyProtein = getMockBuilder().createProtein( "P12345", "prey" );
        final Component prey = getMockBuilder().createComponentBait( preyProtein );

        getIntactContext().getConfig().setAutoUpdateInteractionLabel( false );

        final String label = "bait-prey";
        
        final Interaction interaction1 = getMockBuilder().createInteraction( bait, prey );
        interaction1.setShortLabel( label );
        getPersisterHelper().save( interaction1 );

        final Interaction interaction2 = getMockBuilder().createInteraction( bait, prey );
        interaction2.setShortLabel( label );
        getPersisterHelper().save( interaction2 );

        Assert.assertEquals( 2, getDaoFactory().getInteractionDao().countAll() );

        final List<InteractionImpl> list = getDaoFactory().getInteractionDao().getAll();
        for ( InteractionImpl interaction : list ) {
            Assert.assertEquals( label, interaction.getShortLabel() );
        }
    }
}
