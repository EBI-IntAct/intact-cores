package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.OrganismFetcher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.FullOrganismUpdater;
import psidev.psi.mi.jami.enricher.listener.OrganismEnricherListener;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.IntactOrganismSynchronizer;

import java.util.Collection;

/**
 * Organism merger based on the jami organism enricher.
 * It will override all the properties of the organism loaded from the database with the properties of the source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactOrganismMergerOverride extends IntactDbMergerOverride<IntactOrganism> implements OrganismEnricher{

    private OrganismEnricher organismEnricher;

    public IntactOrganismMergerOverride(IntactOrganismSynchronizer intactSynchronizer){
        this.organismEnricher = new FullOrganismUpdater(intactSynchronizer);
    }

    public OrganismFetcher getOrganismFetcher() {
        return this.organismEnricher.getOrganismFetcher();
    }

    public void setOrganismEnricherListener(OrganismEnricherListener listener) {
        this.organismEnricher.setOrganismEnricherListener(listener);
    }

    public OrganismEnricherListener getOrganismEnricherListener() {
        return this.organismEnricher.getOrganismEnricherListener();
    }

    public void enrich(Organism object) throws EnricherException {
        this.organismEnricher.enrich(object);
    }

    public void enrich(Collection<Organism> objects) throws EnricherException {
        this.organismEnricher.enrich(objects);
    }

    public void enrich(Organism objectToEnrich, Organism objectSource) throws EnricherException {
        if (objectToEnrich instanceof IntactCvTerm && objectSource instanceof IntactCvTerm){
            merge((IntactOrganism)objectToEnrich, (IntactOrganism)objectSource);
        }
        else{
            this.organismEnricher.enrich(objectToEnrich, objectSource);
        }
    }

    @Override
    protected void mergeOtherProperties(IntactOrganism obj1, IntactOrganism obj2) {
        // first merge audit properties
        super.mergeOtherProperties(obj1, obj2);
        // then merge general properties
        try {
            // WARNING: we enrich the object loaded from the database, not the object source!
            this.organismEnricher.enrich(obj2, obj2);
        } catch (EnricherException e) {
            throw new IllegalStateException("Cannot merge "+obj1 + " with "+obj2, e);
        }
    }
}
