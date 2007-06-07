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

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Iterator;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentDaoTest extends TestCase
{
     private ExperimentDao experimentDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        experimentDao = null;
    }

    public void testGetAllScrolled() throws Exception
    {
        Iterator<Experiment> expIter = experimentDao.getAllIterator();

        int i=0;

        while (expIter.hasNext())
        {
            Experiment exp = expIter.next();
            i++;
        }

        assertTrue(i > 4000);
    }

    public void testGetInteractionsForExperimentWithAcScroll() throws Exception
    {
        Iterator<Interaction> expInteraction =
                experimentDao.getInteractionsForExperimentWithAcIterator("EBI-196429"); //giot

        int i=0;

        while (expInteraction.hasNext())
        {
            Interaction inter = expInteraction.next();
            System.out.println(inter.getAc());
            i++;
        }

    }

    public void testCountInteractionsForExperimentWithAc(){
        Experiment exp = experimentDao.getByShortLabel("thoden-1999-1");
        String ac = exp.getAc();
        int interactionsCount = experimentDao.countInteractionsForExperimentWithAc(ac);
        assertEquals(2,interactionsCount);
    }

    public void testGetInteractionsForExperimentWithAc(){
        Experiment exp = experimentDao.getByShortLabel("thoden-1999-1");
        String ac = exp.getAc();
        List<Interaction> interactions = experimentDao.getInteractionsForExperimentWithAc(ac,0,50);
        assertEquals(2, interactions.size());
    }

}
