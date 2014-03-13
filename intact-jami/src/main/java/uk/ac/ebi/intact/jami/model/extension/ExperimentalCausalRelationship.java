package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ParticipantEvidence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Abstract IntAct class for causal relationship
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@DiscriminatorValue("experimental")
public class ExperimentalCausalRelationship extends AbstractIntactCausalRelationship<ParticipantEvidence> implements CausalRelationship {

    protected ExperimentalCausalRelationship(){
        super();
    }

    public ExperimentalCausalRelationship(CvTerm relationType, ParticipantEvidence target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactParticipantEvidence.class, optional = false)
    @JoinColumn( name = "experimental_target_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    @NotNull
    public ParticipantEvidence getTarget() {
        return super.getTarget();
    }
}
