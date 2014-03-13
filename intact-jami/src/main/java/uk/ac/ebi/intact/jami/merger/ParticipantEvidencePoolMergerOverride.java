package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.enricher.ParticipantPoolEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantEvidencePoolUpdater;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.ParticipantEvidencePool;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidencePool;

import java.util.Comparator;

/**
 * Experimental entity pool merger based on the jami entity enricher.
 * It will override properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class ParticipantEvidencePoolMergerOverride
        extends ParticipantEvidenceMergerOverride<ParticipantEvidencePool, IntactParticipantEvidencePool> implements ParticipantPoolEnricher<ParticipantEvidencePool, FeatureEvidence>, ParticipantEvidenceEnricher<ParticipantEvidencePool>{

    public ParticipantEvidencePoolMergerOverride() {
        super(IntactParticipantEvidencePool.class, new FullParticipantEvidencePoolUpdater());
    }

    public ParticipantEvidencePoolMergerOverride(ParticipantEvidenceEnricher<ParticipantEvidencePool> basicEnricher) {
        super(IntactParticipantEvidencePool.class, basicEnricher);
    }

    public ParticipantEnricher getParticipantEnricher() {
        return null;
    }

    public void setParticipantComparator(Comparator<Participant> interactorComparator) {
        ((ParticipantPoolEnricher)getBasicEnricher()).setParticipantComparator(interactorComparator);
    }

    public Comparator<Participant> getParticipantComparator() {
        return ((ParticipantPoolEnricher)getBasicEnricher()).getParticipantComparator();
    }
}
