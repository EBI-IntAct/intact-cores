package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.InteractorEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullInteractorBaseUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.impl.InteractorSynchronizerTemplate;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will override all the properties of the interactor loaded from the database with the properties of the source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class InteractorBaseMergerOverride<I extends Interactor, T extends IntactInteractor> extends IntactDbMergerOverride<I,T> implements InteractorEnricher<I>{

    public InteractorBaseMergerOverride(InteractorSynchronizerTemplate<I, T> intactSynchronizer){
        super(new FullInteractorBaseUpdater<I>(intactSynchronizer));
    }

    protected InteractorBaseMergerOverride(InteractorEnricher<I> interactorEnricher){
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
