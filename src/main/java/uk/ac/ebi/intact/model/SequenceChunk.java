/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Represents a crossreference to another database.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@Table( name = "ia_sequence_chunk" )
public class SequenceChunk extends AbstractAuditable {

    ///////////////////////////////////////
    //attributes

    /**
     * Sequence chunk accession number
     */
    private String ac;

    /**
     * To who belongs that chunk.
     */
    private Polymer parent;

    private String parentAc;

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

    public SequenceChunk( int aSequenceIndex, String aSequenceChunk ) {
        this.sequenceIndex = aSequenceIndex;
        this.sequenceChunk = aSequenceChunk;
    }

    ///////////////////////////////////////
    // associations
    @Id
    @GeneratedValue( generator = "intact-id" )
    @GenericGenerator( name = "intact-id", strategy = "uk.ac.ebi.intact.model.IntactIdGenerator" )
    public String getAc() {
        return ac;
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    ///////////////////////////////////////
    //access methods for attributes
    @ManyToOne( targetEntity = PolymerImpl.class )
    @JoinColumn( name = "parent_ac" )
    public Polymer getParent() {
        return parent;
    }

    public void setParent( Polymer parent ) {
        this.parent = parent;
    }

    @Column( name = "sequence_chunk", length = PolymerImpl.MAX_SEQ_LENGTH_PER_CHUNK )
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

    @Column( name = "parent_ac", insertable = false, updatable = false )
    public String getParentAc() {
        return parentAc;
    }

    public void setParentAc( String parentAc ) {
        this.parentAc = parentAc;
    }
} // end Xref




