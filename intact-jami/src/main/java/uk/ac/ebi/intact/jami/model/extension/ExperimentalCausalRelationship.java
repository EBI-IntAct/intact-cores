package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ExperimentalEntity;

import javax.persistence.*;
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
public class ExperimentalCausalRelationship extends AbstractIntactCausalRelationship<ExperimentalEntity> implements CausalRelationship {

    protected ExperimentalCausalRelationship(){
        super();
    }

    public ExperimentalCausalRelationship(CvTerm relationType, ExperimentalEntity target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactExperimentalEntity.class, optional = false)
    @JoinColumn( name = "experimental_target_ac", referencedColumnName = "ac" )
    @Target(IntactExperimentalEntity.class)
    @NotNull
    public ExperimentalEntity getTarget() {
        return super.getTarget();
    }
}
