package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.BioSource;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_BioSourceTest extends IntactBasicTestCase
{

    @Test
    public void persist_sameBioSource() throws Exception {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs1 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getXrefDao().countAll());

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs2 );

        Assert.assertEquals(1, getDaoFactory().getBioSourceDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getXrefDao().countAll());
    }

    private Component reloadByAc(Component interaction) {
        return getDaoFactory().getComponentDao().getByAc(interaction.getAc());
    }

    private void refresh(Component interaction) {
        getDaoFactory().getComponentDao().refresh(interaction);
    }

}