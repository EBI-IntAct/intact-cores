package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.ForceDiscriminator;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Participant;

import javax.persistence.*;

/**
 * Abstract IntAct class for causal relationship attached to participant evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_experimental_causal_relationship")
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@ForceDiscriminator
public abstract class AbstractExperimentalCausalRelationship<P extends Participant> extends AbstractIntactCausalRelationship<P> implements CausalRelationship {

    protected AbstractExperimentalCausalRelationship(){
        super();
    }

    public AbstractExperimentalCausalRelationship(CvTerm relationType, P target){
        super(relationType, target);
    }
}
