/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.business.IntactTransactionException;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jul-2006</pre>
 */
public class DaoFactoryTest extends TestCase
{

    private static final Log log = LogFactory.getLog(DaoFactoryTest.class);

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testBeginTransactionWithConnection() throws IntactTransactionException {

        Protein p = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao().getByAc("EBI-493");
        assertNotNull(p);

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

    }
}
