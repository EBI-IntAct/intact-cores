package uk.ac.ebi.intact.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * HibernateBaseDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.7.2
 * @version $Id$
 */
public class HibernateBaseDaoImplTest extends IntactBasicTestCase {

    @Test
    public void getDbName() throws Exception {
        commitTransaction();
        Assert.assertFalse( getDataContext().isTransactionActive() );
        final String name = getDaoFactory().getBaseDao().getDbName();
        Assert.assertNotNull( name );
    }    

    @Test
    public void getDbUserName() throws Exception {
        commitTransaction();
        Assert.assertFalse( getDataContext().isTransactionActive() );
        final String name = getDaoFactory().getBaseDao().getDbUserName();
        Assert.assertNotNull( name );
    }    
}