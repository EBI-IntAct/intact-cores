package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Abstract Intact causal relationship.
 *
 * Note: as we want to keep a foreign key to the target entity and in the future we would have two tables for entities: one modelled entity table and one experimentsl
 * entity table, we will need different extensions of causal relationships so we can keep different foreign keys to modelled entity table or experimental entity table
 *
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_causal_relationship")
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractIntactCausalRelationship<T extends psidev.psi.mi.jami.model.Entity> extends AbstractAuditable implements CausalRelationship {

    private CvTerm relationType;
    private T target;
    private Long id;

    protected AbstractIntactCausalRelationship(){
    }

    public AbstractIntactCausalRelationship(CvTerm relationType, T target){
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

    @Transient
    public T getTarget() {
        return target;
    }

    public void setRelationType(CvTerm relationType) {
        if (relationType == null){
            throw new IllegalArgumentException("The relationType in a CausalRelationship cannot be null");
        }
        this.relationType = relationType;
    }

    public void setTarget(T target) {
        if (target == null){
            throw new IllegalArgumentException("The participant target in a CausalRelationship cannot be null");
        }
        this.target = target;
    }

    @Override
    public String toString() {
        return relationType.toString() + ": " + target.toString();
    }
}
