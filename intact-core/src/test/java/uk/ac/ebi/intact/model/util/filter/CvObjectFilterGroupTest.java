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
package uk.ac.ebi.intact.model.util.filter;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectFilterGroupTest {

    @Test
    public void isAccepted_noDefaults() throws Exception {
        CvObjectFilterGroup filterGroup = new CvObjectFilterGroup();

        filterGroup.addIncludedIdentifier("in");
        filterGroup.addExcludedIdentifier("ex");

        Assert.assertTrue(filterGroup.isAccepted("in"));
        Assert.assertFalse(filterGroup.isAccepted("ex"));
        Assert.assertFalse(filterGroup.isAccepted("new"));
        Assert.assertTrue(filterGroup.isAcceptedIdentifier("in"));
        Assert.assertFalse(filterGroup.isAcceptedIdentifier("ex"));
        Assert.assertFalse(filterGroup.isAcceptedLabel("in"));
        Assert.assertFalse(filterGroup.isAcceptedLabel("ex"));
    }

    @Test
    public void isAccepted_includeByDefault() throws Exception {
        CvObjectFilterGroup filterGroup = new CvObjectFilterGroup(true, false);

        filterGroup.addExcludedIdentifier("ex");

        Assert.assertTrue(filterGroup.isAccepted("in"));
        Assert.assertFalse(filterGroup.isAccepted("ex"));
        Assert.assertTrue(filterGroup.isAccepted("new"));
        Assert.assertTrue(filterGroup.isAcceptedIdentifier("in"));
        Assert.assertFalse(filterGroup.isAcceptedIdentifier("ex"));
        Assert.assertTrue(filterGroup.isAcceptedLabel("in"));
        Assert.assertTrue(filterGroup.isAcceptedLabel("ex"));
    }
    
    @Test
    public void isAccepted_excludeByDefault() throws Exception {
        CvObjectFilterGroup filterGroup = new CvObjectFilterGroup(false, true);

        filterGroup.addIncludedIdentifier("in");

        Assert.assertTrue(filterGroup.isAccepted("in"));
        Assert.assertFalse(filterGroup.isAccepted("ex"));
        Assert.assertFalse(filterGroup.isAccepted("new"));
        Assert.assertTrue(filterGroup.isAcceptedIdentifier("in"));
        Assert.assertFalse(filterGroup.isAcceptedIdentifier("ex"));
        Assert.assertFalse(filterGroup.isAcceptedLabel("in"));
        Assert.assertFalse(filterGroup.isAcceptedLabel("ex"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void includedAndExcludedByDefault() throws Exception {
        CvObjectFilterGroup filterGroup = new CvObjectFilterGroup(true, true);
    }
}
