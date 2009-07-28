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

import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This configuration uses a memory database (H2)
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TemporaryH2DataConfig extends JpaCoreDataConfig {

    public static final String NAME = "uk.ac.ebi.intact.config.TEMPORARY_H2";

    private static final String CONNECTION_PROTOCOL="jdbc:h2:";
    private static String CONNECTION_FILE_DEFAULT = null;

    private EntityManagerFactory entityManagerFactory;

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
    private static final String PERSISTENCE_UNIT_NAME = "intact-core-temp";

    public TemporaryH2DataConfig(IntactSession session) {
        super(session, PERSISTENCE_UNIT_NAME);

        if (session.containsInitParam(IntactEnvironment.TEMP_H2.getFqn())) {
            connectionUrl = "jdbc:h2:"+session.getInitParam(IntactEnvironment.TEMP_H2.getFqn());
        }
    }

    public TemporaryH2DataConfig(IntactSession session, String connectionUrl) {
        super(session, PERSISTENCE_UNIT_NAME);
        this.connectionUrl = connectionUrl;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            Map<String,String> map = new HashMap<String,String>();
            map.put(Environment.URL, connectionUrl);

            entityManagerFactory = buildEntityManagerFactory(map);
        }
        
        return entityManagerFactory;
    }

    @Override
    public String getName() {
        return NAME;
    }
}