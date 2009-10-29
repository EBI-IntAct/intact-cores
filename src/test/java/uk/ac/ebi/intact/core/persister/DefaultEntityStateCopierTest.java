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
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.clone.IntactCloner;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public class DefaultEntityStateCopierTest extends IntactBasicTestCase {

    @Test
    public void copyCollection() throws Exception {
        Collection<Integer> source = new ArrayList<Integer>();
        Collection<Integer> target = new ArrayList<Integer>();

        source.add( 1 );
        source.add( 2 );
        source.add( 3 );

        target.add( 3 );

        DefaultEntityStateCopier desc = new DefaultEntityStateCopier();

        desc.copyCollection( source,target );

        Assert.assertEquals(3, target.size());
        Assert.assertTrue( target.contains( 1 ));
        Assert.assertTrue( target.contains( 2 ));
        Assert.assertTrue( target.contains( 3 ));

        Assert.assertTrue( CollectionUtils.isEqualCollection(source, target));
    }

    @Test
    public void copyCollection_2() throws Exception {
        Collection<Integer> source = new ArrayList<Integer>();
        Collection<Integer> target = new ArrayList<Integer>();

        source.add( 1 );

        target.add( 1 );
        target.add( 2 );
        target.add( 3 );

        DefaultEntityStateCopier desc = new DefaultEntityStateCopier();

        desc.copyCollection( source,target );

        Assert.assertEquals(1, target.size());
        Assert.assertTrue( target.contains( 1 ));

        Assert.assertTrue( CollectionUtils.isEqualCollection(source, target));
    }

    @Test
    public void copyCollection_3() throws Exception {
        Collection<Integer> source = new ArrayList<Integer>();
        Collection<Integer> target = new ArrayList<Integer>();

        source.add( 1 );
        source.add( 2 );
        source.add( 3 );

        target.add( 1 );
        target.add( 2 );
        target.add( 3 );

        DefaultEntityStateCopier desc = new DefaultEntityStateCopier();

        desc.copyCollection( source,target );

        Assert.assertEquals(3, target.size());
        Assert.assertTrue( target.contains( 1 ));
        Assert.assertTrue( target.contains( 2 ));
        Assert.assertTrue( target.contains( 3 ));

        Assert.assertTrue( CollectionUtils.isEqualCollection(source, target));
    }


    @Test
    public void editBioSource() throws Exception {
        Assert.assertEquals( 0, getDaoFactory().getBioSourceDao().countAll() );

        Interaction source = getMockBuilder().createInteractionRandomBinary();
        source.setShortLabel( "binaryTest" );
        BioSource mouse = getMockBuilder().createBioSource( 10090, "mouse" );
        source.setBioSource( mouse );
        Assert.assertNotNull( source.getBioSource() );
        Assert.assertEquals( "mouse", source.getBioSource().getShortLabel() );

        getCorePersister().saveOrUpdate( source );
        Assert.assertEquals( 4, getDaoFactory().getBioSourceDao().countAll() );

        IntactCloner cloner = new IntactCloner();
        Interaction target = cloner.cloneInteraction( source );
        Assert.assertNull( target.getAc() );
        BioSource clonedMouse = cloner.cloneBioSource( mouse );
        Assert.assertNull( clonedMouse.getAc() );
        Assert.assertEquals( 4, getDaoFactory().getBioSourceDao().countAll() );

        //same taxid different shortlabel
        Assert.assertEquals( mouse.getTaxId(), clonedMouse.getTaxId() );
        clonedMouse.setShortLabel( "mouseUpdated" );
        Assert.assertNotSame( mouse.getShortLabel(), clonedMouse.getShortLabel() );

        target.setBioSource( clonedMouse );

        //before copying
        Assert.assertEquals( "mouse", source.getBioSource().getShortLabel() );
        Assert.assertEquals( "mouseUpdated", target.getBioSource().getShortLabel() );

        DefaultEntityStateCopier copier = new DefaultEntityStateCopier();
        copier.copyInteractorCommons( source, target );

        //after copying
        Assert.assertEquals( "mouse", source.getBioSource().getShortLabel() );
        Assert.assertEquals( "mouse", target.getBioSource().getShortLabel() );

        //copying from source to target successfull as taxid was different and

        //changing the taxid and shortlabel

        mouse.setTaxId( "10091" );
        mouse.setShortLabel( "mouseUpdatedAgain" );
        source.setBioSource( mouse );
        copier.copyInteractorCommons( source, target );

        //After copying again
        Assert.assertEquals( "mouseUpdatedAgain", source.getBioSource().getShortLabel() );
        Assert.assertEquals( "mouseUpdatedAgain", target.getBioSource().getShortLabel() );


        getCorePersister().saveOrUpdate( source );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().countAll() );
        Assert.assertEquals( 4, getDaoFactory().getBioSourceDao().countAll() );

        InteractionImpl interaction = getDaoFactory().getInteractionDao().getAll().iterator().next();
        Assert.assertEquals( "binarytest", interaction.getShortLabel() );
        Assert.assertEquals( "mouseupdatedagain", interaction.getBioSource().getShortLabel() );


    }

}
