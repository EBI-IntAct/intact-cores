package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.FeatureEnricher;
import psidev.psi.mi.jami.enricher.impl.FullFeatureEnricher;
import psidev.psi.mi.jami.enricher.listener.FeatureEnricherListener;
import psidev.psi.mi.jami.model.Feature;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;

import java.util.Collection;

/**
 * Feature merger based on the jami feature enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactFeatureMergerEnrichOnly<F extends Feature, I extends AbstractIntactFeature> extends IntactDbMergerEnrichOnly<F, I> implements FeatureEnricher<F>{

    public IntactFeatureMergerEnrichOnly() {
        super((Class<I>)AbstractIntactFeature.class, new FullFeatureEnricher<F>());
    }

    public IntactFeatureMergerEnrichOnly(FeatureEnricher<F> basicEnricher) {
        super((Class<I>)AbstractIntactFeature.class, basicEnricher);
    }

    public IntactFeatureMergerEnrichOnly(Class<I> intactClass) {
        super(intactClass, new FullFeatureEnricher<F>());
    }

    public IntactFeatureMergerEnrichOnly(Class<I> intactClass, FeatureEnricher<F> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    protected FeatureEnricher<F> getBasicEnricher() {
        return (FeatureEnricher<F>)super.getBasicEnricher();
    }

    public void setFeaturesWithRangesToUpdate(Collection<F> features) {
        getBasicEnricher().setFeaturesWithRangesToUpdate(features);
    }

    public void setFeatureEnricherListener(FeatureEnricherListener<F> featureEnricherListener) {
        getBasicEnricher().setFeatureEnricherListener(featureEnricherListener);
    }

    public FeatureEnricherListener<F> getFeatureEnricherListener() {
        return getBasicEnricher().getFeatureEnricherListener();
    }

    public void setCvTermEnricher(CvTermEnricher cvTermEnricher) {
        getBasicEnricher().setCvTermEnricher(cvTermEnricher);
    }

    public CvTermEnricher getCvTermEnricher() {
        return getBasicEnricher().getCvTermEnricher();
    }
}
