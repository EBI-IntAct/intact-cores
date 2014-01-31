package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.CvTermFetcher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.impl.FullSourceUpdater;
import psidev.psi.mi.jami.enricher.listener.CvTermEnricherListener;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.IntactSourceSynchronizer;

/**
 * Source merger that overrides all properties
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactSourceMergerOverride extends IntactDbMergerOverride<Source, IntactSource> implements SourceEnricher {

    public IntactSourceMergerOverride(IntactSourceSynchronizer intactSynchronizer){
        super(IntactSource.class, new FullSourceUpdater(intactSynchronizer));
    }

    public void setPublicationEnricher(PublicationEnricher enricher) {
        getBasicEnricher().setPublicationEnricher(enricher);
    }

    public PublicationEnricher getPublicationEnricher() {
        return getBasicEnricher().getPublicationEnricher();
    }

    @Override
    protected SourceEnricher getBasicEnricher() {
        return (SourceEnricher)super.getBasicEnricher();
    }

    public CvTermFetcher<Source> getCvTermFetcher() {
        return getBasicEnricher().getCvTermFetcher();
    }

    public void setCvTermEnricherListener(CvTermEnricherListener<Source> listener) {
        getBasicEnricher().setCvTermEnricherListener(listener);
    }

    public CvTermEnricherListener<Source> getCvTermEnricherListener() {
        return getBasicEnricher().getCvTermEnricherListener();
    }
}
