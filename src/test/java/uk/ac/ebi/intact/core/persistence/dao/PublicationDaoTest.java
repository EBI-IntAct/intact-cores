/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Publication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class PublicationDaoTest extends IntactBasicTestCase {

    @Autowired
    private PersisterHelper persisterHelper;

    @Autowired
    private PublicationDao publicationDao;

    @Before
    public void beforeClass() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        Publication pub1 = mockBuilder.createPublication( "10029528" );
        getPersisterHelper().save( pub1 );
    }

    @Test
    public void getByShortLabel() {
        Assert.assertEquals(1, publicationDao.countAll());
        Assert.assertEquals(0, IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().countAll());

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
