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
package uk.ac.ebi.intact.core.context;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DataContextTest extends IntactBasicTestCase {

    @Autowired
    private DataContext dataContext;

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void testCommitTransaction() throws Exception {

        TransactionStatus transactionStatus = dataContext.beginTransaction();
        Assert.assertTrue(transactionStatus.isNewTransaction());
        Assert.assertFalse(transactionStatus.isCompleted());

        dataContext.commitTransaction(transactionStatus);
        Assert.assertTrue(transactionStatus.isCompleted());
    }
}
