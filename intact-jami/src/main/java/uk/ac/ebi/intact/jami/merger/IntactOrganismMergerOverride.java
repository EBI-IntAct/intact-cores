package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.OrganismFetcher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.impl.FullOrganismUpdater;
import psidev.psi.mi.jami.enricher.listener.OrganismEnricherListener;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.IntactOrganismSynchronizer;

/**
 * Organism merger based on the jami organism enricher.
 * It will override all the properties of the organism loaded from the database with the properties of the source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactOrganismMergerOverride extends IntactDbMergerOverride<Organism, IntactOrganism> implements OrganismEnricher{

    public IntactOrganismMergerOverride(IntactOrganismSynchronizer intactSynchronizer){
        super(IntactOrganism.class, new FullOrganismUpdater(intactSynchronizer));
    }

    @Override
    protected OrganismEnricher getBasicEnricher() {
        return (OrganismEnricher)super.getBasicEnricher();
    }

    public OrganismFetcher getOrganismFetcher() {
        return getBasicEnricher().getOrganismFetcher();
    }

    public void setOrganismEnricherListener(OrganismEnricherListener listener) {
        getBasicEnricher().setOrganismEnricherListener(listener);
    }

    public OrganismEnricherListener getOrganismEnricherListener() {
        return getBasicEnricher().getOrganismEnricherListener();
    }
}
