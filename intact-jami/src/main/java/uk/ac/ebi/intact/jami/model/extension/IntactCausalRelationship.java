package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;

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
@Table(name = "ia_causal_relationship")
public class IntactCausalRelationship extends AbstractAuditable implements CausalRelationship {

    private CvTerm relationType;
    private psidev.psi.mi.jami.model.Entity target;
    private Long id;

    protected IntactCausalRelationship(){
    }

    public IntactCausalRelationship(CvTerm relationType, psidev.psi.mi.jami.model.Entity target){
        if (relationType == null){
            throw new IllegalArgumentException("The relationType in a CausalRelationship cannot be null");
        }
        this.relationType = relationType;

        if (target == null){
            throw new IllegalArgumentException("The participat target in a CausalRelationship cannot be null");
        }
        this.target = target;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
    @SequenceGenerator(name="idGenerator", sequenceName="DEFAULT_ID_SEQ", initialValue = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "relationtype_ac", referencedColumnName = "ac" )
    @Target(IntactCvTerm.class)
    @NotNull
    public CvTerm getRelationType() {
        return relationType;
    }

    @ManyToOne(targetEntity = IntactExperimentalEntity.class, optional = false)
    @JoinColumn( name = "target_ac", referencedColumnName = "ac" )
    @Target(IntactExperimentalEntity.class)
    @NotNull
    public psidev.psi.mi.jami.model.Entity getTarget() {
        return target;
    }

    public void setRelationType(CvTerm relationType) {
        if (relationType == null){
            throw new IllegalArgumentException("The relationType in a CausalRelationship cannot be null");
        }
        this.relationType = relationType;
    }

    public void setTarget(psidev.psi.mi.jami.model.Entity target) {
        if (target == null){
            throw new IllegalArgumentException("The participat target in a CausalRelationship cannot be null");
        }
        this.target = target;
    }

    @Override
    public String toString() {
        return relationType.toString() + ": " + target.toString();
    }
}
