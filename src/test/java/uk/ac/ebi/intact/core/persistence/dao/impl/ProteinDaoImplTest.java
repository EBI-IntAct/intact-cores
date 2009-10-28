package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;

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
        getPersisterHelper().save( getMockBuilder().createInteractionRandomBinary() );
        getPersisterHelper().save( getMockBuilder().createInteractionRandomBinary() );
        getPersisterHelper().save( getMockBuilder().createInteractionRandomBinary() );
        getPersisterHelper().save( getMockBuilder().createInteractionRandomBinary() );
        getPersisterHelper().save( getMockBuilder().createInteractionRandomBinary() );

        // now build an interaction that involve 2 non uniprot protein
        Protein p1 = getMockBuilder().createProtein( "Q98876", "Q98876" );

        Protein p2 = getMockBuilder().createProtein( "foo", "label2" );
        p2.getXrefs().clear();
        
        Protein p3 = getMockBuilder().createProtein( "bar", "label3" );
        p3.getXrefs().clear();
        Interaction interaction = getMockBuilder().createInteraction( p1, p2, p3 );
        getPersisterHelper().save( interaction );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final List<ProteinImpl> proteins =
                daoFactory.getProteinDao().getUniprotProteinsInvolvedInInteractions( 0, 10000 );
        Assert.assertNotNull( proteins );
        Assert.assertEquals(11, proteins.size());
    }

    @Test

    public void getAllUniprotAcs() throws Exception {
        Protein nonInteractingProt = getMockBuilder().createProtein("Q00000", "non");

        Protein protA = getMockBuilder().createProtein("P00111", "protA");
        Protein protB = getMockBuilder().createProtein("P00112", "protB");
        Interaction interaction = getMockBuilder().createInteraction(protA, protB);

        getPersisterHelper().save(nonInteractingProt, interaction);

        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());

        List<String> uniprotAcs = getDaoFactory().getProteinDao().getAllUniprotAcs();
        Assert.assertEquals(2, uniprotAcs.size());
    }

    @Test

    public void getSpliceVariants() throws Exception {
        Protein masterProt1 = getMockBuilder().createProtein("P12345", "master1");

        // master protein needs to be persisted first
        getPersisterHelper().save(masterProt1);
        Assert.assertNotNull(masterProt1.getAc());

        Protein spliceVar11 = getMockBuilder().createProteinSpliceVariant(masterProt1, "P12345-1", "sv11");
        Protein spliceVar12 = getMockBuilder().createProteinSpliceVariant(masterProt1, "P12345-2", "sv12");

        getPersisterHelper().save(spliceVar11, spliceVar12);

        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().getSpliceVariants(masterProt1).size());
    }

    @Test

    public void getSpliceVariantMasterProtein() throws Exception {
        Protein masterProt1 = getMockBuilder().createProtein("P12345", "master1");

        // master protein needs to be persisted first
        getPersisterHelper().save(masterProt1);
        Assert.assertNotNull(masterProt1.getAc());

        Protein spliceVar11 = getMockBuilder().createProteinSpliceVariant(masterProt1, "P12345-1", "sv11");
        Protein spliceVar12 = getMockBuilder().createProteinSpliceVariant(masterProt1, "P12345-2", "sv12");

        getPersisterHelper().save(spliceVar11, spliceVar12);

        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(masterProt1.getAc(), getDaoFactory().getProteinDao().getSpliceVariantMasterProtein(spliceVar11).getAc());
        Assert.assertEquals(masterProt1.getAc(), getDaoFactory().getProteinDao().getSpliceVariantMasterProtein(spliceVar12).getAc());
    }
}