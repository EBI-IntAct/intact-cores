package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.SourceFetcher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.FullSourceEnricher;
import psidev.psi.mi.jami.enricher.listener.SourceEnricherListener;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.IntactSourceSynchronizer;

import java.util.Collection;

/**
 * Source merger based on the jami source enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactSourceMergerEnrichOnly extends IntactDbMergerEnrichOnly<IntactSource> implements SourceEnricher {

    private SourceEnricher sourceEnricher;

    public IntactSourceMergerEnrichOnly(IntactSourceSynchronizer intactSynchronizer){
        this.sourceEnricher = new FullSourceEnricher(intactSynchronizer);
    }

    public SourceFetcher getSourceFetcher() {
        return this.sourceEnricher.getSourceFetcher();
    }

    public void setPublicationEnricher(PublicationEnricher enricher) {
        this.sourceEnricher.setPublicationEnricher(enricher);
    }

    public PublicationEnricher getPublicationEnricher() {
        return this.sourceEnricher.getPublicationEnricher();
    }

    public void setSourceEnricherListener(SourceEnricherListener listener) {
        this.sourceEnricher.setSourceEnricherListener(listener);
    }

    public SourceEnricherListener getSourceEnricherListener() {
        return this.sourceEnricher.getSourceEnricherListener();
    }

    public void enrich(Source object) throws EnricherException {
        this.sourceEnricher.enrich(object);
    }

    public void enrich(Collection<Source> objects) throws EnricherException {
        this.sourceEnricher.enrich(objects);
    }

    public void enrich(Source objectToEnrich, Source objectSource) throws EnricherException {
        if (objectToEnrich instanceof IntactSource && objectSource instanceof IntactSource){
            merge((IntactSource)objectToEnrich, (IntactSource)objectSource);
        }
        else{
            this.sourceEnricher.enrich(objectToEnrich, objectSource);
        }
    }

    @Override
    protected void mergeOtherProperties(IntactSource obj1, IntactSource obj2) {
        // first merge audit properties
        super.mergeOtherProperties(obj1, obj2);
        // then merge general properties
        try {
            // WARNING: we enrich the object loaded from the database, not the object source!
            this.sourceEnricher.enrich(obj2, obj2);
        } catch (EnricherException e) {
            throw new IllegalStateException("Cannot merge "+obj1 + " with "+obj2, e);
        }
    }
}
