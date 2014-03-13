package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.ParticipantPoolEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantPoolEnricher;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipantPool;
import psidev.psi.mi.jami.model.Participant;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipantPool;

import java.util.Comparator;

/**
 * Modelled entity pool merger based on the jami entity enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class ModelledParticipantPoolMergerEnrichOnly
        extends ModelledParticipantMergerEnrichOnly<ModelledParticipantPool, IntactModelledParticipantPool> implements ParticipantPoolEnricher<ModelledParticipantPool, ModelledFeature> {

    public ModelledParticipantPoolMergerEnrichOnly() {
        super(IntactModelledParticipantPool.class, new FullParticipantPoolEnricher<ModelledParticipantPool, ModelledFeature>());
    }

    public ModelledParticipantPoolMergerEnrichOnly(ParticipantPoolEnricher<ModelledParticipantPool, ModelledFeature> basicEnricher) {
        super(IntactModelledParticipantPool.class, basicEnricher);
    }

    @Override
    protected ParticipantPoolEnricher<ModelledParticipantPool, ModelledFeature> getBasicEnricher() {
        return (ParticipantPoolEnricher<ModelledParticipantPool, ModelledFeature>)super.getBasicEnricher();
    }

    public ParticipantEnricher getParticipantEnricher() {
        return null;
    }

    public void setParticipantComparator(Comparator<Participant> interactorComparator) {
         getBasicEnricher().setParticipantComparator(interactorComparator);
    }

    public Comparator<Participant> getParticipantComparator() {
        return getBasicEnricher().getParticipantComparator();
    }
}
