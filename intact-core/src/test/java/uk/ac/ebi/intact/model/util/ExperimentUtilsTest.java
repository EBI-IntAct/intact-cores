/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentUtilsTest extends IntactBasicTestCase {

    @Test
    public void syncShortLabelWithDb() throws Exception {
        getIntactContext().getConfig().setAutoUpdateExperimentLabel(false);
        Experiment exp1 = getMockBuilder().createExperimentEmpty("lala-2011-1", "1234567");
        Experiment exp2 = getMockBuilder().createExperimentEmpty("lala-2011-2", "1234567");
		Experiment exp3 = getMockBuilder().createExperimentEmpty("lala-2011-3", "1234567");
		Experiment exp4 = getMockBuilder().createExperimentEmpty("lala-2011-4", "1234567");
		Experiment exp5 = getMockBuilder().createExperimentEmpty("lala-2011-5", "1234567");
		Experiment exp6 = getMockBuilder().createExperimentEmpty("lala-2011-6", "1234567");
		Experiment exp7 = getMockBuilder().createExperimentEmpty("lala-2011-7", "1234567");
		Experiment exp8 = getMockBuilder().createExperimentEmpty("lala-2011-8", "1234567");
		Experiment exp9 = getMockBuilder().createExperimentEmpty("lala-2011-9", "1234567");
		Experiment exp10 = getMockBuilder().createExperimentEmpty("lala-2011-10", "1234567");
		Experiment exp11 = getMockBuilder().createExperimentEmpty("lala-2011-11", "1234567");
        Experiment exp12 = getMockBuilder().createExperimentEmpty("lala-2011-12", "1234567");

		Experiment exp1a = getMockBuilder().createExperimentEmpty("lala-2011a-1", "1234568");
		Experiment exp2a = getMockBuilder().createExperimentEmpty("lala-2011a-2", "1234568");
		Experiment exp3a = getMockBuilder().createExperimentEmpty("lala-2011a-3", "1234568");
		Experiment exp4a = getMockBuilder().createExperimentEmpty("lala-2011a-4", "1234568");
		Experiment exp5a = getMockBuilder().createExperimentEmpty("lala-2011a-5", "1234568");
		Experiment exp6a = getMockBuilder().createExperimentEmpty("lala-2011a-6", "1234568");
		Experiment exp7a = getMockBuilder().createExperimentEmpty("lala-2011a-7", "1234568");
		Experiment exp8a = getMockBuilder().createExperimentEmpty("lala-2011a-8", "1234568");
		Experiment exp9a = getMockBuilder().createExperimentEmpty("lala-2011a-9", "1234568");
		Experiment exp10a = getMockBuilder().createExperimentEmpty("lala-2011a-10", "1234568");
		Experiment exp11a = getMockBuilder().createExperimentEmpty("lala-2011a-11", "1234568");
		Experiment exp12a = getMockBuilder().createExperimentEmpty("lala-2011a-12", "1234568");

		getCorePersister().saveOrUpdate(exp1, exp2, exp3, exp4, exp5, exp6, exp7, exp8, exp9, exp10, exp11, exp12);
		getCorePersister().saveOrUpdate(exp1a, exp2a, exp3a, exp4a, exp5a, exp6a, exp7a, exp8a, exp9a, exp10a, exp11a, exp12a);

		getIntactContext().getConfig().setAutoUpdateExperimentLabel(true);

        Assert.assertEquals(24, getDaoFactory().getExperimentDao().countAll());
        Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-1"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-2"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-3"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-4"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-5"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-6"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-7"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-8"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-9"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-10"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-11"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-12"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-1"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-2"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-3"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-4"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-5"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-6"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-7"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-8"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-9"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-10"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-11"));
		Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011a-12"));


		Assert.assertEquals("lala-2011-13", ExperimentUtils.syncShortLabelWithDb("lala-2011", "1234567"));
    }
}
