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
@Table(name = "ia_exp_causal_relations")
public class ExperimentalCausalRelationship extends AbstractIntactCausalRelationship<psidev.psi.mi.jami.model.Entity> implements CausalRelationship {

    protected ExperimentalCausalRelationship(){
        super();
    }

    public ExperimentalCausalRelationship(CvTerm relationType, psidev.psi.mi.jami.model.Entity target){
        super(relationType, target);
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class)
    @JoinColumn( name = "modelled_target_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    private ModelledEntity getModelledTarget(){
        psidev.psi.mi.jami.model.Entity target = getTarget();
        return target instanceof ModelledEntity ? (ModelledEntity)target : null;
    }

    @ManyToOne(targetEntity = IntactParticipantEvidence.class)
    @JoinColumn( name = "experimental_target_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    private ExperimentalEntity getExperimentalTarget(){
        psidev.psi.mi.jami.model.Entity target = getTarget();
        return target instanceof ExperimentalEntity ? (ExperimentalEntity)target : null;
    }

    private void setModelledTarget(ModelledEntity modelled){
        if (modelled != null){
            super.setTarget(modelled);
        }
    }

    private void setExperimentalTarget(ExperimentalEntity evidence){
        if (evidence != null){
            super.setTarget(evidence);
        }
    }
}
