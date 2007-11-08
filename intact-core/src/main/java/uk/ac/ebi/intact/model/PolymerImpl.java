/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the super class for Protein and Nucleic Acid types.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.PolymerImpl" )
public abstract class PolymerImpl extends InteractorImpl implements Polymer {

    //Constants

    /**
     * As the maximum size of database objects is limited, the sequence is represented as
     * an array of strings of maximum length.
     */
    public static final int MAX_SEQ_LENGTH_PER_CHUNK = 1000;

    //attributes

    /**
     * Represents the CRC64 checksum. This checksum is used to
     * detect potential inconsistencies between the sequence the object
     * refers to and the external sequence object, for example when the external
     * object has been updated.
     */
    private String crc64;

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    private List<SequenceChunk> sequenceChunks = new ArrayList<SequenceChunk>();

    // Constructors

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    public PolymerImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that Polymer cannot be
     * created without at least a shortLabel and an owner specified.
     *
     * @param owner      The Institution which owns this instance
     * @param source     The biological source of the Protein observation
     * @param shortLabel The memorable label to identify this instance
     * @param type       The interactor type
     */
    protected PolymerImpl( Institution owner, BioSource source, String shortLabel, CvInteractorType type ) {
        super( shortLabel, owner, type );
        setBioSource( source );
    }

    // access methods for attributes
    @Transient
    public String getSequence() {

        if ( ( null == sequenceChunks ) || 0 == ( sequenceChunks.size() ) ) {
            return null;
        }
        // Re-join the sequence chunks.
        // The correct ordering is done during retrieval from the database.
        // It relies on the OJB setting (mapping)
        StringBuffer sequence = new StringBuffer();
        for ( SequenceChunk sequenceChunk : sequenceChunks ) {
            sequence.append( sequenceChunk.getSequenceChunk() );
        }
        return sequence.toString();
    }

    public List<SequenceChunk> setSequence( String aSequence ) {
        // Save work if the new sequence is identical to the old one.

        if( aSequence == null ) {
            sequenceChunks = new ArrayList<SequenceChunk>();
            return sequenceChunks;
        }

        if ( aSequence.equals( getSequence() ) ) {
            return Collections.EMPTY_LIST;
        }

        // The container to hold redundant chunks.
        ArrayList<SequenceChunk> chunkPool = null;

        // All old data are kept, we try to recycle as much chunk as possible
        if ( sequenceChunks == null ) {
            sequenceChunks = new ArrayList<SequenceChunk>();
        } else if ( !sequenceChunks.isEmpty() ) {
            // There is existing chunk ... prepare them for recycling.
            chunkPool = new ArrayList<SequenceChunk>( sequenceChunks.size() );
            chunkPool.addAll( sequenceChunks );
            int count = chunkPool.size();

            // clean chunk to recycle
            for ( int i = 0; i < count; i++ ) {
                SequenceChunk sc = chunkPool.get( i );
                removeSequenceChunk( sc );
            }
        }

        // Note the use of integer operations
        int chunkCount = aSequence.length() / MAX_SEQ_LENGTH_PER_CHUNK;
        if ( aSequence.length() % MAX_SEQ_LENGTH_PER_CHUNK > 0 ) {
            chunkCount++;
        }

        for ( int i = 0; i < chunkCount; i++ ) {
            String chunk = aSequence.substring( i * MAX_SEQ_LENGTH_PER_CHUNK,
                                                Math.min( ( i + 1 ) * MAX_SEQ_LENGTH_PER_CHUNK,
                                                          aSequence.length() ) );

            if ( chunkPool != null && chunkPool.size() > 0 ) {
                // recycle chunk
                SequenceChunk sc = chunkPool.remove( 0 );
                sc.setSequenceChunk( chunk );
                sc.setSequenceIndex( i );
                addSequenceChunk( sc );
            } else {
                // create new chunk
                addSequenceChunk( new SequenceChunk( i, chunk ) );
            }
        }
        // Check for null chunkPool
        return chunkPool == null ? Collections.EMPTY_LIST : chunkPool;
    }

    public String getCrc64() {
        return crc64;
    }

    public void setCrc64( String crc64 ) {
        this.crc64 = crc64;
    }

    @OneToMany( mappedBy = "parent", cascade = CascadeType.ALL )
    @IndexColumn( name = "sequence_index" )
    public List<SequenceChunk> getSequenceChunks() {
        return sequenceChunks;
    }

    public void setSequenceChunks( List<SequenceChunk> sequenceChunks ) {
        this.sequenceChunks = sequenceChunks;
    }

    protected void addSequenceChunk( SequenceChunk sequenceChunk ) {
        if ( !this.sequenceChunks.contains( sequenceChunk ) ) {
            this.sequenceChunks.add( sequenceChunk );
            sequenceChunk.setParent( this );
        }
    }

    protected void removeSequenceChunk( SequenceChunk sequenceChunk ) {
        boolean removed = this.sequenceChunks.remove( sequenceChunk );
        if ( removed ) {
            sequenceChunk.setParent( this );
        }
    }

    /**
     * Equality for Proteins is currently based on equality for
     * <code>Interactors</code>, the sequence and the crc64 checksum.
     *
     * @param obj The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see InteractorImpl
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( !super.equals( obj ) ) {
            return false;
        }
        if ( obj.getClass() != getClass() ) {
            return false;
        }

        final Polymer other = ( Polymer ) obj;

        // TODO do we need to check sequence and CRC64
        if ( crc64 != null ) {
            if ( !crc64.equals( other.getCrc64() ) ) {
                return false;
            }
        } else {
            if ( other.getCrc64() != null ) {
                return false;
            }
        }

        if ( getSequence() != null ) {
            if ( !getSequence().equals( other.getSequence() ) ) {
                return false;
            }
        } else {
            if ( other.getSequence() != null ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Remember that hashCode and equals methods has to be develop in parallel
     * since : if a.equals(b) then a.hashCode() == b.hashCode()
     * The other way round is NOT true.
     * Unless it could break consistancy when storing object in a hash-based
     * collection such as HashMap...
     */
    @Override
    public int hashCode() {
        int code = super.hashCode();
        if ( getSequence() != null ) {
            code = code * 29 + getSequence().hashCode();
        }
        if ( crc64 != null ) {
            code = code * 29 + crc64.hashCode();
        }
        return code;
    }

    @Override
    public String toString() {
        return super.toString() + " [ CRC64: " + getCrc64() + " Sequence: " + getSequence() + "]";
    }
}




