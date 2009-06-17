/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.List;

/**
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface Polymer extends Interactor {

    /**
     * @return a sequence as a string
     */
    public String getSequence();

    /**
     * Sets the current sequence.
     *
     * @param aSequence the sequence to set
     *
     * @return a list of SequenceChunk objects to remove. This list is non empty
     *         only when the current sequence is longer than the new sequence
     *         (i.e, <code>aSequence</code>).
     */
    public List<SequenceChunk> setSequence( String aSequence );

    /**
     * @return crc64 as a string
     */
    public String getCrc64();

    /**
     * Sets the crc64
     *
     * @param crc64 the crc64 value
     */
    public void setCrc64( String crc64 );

    /**
     * This method is mainly for testing purposes to examine the chunks array
     *
     * @return unmodifiable list of sequence chunks.
     */
    public List<SequenceChunk> getSequenceChunks();
}
