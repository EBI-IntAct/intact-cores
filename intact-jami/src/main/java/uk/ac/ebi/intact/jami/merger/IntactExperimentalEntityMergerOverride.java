package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.enricher.impl.FullParticipantEvidenceUpdater;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.FeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactExperimentalEntity;

/**
 * Experimental Entity merger based on the jami participant evidence enricher.
 * It will override all properties from database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactExperimentalEntityMergerOverride<E extends ExperimentalEntity, I extends AbstractIntactExperimentalEntity>
        extends IntactEntityMergerOverride<E, I, FeatureEvidence> implements ParticipantEvidenceEnricher<E,FeatureEvidence> {

    public IntactExperimentalEntityMergerOverride() {
        super((Class<I>)AbstractIntactExperimentalEntity.class, new FullParticipantEvidenceUpdater<E, FeatureEvidence>());
    }

    public IntactExperimentalEntityMergerOverride(ParticipantEvidenceEnricher<E, FeatureEvidence> basicEnricher) {
        super((Class<I>)AbstractIntactExperimentalEntity.class, basicEnricher);
    }

    public IntactExperimentalEntityMergerOverride(Class<I> intactClass) {
        super(intactClass, new FullParticipantEvidenceUpdater<E, FeatureEvidence>());
    }

    public IntactExperimentalEntityMergerOverride(Class<I> intactClass, ParticipantEvidenceEnricher<E, FeatureEvidence> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    protected ParticipantEvidenceEnricher<E,FeatureEvidence> getBasicEnricher() {
        return (ParticipantEvidenceEnricher<E,FeatureEvidence>)super.getBasicEnricher();
    }

    public void setOrganismEnricher(OrganismEnricher organismEnricher) {
        getBasicEnricher().setOrganismEnricher(organismEnricher);
    }

    public OrganismEnricher getOrganismEnricher() {
        return getBasicEnricher().getOrganismEnricher();
    }
}
