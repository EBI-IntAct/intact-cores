package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * IntAct class for causal relationship having participant evidences as target
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_experimental_causal_relationship")
public class ExperimentalCausalRelationship extends AbstractIntactCausalRelationship<Participant> implements CausalRelationship {

    protected ExperimentalCausalRelationship(){
        super();
    }

    public ExperimentalCausalRelationship(CvTerm relationType, ParticipantEvidence target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class)
    @JoinColumn( name = "modelled_target_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    private ModelledParticipant getModelledTarget(){
        Participant target = getTarget();
        return target instanceof ModelledParticipant ? (ModelledParticipant)target : null;
    }

    @ManyToOne(targetEntity = IntactParticipantEvidence.class)
    @JoinColumn( name = "experimental_target_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    private ParticipantEvidence getExperimentalTarget(){
        Participant target = getTarget();
        return target instanceof ParticipantEvidence ? (ParticipantEvidence)target : null;
    }

    private void setModelledTarget(ModelledParticipant modelled){
       super.setTarget(modelled);
    }

    private void setExperimentalTarget(ParticipantEvidence evidence){
        super.setTarget(evidence);
    }
}
