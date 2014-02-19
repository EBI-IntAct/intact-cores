package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.InteractorEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullInteractorBaseEnricher;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.impl.InteractorSynchronizerTemplate;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class InteractorBaseMergerEnrichOnly<I extends Interactor, T extends IntactInteractor> extends IntactDbMergerEnrichOnly<I,T> implements InteractorEnricher<I> {

    public InteractorBaseMergerEnrichOnly(InteractorSynchronizerTemplate<I, T> intactSynchronizer){
        super(new FullInteractorBaseEnricher<I>(intactSynchronizer));
    }

    protected InteractorBaseMergerEnrichOnly(InteractorEnricher<I> interactorEnricher){
        super(interactorEnricher);
    }

    @Override
    protected InteractorEnricher<I> getBasicEnricher() {
        return (InteractorEnricher<I>)super.getBasicEnricher();
    }

    public InteractorFetcher<I> getInteractorFetcher() {
        return getBasicEnricher().getInteractorFetcher();
    }

    public InteractorEnricherListener<I> getListener() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public OrganismEnricher getOrganismEnricher() {
        return null;
    }
}

