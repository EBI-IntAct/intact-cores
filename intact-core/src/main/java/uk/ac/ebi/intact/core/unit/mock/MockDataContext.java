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

import org.hibernate.Session;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MockDataContext extends DataContext {

    private MockDaoFactory daoFactory;
    private MockIntactTransaction intactTransaction;

    public MockDataContext(IntactSession session, MockDaoFactory daoFactory, MockIntactTransaction intactTransaction) {
        super(session);
        this.daoFactory = daoFactory;

        this.intactTransaction = intactTransaction;
    }

    @Override
    public void beginTransaction() {
        daoFactory.beginTransaction();
    }

    @Override
    public void beginTransaction(String dataConfigName) {
        daoFactory.beginTransaction();
    }

    @Override
    public void beginTransactionManualFlush() {
    }

    @Override
    public void commitAllActiveTransactions() throws IntactTransactionException {
    }

    @Override
    public void commitTransaction() throws IntactTransactionException {
    }

    @Override
    public void commitTransaction(String dataConfigName) throws IntactTransactionException {
    }

    @Override
    public void flushSession() {
    }

    @Override
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Override
    public DaoFactory getDaoFactory(String dataConfigName) {
        return daoFactory;
    }

    @Override
    public Session getSession() {
        return super.getSession();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isTransactionActive() {
        return !intactTransaction.wasCommitted();
    }

    @Override
    public boolean isTransactionActive(String dataConfigName) {
        return !intactTransaction.wasCommitted();
    }

    public void setMockDaoFactory(MockDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public void setMockIntactTransaction(MockIntactTransaction intactTransaction) {
        this.intactTransaction = intactTransaction;
    }
}