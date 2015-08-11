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

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.context.IntactContext;

import java.util.Collection;
import java.util.Arrays;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactJobRegistry implements ListableJobLocator {

    @Autowired
    private ApplicationContext applicationContext;

    public void unregister(String jobName) {

    }

    public void register(JobFactory jobFactory) throws DuplicateJobException {

    }

    public Job getJob(String name) throws NoSuchJobException {
        Job job = (Job) applicationContext.getBean(name);

        if (job == null) {
            throw new NoSuchJobException("IntactContext is not aware of this job: "+name);
        }

        return job;
    }

    public Collection<String> getJobNames() {
        return Arrays.asList(applicationContext.getBeanNamesForType(Job.class));
    }
}
