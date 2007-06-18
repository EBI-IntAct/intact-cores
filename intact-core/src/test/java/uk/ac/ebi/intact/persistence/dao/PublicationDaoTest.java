/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.unitdataset.LegacyPsiTestDatasetProvider;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
@IntactUnitDataset(dataset = LegacyPsiTestDatasetProvider.INTACT_CORE, provider = LegacyPsiTestDatasetProvider.class)
public class PublicationDaoTest extends IntactAbstractTestCase
{

    private static final Log log = LogFactory.getLog(PublicationDaoTest.class);

    private PublicationDao publicationDao;

    @Before
    public void prepare() throws Exception
    {
        publicationDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getPublicationDao();

    }

    @After
    public void after() throws Exception
    {
        publicationDao = null;
    }

    @Test
    public void getByShortLabel()
    {
        final String label = "10029528";
        Publication pub = publicationDao.getByShortLabel(label);
        assertNotNull(pub);
        assertEquals(label, pub.getShortLabel());
    }

}
