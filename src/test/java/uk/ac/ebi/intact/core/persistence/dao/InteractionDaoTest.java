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
package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDaoTest extends IntactBasicTestCase {

    @Autowired
    private PersisterHelper persisterHelper;
    
    @Test
    public void getByInteractorsPrimaryId_exact() throws Exception {
        final IntactMockBuilder mockBuilder = getMockBuilder();
        Interaction mockInteraction = mockBuilder.createInteractionRandomBinary();
        mockInteraction.getComponents().clear();

        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A1", "prot1" ) ) );
        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A2", "prot2" ) ) );

        getCorePersister().saveOrUpdate( mockInteraction );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().countAll() );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1", "A2" ).size() );
        Assert.assertEquals( 0, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1" ).size() );
        Assert.assertEquals( 0, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A2" ).size() );
    }

    @Test
    public void getByInteractorsPrimaryId_exact2() throws Exception {
        final IntactMockBuilder mockBuilder = getMockBuilder();
        Interaction mockInteraction = mockBuilder.createInteractionRandomBinary();
        mockInteraction.getComponents().clear();

        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A1", "prot1" ) ) );
        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A2", "prot2" ) ) );
        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A3", "prot3" ) ) );

        getCorePersister().saveOrUpdate( mockInteraction );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().countAll() );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1", "A2", "A3" ).size() );
        Assert.assertEquals( 0, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1", "A2" ).size() );
        Assert.assertEquals( 0, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1", "B9" ).size() );
    }

    @Test
    public void getByInteractorsPrimaryId_notExact() throws Exception {
        final IntactMockBuilder mockBuilder = getMockBuilder();
        Interaction mockInteraction = mockBuilder.createInteractionRandomBinary();
        mockInteraction.getComponents().clear();

        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A1", "prot1" ) ) );
        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A2", "prot2" ) ) );
        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A3", "prot3" ) ) );

        getCorePersister().saveOrUpdate( mockInteraction );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().countAll() );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( false, "A1", "A2", "A3" ).size() );
        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( false, "A1", "A2" ).size() );
        Assert.assertEquals( 0, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( false, "A1", "B9" ).size() );
    }

    @Test
    public void getByInteractorsPrimaryId_self() throws Exception {
        final IntactMockBuilder mockBuilder = getMockBuilder();
        Interaction mockInteraction = mockBuilder.createInteractionRandomBinary();
        mockInteraction.getComponents().clear();

        mockInteraction.getComponents().add( mockBuilder
                .createComponentNeutral( mockInteraction, mockBuilder.createProtein( "A1", "prot1" ) ) );

        getCorePersister().saveOrUpdate( mockInteraction );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().countAll() );

        Assert.assertEquals( 0, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1", "A1" ).size() );
        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId( true, "A1" ).size() );
    }

    @Test
    public void getByInteractorsPrimaryId_noComponents() throws Exception {
        final IntactMockBuilder mockBuilder = getMockBuilder();
        Interaction mockInteraction = mockBuilder.createInteractionRandomBinary();
        mockInteraction.getComponents().clear();

        getCorePersister().saveOrUpdate( mockInteraction );

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().countAll() );
    }

    @Test
    public void getByInteractorsPrimaryId_14Component() throws Exception {
        String[] all = new String[]{"NP_013618", "NP_010928", "NP_015428", "NP_009512",
                "NP_012533", "NP_011651", "NP_014604", "NP_011769", "NP_014045", "NP_015007",
                "NP_011504", "NP_014800", "NP_011020", "NP_116708"};

        for (int i = 4; i < 14; i++) {
            String[] primaryIds = Arrays.asList(all).subList(0, i).toArray(new String[i]);
            Interaction interaction = getMockBuilder().createInteraction(primaryIds);

            getCorePersister().saveOrUpdate(interaction);
            Assert.assertEquals(1, getDaoFactory().getInteractionDao().getByInteractorsPrimaryId(true, primaryIds).size());
        }
    }

    @Test
    public void getInteractionsForProtPairAc() throws Exception {
        Protein p1 = getMockBuilder().createProteinRandom();
        Protein p2 = getMockBuilder().createProteinRandom();

        Interaction interaction = getMockBuilder().createInteraction(p1, p2);

        getCorePersister().saveOrUpdate(p1, p2, interaction);

        getEntityManager().flush();
        getEntityManager().clear();

        final List<Interaction> interactions = getDaoFactory().getInteractionDao().getInteractionsForProtPairAc(p1.getAc(), p2.getAc());
        Assert.assertEquals(1, interactions.size());

        final List<Interaction> interactions2 = getDaoFactory().getInteractionDao().getInteractionsForProtPairAc(p1.getAc(), "lalalaAC");
        Assert.assertEquals(0, interactions2.size());
    }

    @Test
    public void getInteractionsForProtPairAc_oneSecondary() throws Exception {
        Protein p1 = getMockBuilder().createProteinRandom();
        Protein p2 = getMockBuilder().createProteinRandom();

        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        CvXrefQualifier secondaryId = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);

        p2.getXrefs().add(getMockBuilder().createXref(p2, "TEST-0000", secondaryId, intact));

        Interaction interaction = getMockBuilder().createInteraction(p1, p2);

        getCorePersister().saveOrUpdate(p1, p2, interaction);

        getEntityManager().flush();
        getEntityManager().clear();

        final List<Interaction> interactions = getDaoFactory().getInteractionDao().getInteractionsForProtPairAc(p1.getAc(), p2.getAc());
        Assert.assertEquals(1, interactions.size());

        final List<Interaction> interactions2 = getDaoFactory().getInteractionDao().getInteractionsForProtPairAc(p1.getAc(), "TEST-0000");
        Assert.assertEquals(1, interactions2.size());
    }

    @Test
    @Ignore
    public void getByLastImexUpdate() throws Exception {
        final Interaction i1 = getMockBuilder().createInteractionRandomBinary();
        i1.setLastImexUpdate( parseDate( "2009-11-01" ) );

        final Interaction i2 = getMockBuilder().createInteractionRandomBinary();
        i2.setLastImexUpdate( parseDate( "2009-11-05" ) );

        final Interaction i3 = getMockBuilder().createInteractionRandomBinary();
        i3.setLastImexUpdate( parseDate( "2009-11-09" ) );

        final Interaction i4 = getMockBuilder().createInteractionRandomBinary();
        i4.setLastImexUpdate( null );

        final Interaction i5 = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate( i1, i2, i3, i4, i5 );

        assertInteractionCountByLastImexUpdate( 0, "2009-08-01", "2009-09-01" );
        assertInteractionCountByLastImexUpdate( 3, "2009-08-01", "2010-09-01" );
        assertInteractionCountByLastImexUpdate( 0, "2009-08-01", "2009-09-01" );
        assertInteractionCountByLastImexUpdate( 1, "2009-08-01", "2009-09-02" );
    }

    private void assertInteractionCountByLastImexUpdate( int expectedInteractionCount,
                                                         String fromDate, String toDate ) throws ParseException {
        final InteractionDao interactionDao = getDaoFactory().getInteractionDao();
        List<Interaction> interactions = interactionDao.getByLastImexUpdate( parseDate(fromDate),
                                                                             parseDate(toDate) );
        junit.framework.Assert.assertNotNull( interactions );
        junit.framework.Assert.assertEquals( "Expected to find " + expectedInteractionCount +
                             " interaction(s) having lastImexUpdate between " + fromDate + " and " +
                             toDate + ", instead found " + interactions.size() ,
                             expectedInteractionCount, interactions.size() );
    }

    private Date parseDate( String dateStr ) throws ParseException {
        return new SimpleDateFormat( "yyyy-mm-dd" ).parse( dateStr );
    }

    @Test
    public void getByExperimentAc() throws Exception {
        final Experiment e = getMockBuilder().createExperimentRandom( 23 );
        getCorePersister().saveOrUpdate( e );

        List<Interaction> interactions = getDaoFactory().getInteractionDao().getByExperimentAc( e.getAc(), 0, 5 );
        Assert.assertNotNull( interactions );
        Assert.assertEquals( 5, interactions.size() ); // 0, 1, 2, 3, 4

        interactions = getDaoFactory().getInteractionDao().getByExperimentAc( e.getAc(), 5, 5 );
        Assert.assertNotNull( interactions );
        Assert.assertEquals( 5, interactions.size() ); // 5, 6, 7, 8, 9

        interactions = getDaoFactory().getInteractionDao().getByExperimentAc( e.getAc(), 20, 5 );
        Assert.assertNotNull( interactions );
        Assert.assertEquals( 3, interactions.size() ); // 20, 21, 22
    }
}