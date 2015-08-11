package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.Position;
import psidev.psi.mi.jami.model.Range;
import psidev.psi.mi.jami.model.ResultingSequence;
import psidev.psi.mi.jami.utils.PositionUtils;
import psidev.psi.mi.jami.utils.comparator.range.UnambiguousRangeAndResultingSequenceComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

import javax.persistence.*;

/**
 * abstract Intact implementation of range
 *
 * NOTE: for backward compatibility with intact-core, all the ranges are stored in same table but in the future, we will store ranges in two different tables depending if they are attached to feature evidences
 * and/or modelled features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/01/14</pre>
 */
@MappedSuperclass
public abstract class AbstractIntactRange<P extends psidev.psi.mi.jami.model.Entity> extends AbstractIntactPrimaryObject implements Range{

    private Position start;
    private Position end;
    private boolean isLink;

    private ResultingSequence resultingSequence;
    private P participant;

    protected AbstractIntactRange(){

    }

    public AbstractIntactRange(Position start, Position end){
        setPositions(start, end);
    }

    public AbstractIntactRange(Position start, Position end, boolean isLink){
        this(start, end);
        this.isLink = isLink;
    }

    public AbstractIntactRange(Position start, Position end, ResultingSequence resultingSequence){
        this(start, end);
        this.resultingSequence = resultingSequence;
    }

    public AbstractIntactRange(Position start, Position end, boolean isLink, ResultingSequence resultingSequence){
        this(start, end, isLink);
        this.resultingSequence = resultingSequence;
    }

    public AbstractIntactRange(Position start, Position end, P participant){
        this(start, end);
        this.participant = participant;
    }

    public AbstractIntactRange(Position start, Position end, boolean isLink, P participant){
        this(start, end, isLink);
        this.participant = participant;
    }

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="start", column = @Column(name="fromintervalstart") ),
            @AttributeOverride(name="end", column = @Column(name="fromintervalend") )
    } )
    @AssociationOverrides( { @AssociationOverride(name = "status", joinColumns = @JoinColumn(name = "fromfuzzytype_ac")) })
    @Target(IntactPosition.class)
    public Position getStart() {
        return this.start;
    }

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="start", column = @Column(name="tointervalstart") ),
            @AttributeOverride(name="end", column = @Column(name="tointervalend") )
    } )
    @AssociationOverrides( { @AssociationOverride(name = "status", joinColumns = @JoinColumn(name = "tofuzzytype_ac")) })
    @Target(IntactPosition.class)
    public Position getEnd() {
        return this.end;
    }

    public void setPositions(Position start, Position end) {
        if (start == null){
            throw new IllegalArgumentException("The start position is required and cannot be null");
        }
        if (end == null){
            throw new IllegalArgumentException("The end position is required and cannot be null");
        }

        if (start.getEnd() != 0 && end.getStart() != 0 && start.getEnd() > end.getStart()){
            throw new IllegalArgumentException("The start position cannot be ending before the end position");
        }
        this.start = start;
        this.end = end;
    }

    @Column( name = "link" )
    @Type( type = "yes_no" )
    public boolean isLink() {
        return this.isLink;
    }

    public void setLink(boolean link) {
        this.isLink = link;
    }

    @Transient
    public ResultingSequence getResultingSequence() {
        return this.resultingSequence;
    }

    public void setResultingSequence(ResultingSequence resultingSequence) {
        this.resultingSequence = resultingSequence;
    }

    @Transient
    public P getParticipant() {
        return this.participant;
    }

    public void setParticipant(Entity participant) {
        this.participant = (P)participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof Range)){
            return false;
        }

        return UnambiguousRangeAndResultingSequenceComparator.areEquals(this, (Range) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousRangeAndResultingSequenceComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return start.toString() + " - " + end.toString() + (isLink ? "(linked)" : "");
    }

    private void setStart(Position start) {
        this.start = start;
    }

    private void setEnd(Position end) {
        this.end = end;
    }

    @Type( type = "yes_no" )
    @Deprecated
    /**
     * @deprecated use Position.isUndetermined method to know if a specific position is undetermined
     */
    private boolean isUndetermined() {
        if (start == null || end == null){
            return true;
        }
        return PositionUtils.isUndetermined(start) && PositionUtils.isUndetermined(end);
    }

    /**
     * Undetermined is true only both fuzzy types are of UNDETERMINED type. For all other instances, it is false.
     */
    @Deprecated
    private void setUndetermined(boolean value) {
        // nothing to do here, the setter is for hibernate and backward compatibility only
    }
}
