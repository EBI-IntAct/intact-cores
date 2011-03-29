/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.batch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import javax.annotation.Resource;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Transactional(propagation = Propagation.NEVER)
public class ReaderTest extends IntactBasicTestCase {

    @Resource(name = "intactBatchJobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void before() {
        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        counter.reset();
    }

    @Test
    @DirtiesContext
    public void readInteractions() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(5);
        getCorePersister().saveOrUpdate(exp);

        Assert.assertEquals(5, getDaoFactory().getInteractionDao().countAll());

        Job job = (Job) applicationContext.getBean("readInteractionsJob");

        jobLauncher.run(job, new JobParameters());

        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        Assert.assertEquals(5, counter.getCount());
    }

    @Test
    @DirtiesContext
    public void readInteractionsNegative() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractionDao().countAll());

        Experiment exp = getMockBuilder().createExperimentRandom(5);

        Interaction negativeInt = exp.getInteractions().iterator().next();
        negativeInt.addAnnotation(getMockBuilder().createAnnotation("yes", "IA:xxx", CvTopic.NEGATIVE));

        getCorePersister().saveOrUpdate(exp);

        Assert.assertEquals(5, getDaoFactory().getInteractionDao().countAll());

        Job job = (Job) applicationContext.getBean("readInteractionsNonNegativeJob");

        jobLauncher.run(job, new JobParameters());

        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        Assert.assertEquals(4, counter.getCount());
    }
    
    @Test
    @DirtiesContext
    public void readExperiments() throws Exception {
        for (int i=0; i<4; i++) {
            getCorePersister().saveOrUpdate(getMockBuilder().createExperimentEmpty());
        }

        Assert.assertEquals(4, getDaoFactory().getExperimentDao().countAll());

        Job job = (Job) applicationContext.getBean("readExperimentsJob");

        jobLauncher.run(job, new JobParameters());

        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        Assert.assertEquals(4, counter.getCount());
    }

    @Test
    @DirtiesContext
    public void readInteractors() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());

        Job job = (Job) applicationContext.getBean("readInteractorsJob");

        jobLauncher.run(job, new JobParameters());

        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        Assert.assertEquals(2, counter.getCount());
    }

    @Test
    @DirtiesContext
    public void readInteractors_excludeNonInteracting() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        Protein prot = getMockBuilder().createProteinRandom();
        getCorePersister().saveOrUpdate(interaction, prot);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());

        Job job = (Job) applicationContext.getBean("readInteractorsOnlyInteractingJob");

        jobLauncher.run(job, new JobParameters());

        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        Assert.assertEquals(2, counter.getCount());
    }

}
