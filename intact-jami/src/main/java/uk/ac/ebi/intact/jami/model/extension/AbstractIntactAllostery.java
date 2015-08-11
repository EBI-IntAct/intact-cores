package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Intact implementation of allostery
 *
 * The discriminator value for the 'category' column of a allostery element is 'allostery'
 *
 * This class does not have any persistent allosteric effector
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
public abstract class AbstractIntactAllostery<T extends AllostericEffector> extends AbstractIntactCooperativeEffect implements Allostery<T>{

    private CvTerm allostericMechanism;
    private CvTerm allosteryType;
    private ModelledEntity allostericMolecule;
    private T allostericEffector;

    protected AbstractIntactAllostery(){
        super();
    }

    public AbstractIntactAllostery(CvTerm outcome, ModelledEntity allostericMolecule, T allostericEffector) {
        super(outcome);
        if (allostericMolecule == null){
            throw new IllegalArgumentException("The allosteric molecule cannot be null");
        }
        this.allostericMolecule = allostericMolecule;
        if (allostericEffector == null){
            throw new IllegalArgumentException("The allosteric effector cannot be null");
        }
        this.allostericEffector = allostericEffector;
    }

    public AbstractIntactAllostery(CvTerm outcome, CvTerm response, ModelledEntity allostericMolecule, T allostericEffector) {
        super(outcome, response);
        if (allostericMolecule == null){
            throw new IllegalArgumentException("The allosteric molecule cannot be null");
        }
        this.allostericMolecule = allostericMolecule;
        if (allostericEffector == null){
            throw new IllegalArgumentException("The allosteric effector cannot be null");
        }
        this.allostericEffector = allostericEffector;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "mechanism_ac", referencedColumnName = "ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getAllostericMechanism() {
        return this.allostericMechanism;
    }

    public void setAllostericMechanism(CvTerm mechanism) {
        this.allostericMechanism = mechanism;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "type_ac", referencedColumnName = "ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getAllosteryType() {
        return this.allosteryType;
    }

    public void setAllosteryType(CvTerm type) {
        this.allosteryType = type;
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class, optional = false)
    @JoinColumn( name = "allosteric_molecule_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    @NotNull
    public ModelledEntity getAllostericMolecule() {
        return this.allostericMolecule;
    }

    public void setAllostericMolecule(ModelledEntity participant) {
        if (participant == null){
            throw new IllegalArgumentException("The allosteric molecule cannot be null");
        }
        this.allostericMolecule = participant;
    }

    @Transient
    public T getAllostericEffector() {
        return this.allostericEffector;
    }

    public void setAllostericEffector(T effector) {
        if (effector == null){
            throw new IllegalArgumentException("The allosteric effector cannot be null");
        }
        this.allostericEffector = effector;
    }

    @Override
    public String toString() {
        return "allostery: " + super.toString();
    }
}
