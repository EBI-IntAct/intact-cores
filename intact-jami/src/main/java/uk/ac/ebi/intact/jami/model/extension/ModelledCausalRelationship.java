package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@DiscriminatorValue("modelled")
public class ModelledCausalRelationship extends AbstractIntactCausalRelationship<ModelledEntity> implements CausalRelationship {

    protected ModelledCausalRelationship(){
        super();
    }

    public ModelledCausalRelationship(CvTerm relationType, ModelledEntity target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactModelledEntity.class, optional = false)
    @JoinColumn( name = "modelled_target_ac", referencedColumnName = "ac" )
    @Target(IntactModelledEntity.class)
    @NotNull
    public ModelledEntity getTarget() {
        return super.getTarget();
    }
}
