package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import javax.persistence.Entity;
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
@Table(name = "ia_mod_causal_relations")
public class ModelledCausalRelationship extends AbstractIntactCausalRelationship<ModelledEntity> implements CausalRelationship {

    protected ModelledCausalRelationship(){
        super();
    }

    public ModelledCausalRelationship(CvTerm relationType, Participant target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class, optional = false)
    @JoinColumn( name = "target_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    @NotNull
    public ModelledEntity getTarget() {
        return super.getTarget();
    }
}
