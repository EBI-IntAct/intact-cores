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
package uk.ac.ebi.intact.persistence.dao;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

import java.util.Iterator;
import java.util.List;

/**
 * ExperimentDao Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentDaoTest extends IntactBasicTestCase {

    @After
    public void end() throws Exception {
        // nothing
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        Experiment exp1 = mockBuilder.createExperimentRandom( "thoden-1999-1", 1 );
        Experiment exp2 = mockBuilder.createExperimentRandom( "kerrien-2007-1", 2 );
        Experiment exp3 = mockBuilder.createExperimentRandom( "baranda-2007-1", 3 );
        Experiment exp4 = mockBuilder.createExperimentEmpty( "lala-2014-5", "1234567" );
        Experiment exp5 = mockBuilder.createExperimentEmpty( "guru-1974-1" );
        Experiment exp6 = mockBuilder.createExperimentEmpty( "lolo-2001-1" );

        PersisterHelper.saveOrUpdate( exp1, exp2, exp3, exp4, exp5, exp6 );
    }

    @AfterClass
    public static void afterClass() throws Exception {
        IntactContext.closeCurrentInstance();
    }

    @Test
    public void getAllScrolled() throws Exception {
        Iterator<Experiment> expIter = getDaoFactory().getExperimentDao().getAllIterator();

        int i = 0;

        while ( expIter.hasNext() ) {
            Experiment exp = expIter.next();
            i++;
        }

        assertEquals( 6, i );
    }

    @Test
    public void getInteractionsForExperimentWithAcScroll() throws Exception {
        Experiment exp = getDaoFactory().getExperimentDao().getByShortLabel( "thoden-1999-1" );
        Iterator<Interaction> expInteraction =
                getDaoFactory().getExperimentDao().getInteractionsForExperimentWithAcIterator( exp.getAc() ); //giot

        int i = 0;

        while ( expInteraction.hasNext() ) {
            expInteraction.next();
            i++;
        }

        assertEquals( exp.getInteractions().size(), i );
    }

    @Test
    public void countInteractionsForExperimentWithAc() {
        Experiment exp = getDaoFactory().getExperimentDao().getByShortLabel( "thoden-1999-1" );
        String ac = exp.getAc();
        int interactionsCount = getDaoFactory().getExperimentDao().countInteractionsForExperimentWithAc( ac );
        assertEquals( 1, interactionsCount );
    }

    @Test
    public void getInteractionsForExperimentWithAc() {
        Experiment exp = getDaoFactory().getExperimentDao().getByShortLabel( "thoden-1999-1" );
        String ac = exp.getAc();
        List<Interaction> interactions = getDaoFactory().getExperimentDao().getInteractionsForExperimentWithAc( ac, 0, 50 );
        assertEquals( 1, interactions.size() );
    }

    @Test
    public void saveAnnotation() throws Exception {

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final ExperimentDao edao = daoFactory.getExperimentDao();
        final Iterator<Experiment> iterator = edao.getAllIterator();
        Assert.assertTrue( iterator.hasNext() );

        Experiment e = iterator.next();
        Assert.assertNotNull( e );

        final int eSize = e.getAnnotations().size();

        Institution owner = IntactContext.getCurrentInstance().getConfig().getInstitution();
        CvTopic topic = new CvTopic( owner, "topic" );
        daoFactory.getCvObjectDao( CvTopic.class ).persist( topic );
        Assert.assertNotNull( topic.getAc() );

        Annotation annotation = new Annotation( owner, topic, "lala" );
        e.addAnnotation( annotation );

        edao.saveOrUpdate( e );
        final String ac = e.getAc();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        // now check that the new annotation has been persisted
        final Experiment e2 = edao.getByAc( ac );
        Assert.assertNotNull( e2 );
        Assert.assertTrue( e2.getAnnotations().size() > eSize );
    }

    @Test
    public void getByPubIdAndLabelLike() throws Exception {
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().getByPubIdAndLabelLike("1234567", "lala-2014%").size());
        Assert.assertEquals(0, getDaoFactory().getExperimentDao().getByPubIdAndLabelLike("1234567", "lala-2014").size()); 
    }
}
