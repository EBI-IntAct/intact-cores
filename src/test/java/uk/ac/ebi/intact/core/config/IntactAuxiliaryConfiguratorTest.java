/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactAuxiliaryConfiguratorTest {

    @Test
    public void configure() throws Exception {
        Properties props = new Properties();
        props.put(Environment.DIALECT, Oracle9iDialect.class.getName());

        Ejb3Configuration ejbConfig = new Ejb3Configuration();
        ejbConfig.configure("intact-core-default", props);
        IntactAuxiliaryConfigurator.configure(ejbConfig);

        String[] sqls = ejbConfig.getHibernateConfiguration()
                .generateSchemaCreationScript(Dialect.getDialect(props));

        boolean containsCvLocalSeq = false;

        for (String sql : sqls) {
            if (sql.contains("cv_local_seq")) containsCvLocalSeq = true;
        }
        
        Assert.assertTrue("Sequence cv_local_seq should be included in the create DDL",
                          containsCvLocalSeq);
    }
}
