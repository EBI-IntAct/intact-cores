package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.InteractorEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.FullInteractorEnricher;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractorBaseSynchronizer;

import java.util.Collection;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactInteractorBaseMergerEnrichOnly<I extends Interactor, T extends IntactInteractor> extends IntactDbMergerEnrichOnly<T> implements InteractorEnricher<I> {

    private InteractorEnricher<I> interactorEnricher;

    public IntactInteractorBaseMergerEnrichOnly(IntactInteractorBaseSynchronizer<I,T> intactSynchronizer){
        this.interactorEnricher = new FullInteractorEnricher<I>(intactSynchronizer);
    }

    protected IntactInteractorBaseMergerEnrichOnly(InteractorEnricher<I> interactorEnricher){
        if (interactorEnricher == null){
            throw new IllegalArgumentException("The interactor enricher is required.");
        }
        this.interactorEnricher = interactorEnricher;
    }

    public InteractorFetcher<I> getInteractorFetcher() {
        return this.interactorEnricher.getInteractorFetcher();
    }

    public void setListener(InteractorEnricherListener<I> listener) {
        this.interactorEnricher.setListener(listener);
    }

    public InteractorEnricherListener<I> getListener() {
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

    public void enrich(I object) throws EnricherException {
        this.interactorEnricher.enrich(object);
    }

    public void enrich(Collection<I> objects) throws EnricherException {
        this.interactorEnricher.enrich(objects);
    }

    public void enrich(I objectToEnrich, I objectSource) throws EnricherException {
        if (objectToEnrich instanceof IntactInteractor && objectSource instanceof IntactInteractor){
            merge((T)objectToEnrich, (T)objectSource);
        }
        else{
            this.interactorEnricher.enrich(objectToEnrich, objectSource);
        }
    }

    @Override
    protected void mergeOtherProperties(T obj1, T obj2) {
        // first merge audit properties
        super.mergeOtherProperties(obj1, obj2);
        // then merge general properties
        try {
            // WARNING: we enrich the object loaded from the database, not the object source!
            this.interactorEnricher.enrich((I)obj2, (I)obj2);
        } catch (EnricherException e) {
            throw new IllegalStateException("Cannot merge "+obj1 + " with "+obj2, e);
        }
    }
}

