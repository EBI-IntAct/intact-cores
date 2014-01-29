package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.OntologyTermFetcher;
import psidev.psi.mi.jami.enricher.OntologyTermEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.FullOntologyTermEnricher;
import psidev.psi.mi.jami.enricher.listener.OntologyTermEnricherListener;
import psidev.psi.mi.jami.model.OntologyTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.IntactCvTermSynchronizer;

import java.util.Collection;

/**
 * Cv term merger based on the jami cv term enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactCvTermMergerEnrichOnly extends IntactDbMergerEnrichOnly<IntactCvTerm> implements OntologyTermEnricher{

    private OntologyTermEnricher cvEnricher;

    public IntactCvTermMergerEnrichOnly(IntactCvTermSynchronizer intactSynchronizer){
        this.cvEnricher = new FullOntologyTermEnricher(intactSynchronizer);
    }

    public OntologyTermFetcher getOntologyTermFetcher() {
        return this.cvEnricher.getOntologyTermFetcher();
    }

    public void setOntologyTermEnricherListener(OntologyTermEnricherListener listener) {
        this.cvEnricher.setOntologyTermEnricherListener(listener);
    }

    public OntologyTermEnricherListener getOntologyTermEnricherListener() {
        return this.cvEnricher.getOntologyTermEnricherListener();
    }

    public void enrich(OntologyTerm object) throws EnricherException {
        this.cvEnricher.enrich(object);
    }

    public void enrich(Collection<OntologyTerm> objects) throws EnricherException {
        this.cvEnricher.enrich(objects);
    }

    public void enrich(OntologyTerm objectToEnrich, OntologyTerm objectSource) throws EnricherException {
        if (objectToEnrich instanceof IntactCvTerm && objectSource instanceof IntactCvTerm){
            merge((IntactCvTerm)objectToEnrich, (IntactCvTerm)objectSource);
        }
        else{
            this.cvEnricher.enrich(objectToEnrich, objectSource);
        }
    }

    @Override
    protected void mergeOtherProperties(IntactCvTerm obj1, IntactCvTerm obj2) {
        // first merge audit properties
        super.mergeOtherProperties(obj1, obj2);
        // then merge general properties
        try {
            // WARNING: we enrich the object loaded from the database, not the object source!
            this.cvEnricher.enrich(obj2, obj2);
        } catch (EnricherException e) {
            throw new IllegalStateException("Cannot merge "+obj1 + " with "+obj2, e);
        }
    }
}
