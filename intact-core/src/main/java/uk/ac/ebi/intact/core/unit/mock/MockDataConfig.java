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
package uk.ac.ebi.intact.core.unit.mock;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.IntactSession;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MockDataConfig extends AbstractHibernateDataConfig {

    private IntactSession intactSession;

    public MockDataConfig(IntactSession intactSession) {
        super(intactSession);
        this.intactSession = intactSession;
    }

    public void closeSessionFactory() {
    }

    public void flushSession() {
    }

    public Configuration getConfiguration() {
        return null;
    }

    public String getName() {
        return "MOCK-DATA-CONFIG";
    }

    public SessionFactory getSessionFactory() {
        throw new UnsupportedOperationException();
    }

    public void initialize() {
    }

    public boolean isConfigurable() {
        throw new UnsupportedOperationException();
    }

    protected File getConfigFile() {
        throw new UnsupportedOperationException();
    }

    protected List<String> getPackagesWithEntities() {
        return Collections.EMPTY_LIST;
    }

}