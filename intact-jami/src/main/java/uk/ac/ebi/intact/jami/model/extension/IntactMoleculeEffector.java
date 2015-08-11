package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.AllostericEffectorType;
import psidev.psi.mi.jami.model.ModelledEntity;
import psidev.psi.mi.jami.model.MoleculeEffector;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Intact implementation of molecule effector
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Embeddable
public class IntactMoleculeEffector implements MoleculeEffector {

    private ModelledEntity participant;

    protected IntactMoleculeEffector(){

    }

    public IntactMoleculeEffector(ModelledEntity participant){
        if (participant == null){
            throw new IllegalArgumentException("The participant of a MoleculeEffector cannot be null.");
        }
        this.participant = participant;
    }

    @ManyToOne( targetEntity = IntactModelledParticipant.class )
    @JoinColumn( name = "molecule_effector_ac" , referencedColumnName = "ac")
    @Target(IntactModelledParticipant.class)
    public ModelledEntity getMolecule() {
        return participant;
    }

    public void setMolecule(ModelledEntity participant) {
        if (participant == null){
            throw new IllegalArgumentException("The participant of a MoleculeEffector cannot be null.");
        }
        this.participant = participant;
    }

    @Transient
    public AllostericEffectorType getEffectorType() {
        return AllostericEffectorType.molecule;
    }

    @Override
    public String toString() {
        return "molecule effector: " + participant.toString();
    }
}