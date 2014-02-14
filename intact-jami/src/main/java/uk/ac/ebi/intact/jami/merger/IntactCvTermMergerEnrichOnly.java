package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.CvTermFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullCvTermEnricher;
import psidev.psi.mi.jami.enricher.listener.CvTermEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.IntactCvTermSynchronizer;

/**
 * Cv term merger based on the jami cv term enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactCvTermMergerEnrichOnly extends IntactDbMergerEnrichOnly<CvTerm, IntactCvTerm> implements CvTermEnricher<CvTerm>{

    public IntactCvTermMergerEnrichOnly(IntactCvTermSynchronizer intactSynchronizer){
        super(IntactCvTerm.class, new FullCvTermEnricher<CvTerm>(intactSynchronizer));
    }

    @Override
    protected CvTermEnricher getBasicEnricher() {
        return (CvTermEnricher) super.getBasicEnricher();
    }

    public CvTermFetcher<CvTerm> getCvTermFetcher() {
        return getBasicEnricher().getCvTermFetcher();
    }

    public CvTermEnricherListener<CvTerm> getCvTermEnricherListener() {
        return null;
    }
}
