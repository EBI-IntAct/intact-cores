/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.jami.model;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents a sequence chunk of a polymer.
 *
 * @author marine Dumousseau
 */
@Entity
@Table( name = "ia_sequence_chunk", uniqueConstraints = {@UniqueConstraint(columnNames={"parent", "sequenceIndex"})} )
public class SequenceChunk extends AbstractIntactPrimaryObject {

    ///////////////////////////////////////
    //attributes

    /**
     * To who belongs that chunk.
     */
    private Polymer parent;

    /**
     * The content of the sequence chunk.
     */
    private String sequenceChunk;

    /**
     * Chunk order.
     */
    private int sequenceIndex;

    ///////////////////////////////////////
    // constructors
    public SequenceChunk() {
    }

    public SequenceChunk(int aSequenceIndex, String aSequenceChunk) {
        this.sequenceIndex = aSequenceIndex;
        this.sequenceChunk = aSequenceChunk;
    }

    ///////////////////////////////////////
    // associations

    ///////////////////////////////////////
    //access methods for attributes
    @ManyToOne( targetEntity = IntactPolymer.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac")
    @Target(IntactPolymer.class)
    public Polymer getParent() {
        return parent;
    }

    public void setParent( Polymer parent ) {
        this.parent = parent;
    }

    @Column( name = "sequence_chunk", length = IntactUtils.MAX_SEQ_LENGTH_PER_CHUNK, nullable = false)
    @NotNull
    public String getSequenceChunk() {
        return sequenceChunk;
    }

    public void setSequenceChunk( String sequenceChunk ) {
        this.sequenceChunk = sequenceChunk;
    }

    @Column( name = "sequence_index" )
    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex( int sequenceIndex ) {
        this.sequenceIndex = sequenceIndex;
    }

} // end Xref




