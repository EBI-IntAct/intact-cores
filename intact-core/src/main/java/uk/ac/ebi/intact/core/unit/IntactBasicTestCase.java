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
package uk.ac.ebi.intact.core.unit;

import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * Base for all intact-tests.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactBasicTestCase
{

    protected void beginTransaction() {
        getDataContext().beginTransaction();
    }

    protected void commitTransaction() throws IntactTestException {
        if (getDataContext().isTransactionActive()) {
            try {
                getDataContext().commitTransaction();
            } catch (IntactTransactionException e) {
                throw new IntactTestException(e);
            }
        }
    }

    protected IntactContext getIntactContext() {
        return IntactContext.getCurrentInstance();
    }

    protected DataContext getDataContext() {
        return getIntactContext().getDataContext();
    }

    protected DaoFactory getDaoFactory() {
        return getDataContext().getDaoFactory();
    }

}