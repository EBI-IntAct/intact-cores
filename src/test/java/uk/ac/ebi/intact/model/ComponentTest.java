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
package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.core.persister.PersisterHelper;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Component Tester
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentTest extends IntactBasicTestCase {

    private static final Log log = LogFactory.getLog( ComponentTest.class );


    @Test
    public void participantDetectionMethods() {
        Component component = getMockBuilder().createInteractionRandomBinary().getComponents().iterator().next();

        Assert.assertEquals( 1, component.getParticipantDetectionMethods().size() );
        Assert.assertSame( component.getParticipantIdentification(), component.getParticipantDetectionMethods().iterator().next() );

        component.getParticipantDetectionMethods().clear();

        Assert.assertTrue( component.getParticipantDetectionMethods().isEmpty() );
        Assert.assertNull( component.getParticipantIdentification() );

        component.setParticipantIdentification( getMockBuilder().createCvObject( CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED ) );

        Assert.assertEquals( 1, component.getParticipantDetectionMethods().size() );
        Assert.assertSame( component.getParticipantIdentification(), component.getParticipantDetectionMethods().iterator().next() );
    }


    @Test
    public void experimentalRoles() {

        Component component = getMockBuilder().createInteractionRandomBinary().getComponents().iterator().next();

        CvExperimentalRole bait = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        CvExperimentalRole prey = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY );
        CvExperimentalRole neutral = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );

        Collection<CvExperimentalRole> experimentalRoles = new ArrayList<CvExperimentalRole>();
        experimentalRoles.add( bait );
        experimentalRoles.add( prey );
        experimentalRoles.add( neutral );

        component.setExperimentalRoles( experimentalRoles );
        Assert.assertEquals( 3, component.getExperimentalRoles().size() );

        /*String[] schemaddl = SchemaUtils.generateCreateSchemaDDLForOracle();
          for(String ddl: schemaddl){
            if(ddl.contains("ia_component"))
            System.out.println(ddl );
        }*/
    }


    @Test
    public void onPersist_syncedLabel_single_experimentalRole() {

        Component baitComponent = getMockBuilder().createComponentBait( getMockBuilder().createDeterministicProtein( "P1", "baaa" ) );
        Component preyComponent1 = getMockBuilder().createComponentPrey( getMockBuilder().createDeterministicProtein( "P2", "paaa" ) );
        Component preyComponent2 = getMockBuilder().createComponentPrey( getMockBuilder().createDeterministicProtein( "P3", "pbbb" ) );
        Component preyComponent3 = getMockBuilder().createComponentPrey( getMockBuilder().createDeterministicProtein( "P4", "pccc" ) );

        // eg. 1. bait(baaa), prey(paaa, pbbb, pccc) should give us: baaa-paaa and baaa-paaa-1 the second time
        Interaction interaction = getMockBuilder().createInteraction( baitComponent, preyComponent1, preyComponent2, preyComponent3 );
        PersisterHelper.saveOrUpdate( interaction );
        Assert.assertEquals( "baaa-paaa", interaction.getShortLabel() );

        Interaction interactionDuplicated = getMockBuilder().createInteraction( baitComponent, preyComponent1, preyComponent2, preyComponent3 );
        PersisterHelper.saveOrUpdate( interactionDuplicated );
        Assert.assertEquals( "baaa-paaa-1", interactionDuplicated.getShortLabel() );


        Component neutraulComponent = getMockBuilder().createComponentNeutral( interaction, getMockBuilder().createDeterministicProtein( "P5", "naaa" ) );
        Interaction interaction2 = getMockBuilder().createInteraction( neutraulComponent );
        PersisterHelper.saveOrUpdate( interaction2 );
        Assert.assertEquals( "naaa", interaction2.getShortLabel() );

        //bait(baaa), prey(), neutral(naaa) should gives us: baaa-naaa
        Interaction interaction3 = getMockBuilder().createInteraction( baitComponent, neutraulComponent );
        PersisterHelper.saveOrUpdate( interaction3 );
        Assert.assertEquals( "baaa-naaa", interaction3.getShortLabel() );

        //bait(), prey(paaa, pbbb, pccc), neutral(naaa)
        //since bait is empty it throws an exception and it creates a predefined short label unk-unk in the catchblock of IntactMockBuilder..createInteraction(Component ... components)
        Interaction interaction4 = getMockBuilder().createInteraction( preyComponent1, preyComponent2, preyComponent3, neutraulComponent );
        PersisterHelper.saveOrUpdate( interaction4 );
        Assert.assertEquals( "unk-unk", interaction4.getShortLabel() );


    }

    @Test
    public void onPersist_syncedLabel_multiple_experimentalRoles() {

        CvExperimentalRole baitExperimentalRole = getMockBuilder().createCvObject(CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT);
        CvExperimentalRole preyExperimentalRole = getMockBuilder().createCvObject(CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY);
        CvExperimentalRole neutralExperimentalRole = getMockBuilder().createCvObject(CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL);

        Collection<CvExperimentalRole>  baitNeutralExperimentalRoles = new ArrayList<CvExperimentalRole>();
        baitNeutralExperimentalRoles.add( baitExperimentalRole );
        baitNeutralExperimentalRoles.add(neutralExperimentalRole );

        Collection<CvExperimentalRole>  preyNeutralExperimentalRoles = new ArrayList<CvExperimentalRole>();
        preyNeutralExperimentalRoles.add( preyExperimentalRole );
        preyNeutralExperimentalRoles.add( neutralExperimentalRole );

        Collection<CvExperimentalRole>  neutralExperimentalRoles = new ArrayList<CvExperimentalRole>();
        neutralExperimentalRoles.add( neutralExperimentalRole );

        Component baitComponent = getMockBuilder().createComponentBait( getMockBuilder().createDeterministicProtein( "P1", "baaa" ) );
        baitComponent.setExperimentalRoles(baitNeutralExperimentalRoles);

        Component preyComponent1 = getMockBuilder().createComponentPrey( getMockBuilder().createDeterministicProtein( "P2", "paaa" ) );
        preyComponent1.setExperimentalRoles(preyNeutralExperimentalRoles);

        Component preyComponent2 = getMockBuilder().createComponentPrey( getMockBuilder().createDeterministicProtein( "P3", "pbbb" ) );
        Component preyComponent3 = getMockBuilder().createComponentPrey( getMockBuilder().createDeterministicProtein( "P4", "pccc" ) );

        // eg. 1. bait(baaa)&neutral(naaa), prey(paaa)&neutral(naaa), prey(pbbb), prey(pccc) should gives us: baaa-paaa
        Interaction interaction = getMockBuilder().createInteraction( baitComponent, preyComponent1, preyComponent2, preyComponent3 );
        PersisterHelper.saveOrUpdate( interaction );
        Assert.assertEquals( "baaa-paaa", interaction.getShortLabel() );



    }


}