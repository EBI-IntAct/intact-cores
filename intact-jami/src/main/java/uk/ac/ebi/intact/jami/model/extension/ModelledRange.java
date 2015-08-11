package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import javax.persistence.Entity;

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
public class ModelledRange extends AbstractIntactRange<ModelledEntity> {

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

    @Override
    @Embedded
    @Target(ModelledResultingSequence.class)
    public ResultingSequence getResultingSequence() {
        return super.getResultingSequence();
    }

    @ManyToOne(targetEntity = IntactModelledParticipant.class)
    @JoinColumn(name = "modelled_participant_ac", referencedColumnName = "ac")
    @Target(IntactModelledParticipant.class)
    @Override
    public ModelledEntity getParticipant() {
        return super.getParticipant();
    }

    @Override
    public void setParticipant(psidev.psi.mi.jami.model.Entity participant) {
        if (participant != null && !(participant instanceof ModelledEntity)){
            throw new IllegalArgumentException("A range attached to a modelled participant can only refer to a modelled participant.");
        }
        super.setParticipant(participant);
    }
}
