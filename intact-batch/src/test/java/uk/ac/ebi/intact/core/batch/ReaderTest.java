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
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persister.CorePersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import javax.annotation.Resource;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:/META-INF/intact.spring.xml",
        "classpath*:/META-INF/intact-test.spring.xml"
})
@Transactional(propagation = Propagation.NEVER)
public class ReaderTest {

    @Resource(name = "intactBatchJobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    private ApplicationContext applicationContext;

    private IntactMockBuilder mockBuilder;

    @Before
    public void before() {
        mockBuilder = new IntactMockBuilder(getIntactContext().getConfig().getDefaultInstitution());
        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        counter.reset();
    }

    @Test
    @DirtiesContext
    public void readInteractions() throws Exception {
        Experiment exp = mockBuilder.createExperimentRandom(5);
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

        Experiment exp = mockBuilder.createExperimentRandom(5);

        Interaction negativeInt = exp.getInteractions().iterator().next();
        negativeInt.addAnnotation(mockBuilder.createAnnotation("yes", "IA:xxx", CvTopic.NEGATIVE));

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
            getCorePersister().saveOrUpdate(mockBuilder.createExperimentEmpty());
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
        Interaction interaction = mockBuilder.createInteractionRandomBinary();
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
        Interaction interaction = mockBuilder.createInteractionRandomBinary();
        Protein prot = mockBuilder.createProteinRandom();
        getCorePersister().saveOrUpdate(interaction, prot);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countInteractorInvolvedInInteraction());

        Job job = (Job) applicationContext.getBean("readInteractorsOnlyInteractingJob");

        jobLauncher.run(job, new JobParameters());

        IntactObjectCounterWriter counter = (IntactObjectCounterWriter) applicationContext.getBean("intactObjectCounterWriter");
        Assert.assertEquals(2, counter.getCount());
    }

    protected IntactContext getIntactContext() {
        return (IntactContext) applicationContext.getBean("intactContext");
    }

    protected DataContext getDataContext() {
        return getIntactContext().getDataContext();
    }

    public CorePersister getCorePersister() {
        return getIntactContext().getCorePersister();
    }

    protected DaoFactory getDaoFactory() {
        return getDataContext().getDaoFactory();
    }
}
