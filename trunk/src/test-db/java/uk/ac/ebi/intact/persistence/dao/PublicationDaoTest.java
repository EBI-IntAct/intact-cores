/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Publication;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class PublicationDaoTest extends TestCase
{

    private static final Log log = LogFactory.getLog(PublicationDaoTest.class);

    private PublicationDao publicationDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        publicationDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getPublicationDao();

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        publicationDao = null;
    }

    public void testGetByAc()
    {
        Publication pub = publicationDao.getByAc("EBI-972997");
        assertNotNull(pub);
        assertEquals("16469705", pub.getShortLabel());
    }

}
