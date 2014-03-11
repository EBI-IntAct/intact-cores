package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.FeatureEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.impl.CompositeInteractorEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantEnricher;
import psidev.psi.mi.jami.enricher.listener.ParticipantEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.Feature;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;

/**
 * Entity merger based on the jami participant enricher.
 * It will override properties from database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class EntityMergerOverride<E extends Entity, I extends AbstractIntactEntity, F extends Feature> extends IntactDbMergerOverride<E, I> implements ParticipantEnricher<E,F> {

    public EntityMergerOverride() {
        super((Class<I>)AbstractIntactEntity.class, new FullParticipantEnricher<E,F>());
    }

    public EntityMergerOverride(ParticipantEnricher<E, F> basicEnricher) {
        super((Class<I>)AbstractIntactEntity.class, basicEnricher);
    }

    public EntityMergerOverride(Class<I> intactClass) {
        super(intactClass, new FullParticipantEnricher<E,F>());
    }

    public EntityMergerOverride(Class<I> intactClass, ParticipantEnricher<E, F> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    protected ParticipantEnricher<E,F> getBasicEnricher() {
        return (ParticipantEnricher<E,F>)super.getBasicEnricher();
    }

    public CompositeInteractorEnricher getInteractorEnricher() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public FeatureEnricher getFeatureEnricher() {
        return null;
    }

    public ParticipantEnricherListener getParticipantEnricherListener() {
        return null;
    }
}
