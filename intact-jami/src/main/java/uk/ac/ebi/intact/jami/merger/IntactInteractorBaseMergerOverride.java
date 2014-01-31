package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.InteractorEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.impl.FullInteractorUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractorBaseSynchronizer;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will override all the properties of the interactor loaded from the database with the properties of the source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactInteractorBaseMergerOverride<I extends Interactor, T extends IntactInteractor> extends IntactDbMergerOverride<I,T> implements InteractorEnricher<I>{

    public IntactInteractorBaseMergerOverride(IntactInteractorBaseSynchronizer<I,T> intactSynchronizer){
        super(new FullInteractorUpdater<I>(intactSynchronizer));
    }

    protected IntactInteractorBaseMergerOverride(InteractorEnricher<I> interactorEnricher){
        super(interactorEnricher);
    }

    @Override
    protected InteractorEnricher<I> getBasicEnricher() {
        return (InteractorEnricher<I>)super.getBasicEnricher();
    }

    public InteractorFetcher<I> getInteractorFetcher() {
        return getBasicEnricher().getInteractorFetcher();
    }

    public void setListener(InteractorEnricherListener<I> listener) {
        getBasicEnricher().setListener(listener);
    }

    public InteractorEnricherListener<I> getListener() {
        return getBasicEnricher().getListener();
    }

    public void setCvTermEnricher(CvTermEnricher cvTermEnricher) {
        getBasicEnricher().setCvTermEnricher(cvTermEnricher);
    }

    public CvTermEnricher getCvTermEnricher() {
        return getBasicEnricher().getCvTermEnricher();
    }

    public void setOrganismEnricher(OrganismEnricher organismEnricher) {
        getBasicEnricher().setOrganismEnricher(organismEnricher);
    }

    public OrganismEnricher getOrganismEnricher() {
        return getBasicEnricher().getOrganismEnricher();
    }
}
