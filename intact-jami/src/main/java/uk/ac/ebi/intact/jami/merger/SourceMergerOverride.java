package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.CvTermFetcher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullSourceUpdater;
import psidev.psi.mi.jami.enricher.listener.CvTermEnricherListener;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.impl.SourceSynchronizer;

/**
 * Source merger that overrides all properties
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class SourceMergerOverride extends IntactDbMergerOverride<Source, IntactSource> implements SourceEnricher {

    public SourceMergerOverride(SourceSynchronizer intactSynchronizer){
        super(IntactSource.class, new FullSourceUpdater(intactSynchronizer));
    }

    public PublicationEnricher getPublicationEnricher() {
        return null;
    }

    @Override
    public void setPublicationEnricher(PublicationEnricher enricher) {
        // nothing to do
    }

    @Override
    protected SourceEnricher getBasicEnricher() {
        return (SourceEnricher)super.getBasicEnricher();
    }

    public CvTermFetcher<Source> getCvTermFetcher() {
        return getBasicEnricher().getCvTermFetcher();
    }

    public CvTermEnricherListener<Source> getCvTermEnricherListener() {
        return getBasicEnricher().getCvTermEnricherListener();
    }

    @Override
    public void setCvTermEnricherListener(CvTermEnricherListener<Source> listener) {
        getBasicEnricher().setCvTermEnricherListener(listener);
    }
}
