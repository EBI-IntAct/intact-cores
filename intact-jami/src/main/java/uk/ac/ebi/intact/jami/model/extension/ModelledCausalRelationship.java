package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Intact causal relationship having a modelled entity as target.
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_modelled_causal_relationship")
public class ModelledCausalRelationship extends AbstractIntactCausalRelationship<ModelledParticipant> implements CausalRelationship {

    protected ModelledCausalRelationship(){
        super();
    }

    public ModelledCausalRelationship(CvTerm relationType, ModelledParticipant target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class, optional = false)
    @JoinColumn( name = "target_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    @NotNull
    public ModelledParticipant getTarget() {
        return super.getTarget();
    }
}
