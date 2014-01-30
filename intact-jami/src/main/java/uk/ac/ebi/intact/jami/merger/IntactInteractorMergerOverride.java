package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.InteractorEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.FullInteractorUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractorSynchronizer;

import java.util.Collection;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will override all the properties of the cv loaded from the database with the properties of the source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactInteractorMergerOverride extends IntactDbMergerOverride<IntactInteractor> implements InteractorEnricher<Interactor>{
    private InteractorEnricher<Interactor> interactorEnricher;

    public IntactInteractorMergerOverride(IntactInteractorSynchronizer intactSynchronizer){
        this.interactorEnricher = new FullInteractorUpdater(intactSynchronizer);
    }

    protected IntactInteractorMergerOverride(InteractorEnricher<Interactor> interactorEnricher){
        if (interactorEnricher == null){
            throw new IllegalArgumentException("The interactor enricher is required.");
        }
        this.interactorEnricher = interactorEnricher;
    }

    public InteractorFetcher<Interactor> getInteractorFetcher() {
        return this.interactorEnricher.getInteractorFetcher();
    }

    public void setListener(InteractorEnricherListener<Interactor> listener) {
        this.interactorEnricher.setListener(listener);
    }

    public InteractorEnricherListener<Interactor> getListener() {
        return this.interactorEnricher.getListener();
    }

    public void setCvTermEnricher(CvTermEnricher cvTermEnricher) {
        this.interactorEnricher.setCvTermEnricher(cvTermEnricher);
    }

    public CvTermEnricher getCvTermEnricher() {
        return this.interactorEnricher.getCvTermEnricher();
    }

    public void setOrganismEnricher(OrganismEnricher organismEnricher) {
        this.interactorEnricher.setOrganismEnricher(organismEnricher);
    }

    public OrganismEnricher getOrganismEnricher() {
        return this.interactorEnricher.getOrganismEnricher();
    }

    public void enrich(Interactor object) throws EnricherException {
        this.interactorEnricher.enrich(object);
    }

    public void enrich(Collection<Interactor> objects) throws EnricherException {
        this.interactorEnricher.enrich(objects);
    }

    public void enrich(Interactor objectToEnrich, Interactor objectSource) throws EnricherException {
        if (objectToEnrich instanceof IntactInteractor && objectSource instanceof IntactInteractor){
            merge((IntactInteractor)objectToEnrich, (IntactInteractor)objectSource);
        }
        else{
            this.interactorEnricher.enrich(objectToEnrich, objectSource);
        }
    }

    @Override
    protected void mergeOtherProperties(IntactInteractor obj1, IntactInteractor obj2) {
        // first merge audit properties
        super.mergeOtherProperties(obj1, obj2);
        // then merge general properties
        try {
            // WARNING: we enrich the object loaded from the database, not the object source!
            this.interactorEnricher.enrich(obj2, obj2);
        } catch (EnricherException e) {
            throw new IllegalStateException("Cannot merge "+obj1 + " with "+obj2, e);
        }
    }
}
