package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * IntAct class for causal relationship having participant evidences as target
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@DiscriminatorValue("modelled")
public class ExperimentalCausalRelationshipWithModelledTarget extends AbstractExperimentalCausalRelationship<ModelledParticipant> implements CausalRelationship {

    protected ExperimentalCausalRelationshipWithModelledTarget(){
        super();
    }

    public ExperimentalCausalRelationshipWithModelledTarget(CvTerm relationType, ModelledParticipant target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class, optional = false)
    @JoinColumn( name = "modelled_target_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    @NotNull
    public ModelledParticipant getTarget() {
        return super.getTarget();
    }
}
