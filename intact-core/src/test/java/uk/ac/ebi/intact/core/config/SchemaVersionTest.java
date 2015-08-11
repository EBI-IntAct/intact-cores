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
package uk.ac.ebi.intact.core.config;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SchemaVersionTest {
    @Test
    public void testParse() {
        SchemaVersion v190 = SchemaVersion.parse("1.9.0");
        Assert.assertEquals(1, v190.getMajor().intValue());
        Assert.assertEquals(9, v190.getMinor().intValue());
        Assert.assertEquals(0, v190.getBuild().intValue());
    }

    @Test
    public void testIsCompatibleWith() {
        Assert.assertTrue(new SchemaVersion(1,9,0).isCompatibleWith(new SchemaVersion(1,9,0)));
        Assert.assertTrue(new SchemaVersion(1,9,3).isCompatibleWith(new SchemaVersion(1,9,0)));
        Assert.assertFalse(new SchemaVersion(2,0,0).isCompatibleWith(new SchemaVersion(1,9,0)));
        Assert.assertFalse(new SchemaVersion(1,9,0).isCompatibleWith(new SchemaVersion(2,0,0)));
    }
}
