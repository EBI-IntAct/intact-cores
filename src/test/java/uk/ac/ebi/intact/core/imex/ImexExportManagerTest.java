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
package uk.ac.ebi.intact.core.imex;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Interaction;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexExportManagerTest extends IntactBasicTestCase {

    @Autowired
    private ImexExportManager imexExportManager;

    @Test
    @Ignore
    public void testPrepareRelease() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary("IM-1-1");
        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary("IM-1-2");
        Interaction interaction3 = getMockBuilder().createInteractionRandomBinary("IM-2-1");
        Interaction interaction4 = getMockBuilder().createInteractionRandomBinary();

        ImexReleaseTagger imexReleaseTagger = new ImexReleaseTagger(getIntactContext());
        imexReleaseTagger.tag(interaction1);
        imexReleaseTagger.tag(interaction2);

        getCorePersister().saveOrUpdate(interaction1, interaction2, interaction3, interaction4);

        imexExportManager.prepareRelease();

        assertImexExportInteractionCount(2);
        assertImexExportInteractionExists("IM-1-1");
        assertImexExportInteractionExists("IM-1-2");
        assertImexExportInteractionNotExists("IM-2-1");
        assertImexExportReleaseCount(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrepareRelease_toBeforeFromException() throws Exception {
        imexExportManager.prepareRelease(new DateTime("2009-01-01"), new DateTime("2007-01-01"));
    }

    private void assertImexExportInteractionExists(String imexId) {
        Assert.assertFalse(getDaoFactory().getImexExportInteractionDao().getByImexId(imexId).isEmpty());
    }

    private void assertImexExportInteractionNotExists(String imexId) {
        Assert.assertTrue(getDaoFactory().getImexExportInteractionDao().getByImexId(imexId).isEmpty());
    }

    private void assertImexExportReleaseCount(int exportReleaseCount) {
        Assert.assertEquals(exportReleaseCount, getDaoFactory().getImexExportReleaseDao().countAll());
    }

    private void assertImexExportInteractionCount(int exportInteractionCount) {
        Assert.assertEquals(exportInteractionCount, getDaoFactory().getImexExportInteractionDao().countAll());
    }
}
