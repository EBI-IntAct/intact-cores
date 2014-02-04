package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.impl.FullPublicationUpdater;
import psidev.psi.mi.jami.enricher.listener.PublicationEnricherListener;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.IntactPublicationSynchronizer;

/**
 * Publication merger based on the jami publication enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactPublicationMergerOverride extends IntactDbMergerOverride<Publication, IntactPublication> implements PublicationEnricher {

    public IntactPublicationMergerOverride(IntactPublicationSynchronizer intactSynchronizer){
        super(IntactPublication.class, new FullPublicationUpdater(intactSynchronizer));
    }

    @Override
    protected PublicationEnricher getBasicEnricher() {
        return (PublicationEnricher) super.getBasicEnricher();
    }

    public PublicationFetcher getPublicationFetcher() {
        return getBasicEnricher().getPublicationFetcher();
    }

    public void setSourceEnricher(SourceEnricher cvTermEnricher) {
        getBasicEnricher().setSourceEnricher(cvTermEnricher);
    }

    public SourceEnricher getSourceEnricher() {
        return getBasicEnricher().getSourceEnricher();
    }

    public void setPublicationEnricherListener(PublicationEnricherListener listener) {
        getBasicEnricher().setPublicationEnricherListener(listener);
    }

    public PublicationEnricherListener getPublicationEnricherListener() {
        return getBasicEnricher().getPublicationEnricherListener();
    }
}
