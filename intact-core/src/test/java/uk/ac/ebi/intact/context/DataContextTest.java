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
package uk.ac.ebi.intact.context;

import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DataContextTest extends IntactBasicTestCase {

    @Test
    public void commitTransaction_test() throws Exception {
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) getIntactContext().getConfig().getDefaultDataConfig().getEntityManagerFactory();
        Statistics stats = hemf.getSessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);

        Assert.assertEquals(0, stats.getSessionOpenCount());
        Assert.assertEquals(0, stats.getSessionCloseCount());
        Assert.assertEquals(0, stats.getTransactionCount());

        beginTransaction();
        Assert.assertTrue(getDaoFactory().isTransactionActive());

        Assert.assertEquals(1, stats.getSessionOpenCount());
        Assert.assertEquals(0, stats.getSessionCloseCount());
        Assert.assertEquals(0, stats.getTransactionCount());

        getDataContext().commitTransaction();

        Assert.assertFalse(getDaoFactory().isTransactionActive());

        Assert.assertEquals(1, stats.getSessionOpenCount());
        Assert.assertEquals(1, stats.getSessionCloseCount());
        Assert.assertEquals(1, stats.getTransactionCount());

        stats.clear();
    }
    
    @Test
    public void commitAllActiveTransaction_test() throws Exception {
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) getIntactContext().getConfig().getDefaultDataConfig().getEntityManagerFactory();
        Statistics stats = hemf.getSessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);

        Assert.assertEquals(0, stats.getSessionOpenCount());
        Assert.assertEquals(0, stats.getSessionCloseCount());
        Assert.assertEquals(0, stats.getTransactionCount());

        beginTransaction();
        Assert.assertTrue(getDaoFactory().isTransactionActive());

        Assert.assertEquals(1, stats.getSessionOpenCount());
        Assert.assertEquals(0, stats.getSessionCloseCount());
        Assert.assertEquals(0, stats.getTransactionCount());

        getDataContext().commitAllActiveTransactions();

        Assert.assertFalse(getDaoFactory().isTransactionActive());

        Assert.assertEquals(1, stats.getSessionOpenCount());
        Assert.assertEquals(1, stats.getSessionCloseCount());
        Assert.assertEquals(1, stats.getTransactionCount());

        stats.clear();
    }
}
