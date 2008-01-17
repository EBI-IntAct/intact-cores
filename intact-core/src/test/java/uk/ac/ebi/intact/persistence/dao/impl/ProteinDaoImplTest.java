package uk.ac.ebi.intact.persistence.dao.impl;

import static org.junit.Assert.*;
import org.junit.*;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import java.util.List;

/**
 * ProteinDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since TODO artifact version
 * @version $Id$
 */
public class ProteinDaoImplTest extends IntactBasicTestCase {

    @Test
    public void getUniprotProteinsInvolvedInInteractions() throws Exception {
        PersisterHelper.saveOrUpdate( getMockBuilder().createInteractionRandomBinary() );
        PersisterHelper.saveOrUpdate( getMockBuilder().createInteractionRandomBinary() );
        PersisterHelper.saveOrUpdate( getMockBuilder().createInteractionRandomBinary() );
        PersisterHelper.saveOrUpdate( getMockBuilder().createInteractionRandomBinary() );
        PersisterHelper.saveOrUpdate( getMockBuilder().createInteractionRandomBinary() );

        // now build an interaction that involve 2 non uniprot protein
        Protein p1 = getMockBuilder().createProtein( "Q98876", "Q98876" );

        Protein p2 = getMockBuilder().createProtein( "foo", "label2" );
        p2.getXrefs().clear();
        
        Protein p3 = getMockBuilder().createProtein( "bar", "label3" );
        p3.getXrefs().clear();
        Interaction interaction = getMockBuilder().createInteraction( p1, p2, p3 );
        PersisterHelper.saveOrUpdate( interaction );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final List<ProteinImpl> proteins =
                daoFactory.getProteinDao().getUniprotProteinsInvolvedInInteractions( 0, 10000 );
        Assert.assertNotNull( proteins );
        Assert.assertEquals(11, proteins.size());
    }
}