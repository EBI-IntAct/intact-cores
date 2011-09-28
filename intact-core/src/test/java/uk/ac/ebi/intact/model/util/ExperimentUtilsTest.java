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
        Experiment exp2 = getMockBuilder().createExperimentEmpty("lala-2011-5", "1234567");

        getCorePersister().saveOrUpdate(exp1, exp2);

        getIntactContext().getConfig().setAutoUpdateExperimentLabel(true);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-1"));
        Assert.assertNotNull(getDaoFactory().getExperimentDao().getByShortLabel("lala-2011-5"));

        // TODO I think this is a design problem, ideally it should be lala-2011-6, just in case -3, -4... do not exist
        Assert.assertEquals("lala-2011-3", ExperimentUtils.syncShortLabelWithDb("lala-2011", "1234567"));
    }

}
