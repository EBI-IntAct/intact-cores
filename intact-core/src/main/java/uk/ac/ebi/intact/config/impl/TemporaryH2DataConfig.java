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
package uk.ac.ebi.intact.config.impl;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.config.ConfigurationException;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;

import java.io.*;

/**
 * This configuration uses a memory database (H2)
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TemporaryH2DataConfig extends StandardCoreDataConfig {

    public static final String NAME = "uk.ac.ebi.intact.config.TEMPORARY_H2";

    private static final String CONNECTION_PROTOCOL="jdbc:h2:";
    private static String CONNECTION_FILE_DEFAULT = null;

    static {
        try
        {
            CONNECTION_FILE_DEFAULT = File.createTempFile("intact-", "-h2").toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String connectionUrl = CONNECTION_PROTOCOL+CONNECTION_FILE_DEFAULT;

    private File configurationFile;

    public TemporaryH2DataConfig(IntactSession session) {
        super(session);
    }

    @Override
    public Configuration getConfiguration()
    {
        connectionUrl = CONNECTION_PROTOCOL+CONNECTION_FILE_DEFAULT;

        if (getSession().containsInitParam(IntactEnvironment.TEMP_H2.getFqn())) {
            connectionUrl = CONNECTION_PROTOCOL + getSession().getInitParam(IntactEnvironment.TEMP_H2.getFqn());
        }
        
        Configuration configuration = super.getConfiguration();
        configuration.setProperty(Environment.URL, connectionUrl);

        return configuration;
    }

    @Override
    protected File getConfigFile() {
        if (configurationFile != null) {
            return configurationFile;
        }

        try {
            configurationFile = File.createTempFile("temporary-hibernate-", ".cfg.xml");
            configurationFile.deleteOnExit();

            InputStream is = InMemoryDataConfig.class.getResourceAsStream("/META-INF/temporary-hibernate.cfg.xml");

            FileWriter writer = new FileWriter(configurationFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line+System.getProperty("line.separator"));
            }

            writer.close();
        } catch (IOException e) {
            throw new ConfigurationException("Exception getting configuration file", e);
        }

        return configurationFile;
    }

    @Override
    public String getName() {
        return NAME;
    }
}