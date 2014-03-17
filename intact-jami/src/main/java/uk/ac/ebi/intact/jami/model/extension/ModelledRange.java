package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.Position;
import psidev.psi.mi.jami.model.ResultingSequence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Intact implementation of range attached to modelled features
 * NOTE: when modelled ranges and experimental ranges are in different tables, the where clause attached to this entity can be removed
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/01/14</pre>
 */
@Entity
@Table(name = "ia_range")
@Where(clause = "category = 'modelled'")
public class ModelledRange extends AbstractIntactRange<ModelledParticipant> {

    protected ModelledRange() {
        super();
    }

    public ModelledRange(Position start, Position end) {
        super(start, end);
    }

    public ModelledRange(Position start, Position end, boolean isLink) {
        super(start, end, isLink);
    }

    public ModelledRange(Position start, Position end, ResultingSequence resultingSequence) {
        super(start, end, resultingSequence);
    }

    public ModelledRange(Position start, Position end, ModelledParticipant participant) {
        super(start, end, participant);
    }

    public ModelledRange(Position start, Position end, boolean isLink, ResultingSequence resultingSequence) {
        super(start, end, isLink, resultingSequence);
    }

    public ModelledRange(Position start, Position end, boolean isLink, ModelledParticipant participant) {
        super(start, end, isLink, participant);
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class)
    @JoinColumn(name = "modelled_participant_ac", referencedColumnName = "ac")
    @Target(IntactModelledParticipant.class)
    @Override
    public ModelledParticipant getParticipant() {
        return super.getParticipant();
    }

    @Override
    public void setParticipant(Participant participant) {
        if (!(participant instanceof ModelledParticipant)){
            throw new IllegalArgumentException("A range attached to a modelled participant can only refer to a modelled participant.");
        }
        super.setParticipant(participant);
    }
}
