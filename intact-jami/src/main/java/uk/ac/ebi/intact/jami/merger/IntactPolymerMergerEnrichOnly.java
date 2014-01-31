package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.PolymerEnricher;
import psidev.psi.mi.jami.enricher.impl.FullPolymerEnricher;
import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.synchronizer.IntactPolymerSynchronizer;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactPolymerMergerEnrichOnly<P extends Polymer, T extends IntactPolymer> extends IntactInteractorBaseMergerEnrichOnly<P, T>
implements PolymerEnricher<P>{

    public IntactPolymerMergerEnrichOnly(IntactPolymerSynchronizer<P,T> intactSynchronizer){
        super(new FullPolymerEnricher<P>(intactSynchronizer));
    }
}

