package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.enricher.CuratedPublicationEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullCuratedPublicationEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullPublicationEnricher;
import psidev.psi.mi.jami.enricher.listener.PublicationEnricherListener;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.impl.CuratedPublicationSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.PublicationSynchronizer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Publication merger based on the jami publication enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class PublicationMergerEnrichOnly extends IntactDbMergerEnrichOnly<Publication, IntactPublication> implements PublicationEnricher {

    public PublicationMergerEnrichOnly(PublicationSynchronizer intactSynchronizer){
        super(IntactPublication.class, new FullPublicationEnricher(intactSynchronizer));
    }

    @Override
    protected PublicationEnricher getBasicEnricher() {
        return (PublicationEnricher) super.getBasicEnricher();
    }

    public PublicationFetcher getPublicationFetcher() {
        return getBasicEnricher().getPublicationFetcher();
    }

    public PublicationEnricherListener getPublicationEnricherListener() {
        return null;
    }

    public SourceEnricher getSourceEnricher() {
        return null;
    }
}