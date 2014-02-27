package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.AllostericEffector;
import psidev.psi.mi.jami.model.Allostery;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Intact implementation of allostery
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@DiscriminatorValue("allostery")
public class IntactAllostery<T extends AllostericEffector> extends AbstractIntactCooperativeEffect implements Allostery<T>{

    private CvTerm allostericMechanism;
    private CvTerm allosteryType;
    private ModelledParticipant allostericMolecule;
    private T allostericEffector;

    protected IntactAllostery(){
        super();
    }

    public IntactAllostery(CvTerm outcome, ModelledParticipant allostericMolecule, T allostericEffector) {
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

    public IntactAllostery(CvTerm outcome, CvTerm response, ModelledParticipant allostericMolecule, T allostericEffector) {
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
    public ModelledParticipant getAllostericMolecule() {
        return this.allostericMolecule;
    }

    public void setAllostericMolecule(ModelledParticipant participant) {
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
