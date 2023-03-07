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
package uk.ac.ebi.intact.core.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:/META-INF/intact.spring.xml",
        "classpath*:/META-INF/standalone/*-standalone.spring.xml"
})
public class SchemaUtilsTest {

    @Autowired
    DataSource dataSource;

    @Test
    public void testGenerateCreateSchemaDDLForOracle() {
        String[] strings = SchemaUtils.generateCreateSchemaDDLForOracle(dataSource);

        Assert.assertEquals(189, strings.length);
        Assert.assertEquals(189, SchemaUtils.generateCreateSchemaDDLForPostgreSQL(dataSource).length);
        Assert.assertEquals(189, SchemaUtils.generateCreateSchemaDDLForHSQL(dataSource).length);
        Assert.assertEquals(189, SchemaUtils.generateCreateSchemaDDLForH2(dataSource).length);

        Assert.assertEquals(53, SchemaUtils.getTableNames(dataSource).length);
    }
}
