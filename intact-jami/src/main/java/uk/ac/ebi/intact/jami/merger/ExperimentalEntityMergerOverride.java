package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantEvidenceUpdater;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.FeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntity;

/**
 * Experimental Entity merger based on the jami participant evidence enricher.
 * It will override all properties from database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class ExperimentalEntityMergerOverride<E extends ExperimentalEntity, I extends IntactExperimentalEntity>
        extends EntityMergerOverride<E, I, FeatureEvidence> implements ParticipantEvidenceEnricher<E> {

    public ExperimentalEntityMergerOverride() {
        super((Class<I>)IntactExperimentalEntity.class, new FullParticipantEvidenceUpdater<E>());
    }

    public ExperimentalEntityMergerOverride(ParticipantEvidenceEnricher<E> basicEnricher) {
        super((Class<I>)IntactExperimentalEntity.class, basicEnricher);
    }

    public ExperimentalEntityMergerOverride(Class<I> intactClass) {
        super(intactClass, new FullParticipantEvidenceUpdater<E>());
    }

    public ExperimentalEntityMergerOverride(Class<I> intactClass, ParticipantEvidenceEnricher<E> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    protected ParticipantEvidenceEnricher<E> getBasicEnricher() {
        return (ParticipantEvidenceEnricher<E>)super.getBasicEnricher();
    }

    public OrganismEnricher getOrganismEnricher() {
        return getBasicEnricher().getOrganismEnricher();
    }
}
