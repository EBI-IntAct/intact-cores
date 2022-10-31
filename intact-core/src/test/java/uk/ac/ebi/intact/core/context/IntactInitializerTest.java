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
package uk.ac.ebi.intact.core.context;

import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.InstitutionDao;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvPublicationStatus;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.EntityManagerFactory;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactInitializerTest extends IntactBasicTestCase {

    @Autowired
    private InstitutionDao institutionDao;

    @Autowired
    private CvObjectDao cvObjectDao;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void idtestInit() {
        Assert.assertEquals(4, institutionDao.countAll());
        Assert.assertEquals(39, cvObjectDao.countAll());

        CvTopic correctionComment = getDaoFactory().getCvObjectDao(CvTopic.class).getByShortLabel(CvTopic.CORRECTION_COMMENT);
        Assert.assertFalse(CvObjectUtils.isHidden(correctionComment));
        CvPublicationStatus released = getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByShortLabel("released");
        Assert.assertEquals(1, released.getXrefs().size());
        Assert.assertEquals(CvDatabase.INTACT, released.getXrefs().iterator().next().getCvDatabase().getShortLabel());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sessionLeakTest() {
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) entityManagerFactory;
        Statistics statistics = hemf.getSessionFactory().getStatistics();

        Assert.assertEquals(statistics.getSessionCloseCount(), statistics.getSessionOpenCount());
    }
}
