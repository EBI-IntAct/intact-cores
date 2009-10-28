package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Protein;

import java.util.Date;
import java.util.List;

/**
 * HibernateBaseDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.7.2
 */
public class HibernateBaseDaoImplTest extends IntactBasicTestCase {

    @Autowired
    private PersisterHelper persisterHelper;

    @Test
    public void getDbName() throws Exception {
        final String name = getDaoFactory().getBaseDao().getDbName();
        Assert.assertNotNull( name );
    }

    @Test
    public void getDbUserName() throws Exception {
        final String name = getDaoFactory().getBaseDao().getDbUserName();
        Assert.assertNotNull( name );
    }

    @Test
    public void getAllSorted_creationDate() throws Exception {
        Protein protOldest = getMockBuilder().createProtein("Q00001", "oldest");
        Protein protMiddle = getMockBuilder().createProtein("Q00002", "middle");
        Protein protNewest = getMockBuilder().createProtein("Q00003", "newest");

        protOldest.setCreated(new Date(1));
        protMiddle.setCreated(new Date());
        protNewest.setCreated(new Date(System.currentTimeMillis()*2));

        getPersisterHelper().save(protMiddle, protNewest, protOldest);

        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());

        List<? extends Protein> protList = getDaoFactory().getProteinDao().getAllSorted(0, 10, "created", true);

        Assert.assertFalse(protList.isEmpty());
        Assert.assertEquals("oldest", protList.get(0).getShortLabel());
        Assert.assertEquals("middle", protList.get(1).getShortLabel());
        Assert.assertEquals("newest", protList.get(2).getShortLabel());

        List<? extends Protein> protListDesc = getDaoFactory().getProteinDao().getAllSorted(0, 10, "created", false);

        Assert.assertFalse(protListDesc.isEmpty());
        Assert.assertEquals("newest", protListDesc.get(0).getShortLabel());
        Assert.assertEquals("middle", protListDesc.get(1).getShortLabel());
        Assert.assertEquals("oldest", protListDesc.get(2).getShortLabel());
    }
    

}