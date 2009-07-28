/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Publication;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class PublicationDaoTest extends IntactBasicTestCase {
    private PublicationDao publicationDao;

    @Before
    public void prepare() throws Exception {
        publicationDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getPublicationDao();
    }

    @After
    public void end() throws Exception {
        publicationDao = null;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        Publication pub1 = mockBuilder.createPublication( "10029528" );
        PersisterHelper.saveOrUpdate( pub1 );
    }

    @AfterClass
    public static void afterClass() throws Exception {
        IntactContext.closeCurrentInstance();
    }

    @Test
    public void getByShortLabel() {
        final String label = "10029528";
        Publication pub = publicationDao.getByShortLabel( label );
        assertNotNull( pub );
        assertEquals( label, pub.getShortLabel() );
    }

    @Test
    public void getByPubmedId() {
        final String label = "10029528";
        Publication pub = publicationDao.getByPubmedId( label );
        assertNotNull( pub );
        assertEquals( label, pub.getShortLabel() );
    }

}
