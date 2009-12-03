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
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persistence.dao.ImexExportReleaseDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.meta.ImexExportRelease;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexExportReleaseDaoImplTest extends IntactBasicTestCase {

    @Autowired
    private ImexExportReleaseDao imexExportReleaseDao;

    @Test
    public void testGetLastRelease() throws Exception {
        Assert.assertNull(imexExportReleaseDao.getLastRelease());
    }
    
    @Test
    public void testGetLastRelease1() throws Exception {
        ImexExportRelease release1 = new ImexExportRelease();
        imexExportReleaseDao.persist(release1);

        Assert.assertNotNull(imexExportReleaseDao.getLastRelease());
    }

    @Test
    public void testGetLastRelease2() throws Exception {
        ImexExportRelease release1 = new ImexExportRelease();
        release1.setCreated(new DateTime("1980-01-01").toDate());
        release1.setCreatedCount(5);
        ImexExportRelease release2 = new ImexExportRelease();
        release2.setCreatedCount(10);

        imexExportReleaseDao.persist(release1);
        imexExportReleaseDao.persist(release2);

        final ImexExportRelease lastRelease = imexExportReleaseDao.getLastRelease();
        Assert.assertNotNull(lastRelease);
        Assert.assertEquals(10, lastRelease.getCreatedCount());
    }
}
