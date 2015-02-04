package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.FeatureEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.impl.CompositeInteractorEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantEnricher;
import psidev.psi.mi.jami.enricher.listener.EntityEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.Participant;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParticipant;

/**
 * Feature merger based on the jami feature enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class ParticipantMergerEnrichOnly<E extends Participant, I extends AbstractIntactParticipant, F extends Feature> extends IntactDbMergerEnrichOnly<E, I> implements ParticipantEnricher<E,F> {

    public ParticipantMergerEnrichOnly() {
        super((Class<I>)AbstractIntactParticipant.class, new FullParticipantEnricher<E,F>());
    }

    public ParticipantMergerEnrichOnly(ParticipantEnricher<E, F> basicEnricher) {
        super((Class<I>)AbstractIntactParticipant.class, basicEnricher);
    }

    public ParticipantMergerEnrichOnly(Class<I> intactClass) {
        super(intactClass, new FullParticipantEnricher<E,F>());
    }

    public ParticipantMergerEnrichOnly(Class<I> intactClass, ParticipantEnricher<E, F> basicEnricher) {
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

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }

    public FeatureEnricher getFeatureEnricher() {
        return null;
    }

    public EntityEnricherListener getParticipantEnricherListener() {
        return getBasicEnricher().getParticipantEnricherListener();
    }

    @Override
    public void setInteractorEnricher(CompositeInteractorEnricher interactorEnricher) {

    }

    @Override
    public void setFeatureEnricher(FeatureEnricher<F> enricher) {

    }

    @Override
    public void setParticipantEnricherListener(EntityEnricherListener listener) {
        getBasicEnricher().setParticipantEnricherListener(listener);
    }
}
