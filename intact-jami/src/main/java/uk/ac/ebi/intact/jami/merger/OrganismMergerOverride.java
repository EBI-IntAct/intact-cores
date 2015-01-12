package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.OrganismFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullOrganismUpdater;
import psidev.psi.mi.jami.enricher.listener.OrganismEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.impl.OrganismSynchronizer;

/**
 * Organism merger based on the jami organism enricher.
 * It will override all the properties of the organism loaded from the database with the properties of the source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class OrganismMergerOverride extends IntactDbMergerOverride<Organism, IntactOrganism> implements OrganismEnricher{

    public OrganismMergerOverride(OrganismSynchronizer intactSynchronizer){
        super(IntactOrganism.class, new FullOrganismUpdater(intactSynchronizer));
    }

    @Override
    protected OrganismEnricher getBasicEnricher() {
        return (OrganismEnricher)super.getBasicEnricher();
    }

    public OrganismFetcher getOrganismFetcher() {
        return getBasicEnricher().getOrganismFetcher();
    }

    public OrganismEnricherListener getOrganismEnricherListener() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    @Override
    public void setOrganismEnricherListener(OrganismEnricherListener listener) {

    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }
}
