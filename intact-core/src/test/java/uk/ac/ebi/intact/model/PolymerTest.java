package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-May-2010</pre>
 */

public class PolymerTest extends IntactBasicTestCase {

    @Test
    public void test_sequence_Polymer() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );
        Polymer polymer = mockBuilder.createPeptideRandom();

        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII\n" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV\n" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ\n" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM\n" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE\n" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASTQDTGKLPVKWTAPEALREK\n" +
                "KFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMKN\n" +
                "CWHLDAAMRPSFLQLREQLEHIKTHELHL";

        List<SequenceChunk> sequenceChunks = polymer.setSequence(sequence);

        Assert.assertTrue( polymer.getSequence().equals(sequence));
        Assert.assertTrue( sequenceChunks.isEmpty());
        Assert.assertEquals( 1, polymer.getSequenceChunks().size());
        Assert.assertEquals( sequence, polymer.getSequenceChunks().get(0).getSequenceChunk());
        Assert.assertEquals(polymer, polymer.getSequenceChunks().get(0).getParent());
    }
}
