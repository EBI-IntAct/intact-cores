package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.FeatureEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullFeatureUpdater;
import psidev.psi.mi.jami.enricher.listener.FeatureEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Feature;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;

import java.util.Collection;

/**
 * Feature merger based on the jami feature enricher.
 * It will override all properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class FeatureMergerOverride<F extends Feature, I extends AbstractIntactFeature> extends IntactDbMergerOverride<F, I> implements FeatureEnricher<F>{

    public FeatureMergerOverride() {
        super((Class<I>)AbstractIntactFeature.class, new FullFeatureUpdater<F>());
    }

    public FeatureMergerOverride(FeatureEnricher<F> basicEnricher) {
        super((Class<I>)AbstractIntactFeature.class, basicEnricher);
    }

    public FeatureMergerOverride(Class<I> intactClass) {
        super(intactClass, new FullFeatureUpdater<F>());
    }

    public FeatureMergerOverride(Class<I> intactClass, FeatureEnricher<F> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    protected FeatureEnricher<F> getBasicEnricher() {
        return (FeatureEnricher<F>)super.getBasicEnricher();
    }

    public void setFeaturesWithRangesToUpdate(Collection<F> features) {
        getBasicEnricher().setFeaturesWithRangesToUpdate(features);
    }

    public FeatureEnricherListener<F> getFeatureEnricherListener() {
        return getBasicEnricher().getFeatureEnricherListener();
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    @Override
    public void setFeatureEnricherListener(FeatureEnricherListener<F> listener) {
       getBasicEnricher().setFeatureEnricherListener(listener);
    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> cvEnricher) {

    }
}
