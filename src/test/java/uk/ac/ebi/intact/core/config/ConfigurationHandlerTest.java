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
package uk.ac.ebi.intact.core.config;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.meta.ApplicationProperty;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ConfigurationHandlerTest extends IntactBasicTestCase {

    @Autowired
    private ConfigurationHandler configurationHandler;

    @Autowired
    private IntactConfiguration intactConfiguration;

    @Test
    public void testLoadConfiguration() throws Exception {
        Application app = new Application("lalaApp", "laladesc");
        app.addProperty(new ApplicationProperty(app, "intactConfig.acPrefix", "LALA"));

        configurationHandler.loadConfiguration(app);

        Assert.assertEquals("LALA", intactConfiguration.getAcPrefix());

        Assert.assertEquals("lalaApp", app.getKey());
        Assert.assertEquals(6, app.getProperties().size());
    }

    @Test
    public void testPersistConfiguration() throws Exception {
        Application app = new Application("lalaApp", "laladesc");
        app.addProperty(new ApplicationProperty(app, "intactConfig.acPrefix", "LALA"));

        Assert.assertEquals( 1, getDaoFactory().getApplicationDao().countAll());

        getIntactContext().bindToApplication(app);

        Assert.assertEquals( 1, getDaoFactory().getApplicationDao().countAll());

        intactConfiguration.setDefaultInstitution((Institution) getSpringContext().getBean("institutionIntact"));

        configurationHandler.persistConfiguration();

        Assert.assertEquals(2, getDaoFactory().getApplicationDao().countAll());

        configurationHandler.persistConfiguration();

        Assert.assertEquals(2, getDaoFactory().getApplicationDao().countAll());

        Application refreshedApp = getDaoFactory().getApplicationDao().getByKey("lalaApp");

        Assert.assertEquals(6, refreshedApp.getProperties().size());
        Assert.assertEquals("LALA", refreshedApp.getProperty("intactConfig.acPrefix").getValue());
        Assert.assertEquals("intact", refreshedApp.getProperty("intactConfig.defaultInstitution").getValue());
    }

    @Test
    public void testPersistTwiceConfiguration() throws Exception {
        Assert.assertEquals( 1, getDaoFactory().getApplicationDao().countAll());

        Application app = new Application("lalaApp", "laladesc");
        app.addProperty(new ApplicationProperty(app, "intactConfig.acPrefix", "LALA"));

        getIntactContext().bindToApplication(app);

        Assert.assertEquals( 1, getDaoFactory().getApplicationDao().countAll());

        intactConfiguration.setDefaultInstitution((Institution) getSpringContext().getBean("institutionIntact"));

        configurationHandler.persistConfiguration();

        Application app2 = new Application("lalaApp", "same");

        getIntactContext().bindToApplication(app2);

        Assert.assertEquals(2, getDaoFactory().getApplicationDao().countAll());

        configurationHandler.persistConfiguration();

        Assert.assertEquals(2, getDaoFactory().getApplicationDao().countAll());

        Application refreshedApp = getDaoFactory().getApplicationDao().getByKey("lalaApp");

        Assert.assertEquals(6, refreshedApp.getProperties().size());
        Assert.assertEquals("same", refreshedApp.getDescription());
        Assert.assertEquals("LALA", refreshedApp.getProperty("intactConfig.acPrefix").getValue());
        Assert.assertEquals("intact", refreshedApp.getProperty("intactConfig.defaultInstitution").getValue());
    }
}
