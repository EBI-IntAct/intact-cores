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

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.*;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MockIntactContext extends IntactContext {

    private DataContext dataContext;
    private IntactSession session;
    private RuntimeConfig runtimeConfig;
    private CvContext cvContext;
    private UserContext userContext;

    public MockIntactContext(DataContext dataContext, IntactSession session) {
        super(dataContext, session);
        this.dataContext = dataContext;
        this.session = session;

        setCurrentInstance(this);
    }

    public static void initMockContext() {
        MockIntactSession intactSession = new MockIntactSession();
        MockDataConfig dataConfig = new MockDataConfig(intactSession);
        MockDaoFactory daoFactory = new MockDaoFactory(dataConfig, intactSession);
        MockIntactTransaction intactTransaction = new MockIntactTransaction(intactSession);
        MockDataContext dataContext = new MockDataContext(intactSession, daoFactory, intactTransaction);

        // start the mock context
        new MockIntactContext(dataContext, intactSession);
    }

    @Override
    public RuntimeConfig getConfig() {
        return runtimeConfig;
    }

    @Override
    public CvContext getCvContext() {
        return cvContext;
    }

    @Override
    public DataContext getDataContext() {
        return dataContext;
    }

    @Override
    public Institution getInstitution() throws IntactException {
        return getConfig().getInstitution();
    }

    @Override
    public IntactSession getSession() {
        return session;
    }

    @Override
    public UserContext getUserContext() {
        return userContext;
    }

    public void setCvContext(CvContext cvContext) {
        this.cvContext = cvContext;
    }

    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    public void setInstitution(Institution institution) {
        getConfig().setInstitution(institution);
    }

    public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public void setSession(IntactSession session) {
        this.session = session;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public static MockDaoFactory configureMockDaoFactory() {
        if (!IntactContext.currentInstanceExists()) {
            MockIntactContext.initMockContext();
        }
        return (MockDaoFactory) IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

}