package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.EntityPoolEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullExperimentalEntityPoolUpdater;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.ExperimentalEntityPool;
import psidev.psi.mi.jami.model.FeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntityPool;

import java.util.Comparator;

/**
 * Experimental entity pool merger based on the jami entity enricher.
 * It will override properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class ExperimentalEntityPoolMergerOverride
        extends ParticipantEvidenceMergerOverride<ExperimentalEntityPool, IntactExperimentalEntityPool> implements EntityPoolEnricher<ExperimentalEntityPool, FeatureEvidence>, ParticipantEvidenceEnricher<ExperimentalEntityPool>{

    public ExperimentalEntityPoolMergerOverride() {
        super(IntactExperimentalEntityPool.class, new FullExperimentalEntityPoolUpdater());
    }

    public ExperimentalEntityPoolMergerOverride(ParticipantEvidenceEnricher<ExperimentalEntityPool> basicEnricher) {
        super(IntactExperimentalEntityPool.class, basicEnricher);
    }

    public ParticipantEnricher getParticipantEnricher() {
        return null;
    }

    public void setParticipantComparator(Comparator<Entity> interactorComparator) {
        ((EntityPoolEnricher)getBasicEnricher()).setParticipantComparator(interactorComparator);
    }

    public Comparator<Entity> getParticipantComparator() {
        return ((EntityPoolEnricher)getBasicEnricher()).getParticipantComparator();
    }
}
