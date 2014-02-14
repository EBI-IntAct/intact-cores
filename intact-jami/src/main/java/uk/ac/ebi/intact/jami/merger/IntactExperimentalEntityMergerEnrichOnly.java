package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantEvidenceEnricher;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.FeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactExperimentalEntity;

/**
 * Experimental Entity merger based on the jami participant evidence enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactExperimentalEntityMergerEnrichOnly<E extends ExperimentalEntity, I extends AbstractIntactExperimentalEntity>
        extends IntactEntityMergerEnrichOnly<E, I, FeatureEvidence> implements ParticipantEvidenceEnricher<E> {

    public IntactExperimentalEntityMergerEnrichOnly() {
        super((Class<I>)AbstractIntactExperimentalEntity.class, new FullParticipantEvidenceEnricher<E>());
    }

    public IntactExperimentalEntityMergerEnrichOnly(ParticipantEvidenceEnricher<E> basicEnricher) {
        super((Class<I>)AbstractIntactExperimentalEntity.class, basicEnricher);
    }

    public IntactExperimentalEntityMergerEnrichOnly(Class<I> intactClass) {
        super(intactClass, new FullParticipantEvidenceEnricher<E>());
    }

    public IntactExperimentalEntityMergerEnrichOnly(Class<I> intactClass, ParticipantEvidenceEnricher<E> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    protected ParticipantEvidenceEnricher<E> getBasicEnricher() {
        return (ParticipantEvidenceEnricher<E>)super.getBasicEnricher();
    }

    public OrganismEnricher getOrganismEnricher() {
        return null;
    }
}
