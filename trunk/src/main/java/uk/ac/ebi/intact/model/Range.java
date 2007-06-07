/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * <p/>
 * Represents a location on a sequence. </p>
 * <p/>
 * Features with multiple positions on the sequence, e.g. structural domains or PRINTS matches are represented by
 * multiple range objects attached to the Feature. </p>
 * <p/>
 * A Range may have a &quot;fuzzy&quot; start/end, e.g. 4..5 or &lt;5. </p>
 * <p/>
 * The table below shows the representation of both exact and &quot;fuzzy&quot; features: </p>
 * <p/>
 * attribute 4-4 4-10 4..6-10 &lt;5-&gt;10 ?-10 undetermined </p>
 * <p/>
 * fromIntervalStart 4 4 4 5 null null </p>
 * <p/>
 * fromIntervalEnd 4 4 6 5 null null </p>
 * <p/>
 * toIntervalStart 4 10 10 10 10 null </p>
 * <p/>
 * toIntervalEnd 4 10 10 10 10 null </p>
 * <p/>
 * startFuzzyType exact exact interval lessThan undet. null </p>
 * <p/>
 * endFuzzyType exact exact exact greaterThan exact null </p>
 * <p/>
 * undetermined false false false false false true </p>
 *
 * @author Chris Lewington, hhe
 * @version $Id$
 */
@Entity
@Table( name = "ia_range" )
public class Range extends BasicObjectImpl {

    //------------ attributes ------------------------------------

    /**
     * Sequence size limit for this class. Set to a default value.
     */
    private static int ourMaxSeqSize = 100;

    /**
     * TODO Comments
     */
    private int fromIntervalStart;

    /**
     * TODO Comments
     */
    private int fromIntervalEnd;

    /**
     * TODO Comments
     */
    private int toIntervalStart;

    /**
     * TODO Comments
     */
    private int toIntervalEnd;

    /**
     * <p/>
     * Contains the first 100 amino acids of the sequence in the Range. This is purely used for data consistency checks.
     * In case of sequence updates the new position can be determined by sequence alignment. </p>
     */
    //NOTE: We will assume a maximum size of 100 characters for this
    private String sequence;

    /**
     * TODO Comments This is really a boolean but we need to use a character for it because Oracle does not support
     * boolean types NB JDBC spec has no JDBC type for char, only Strings!
     */
    //private String undetermined = "N";
    private boolean undetermined = false;

    /**
     * <p/>
     * True if the Range describes a link between two positions in the sequence, e.g. a sulfate bridge. </p>
     * <p/>
     * False otherwise. </p>
     * <p/>
     * This is really a boolena but we need to use a character for it because Oracle does not support boolean types NB
     * JDBC spec has no JDBC type for char, only Strings!
     */
    private boolean linked = true;

    //------------------- cvObjects --------------------------------------

    /**
     * TODO Comments
     */
    private CvFuzzyType fromCvFuzzyType;
    /**
     * TODO Comments
     */
    private CvFuzzyType toCvFuzzyType;

    private Feature feature;

    /**
     * Sets the bean's from range
     */
    public static String getRange( String type, int start, int end ) {
        // The rage to return.
        String result;

        // The value for display (fuzzy).
        String dispLabel = CvFuzzyType.Converter.getInstance().getDisplayValue( type );

        // Single type?
        if ( CvFuzzyType.isSingleType( type ) ) {
            result = dispLabel;
        }
        // Range type?
        else if ( type.equals( CvFuzzyType.RANGE ) ) {
            result = start + dispLabel + end;
        }
        // No fuzzy type?
        else if ( type.length() == 0 ) {
            result = dispLabel + start;
        } else {
            // >, <, c or n
            result = dispLabel + start;
        }
        return result;
    }

    /**
     * @return returns the maximum sequence size.
     */
    public static int getMaxSequenceSize() {
        return ourMaxSeqSize;
    }

    /**
     * Sets the maximum sequence size. <b>This method is only used by the unit tester for this class</b>.
     *
     * @param max the new sequence value.
     */
    public static void setMaxSequenceSize( int max ) {
        ourMaxSeqSize = max;
    }

    //--------------------------- constructors --------------------------------------

    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public Range() {
        super();
    }

    /**
     * This is a convenient constructor to create Range with from and end values.
     *
     * @param owner     the owner of this range.
     * @param fromStart The starting point of the 'from' interval for the Range. The 'from' end value is set to this
     *                  value.
     * @param toStart   The starting point of the 'to' interval of the Range. The 'to' end value is set to this value.
     * @param seq       The sequence - maximum of 100 characters (null allowed)
     */
    public Range( Institution owner, int fromStart, int toStart, String seq ) {
        this( owner, fromStart, fromStart, toStart, toStart, seq );
    }

    /**
     * Sets up a valid Range instance. Range is dependent on the feature and hence it cannot exist on its own. Currently
     * a valid Range must have at least the following defined:
     *
     * @param owner     the owner of this range.
     * @param fromStart The starting point of the 'from' interval for the Range.
     * @param fromEnd   The end point of the 'from' interval.
     * @param toStart   The starting point of the 'to' interval of the Range
     * @param toEnd     The end point of the 'to' interval
     * @param seq       The sequence - maximum of 100 characters (null allowed)
     *                  <p/>
     *                  <p/>
     *                  NB ASSUMPTION: The progression of intervals is always assumed to be from 'left to right' along
     *                  the number line when defining intervals. Thus '-6 to -4', '5 to 20' and '-7 to 15' are  all
     *                  <b>valid</b> single intervals, but '-3 to -8', '12 to 1' and  '5 to -7' are <b>not</b>. </p>
     */
    public Range( Institution owner, int fromStart, int fromEnd, int toStart, int toEnd, String seq ) {
        //NB negative intervals are allowed!! This needs more sophisticated checking..
        super( owner );

        if ( fromEnd < fromStart ) {
            throw new IllegalArgumentException( "End of 'from' interval must be bigger than the start!" );
        }
        if ( toEnd < toStart ) {
            throw new IllegalArgumentException( "End of 'to' interval must be bigger than the start!" );
        }
        if ( fromEnd > toStart ) {
            throw new IllegalArgumentException( "The 'from' and 'to' intervals cannot overlap!" );
        }
        if ( fromStart > toEnd ) {
            throw new IllegalArgumentException( "The 'from' interval starts beyond the 'to' interval!" );
        }
        if ( fromStart > toStart ) {
            throw new IllegalArgumentException( "The 'from' interval cannot begin during the 'to' interval!" );
        }

        this.fromIntervalStart = fromStart;
        this.fromIntervalEnd = fromEnd;
        this.toIntervalStart = toStart;
        this.toIntervalEnd = toEnd;

        this.sequence = prepareSequence( seq );
    }

    //------------------------- public methods --------------------------------------


    public int getFromIntervalStart() {
        return fromIntervalStart;
    }

    /**
     * Sets the starting from interval. Please call {@link #setSequence(String)} after calling this method as the
     * sequence to set is determined by this value.
     *
     * @param posFrom
     *
     * @see #setFromCvFuzzyType(CvFuzzyType)
     */
    public void setFromIntervalStart( int posFrom ) {
        fromIntervalStart = posFrom;
    }

    public int getFromIntervalEnd() {
        return fromIntervalEnd;
    }

    public void setFromIntervalEnd( int posTo ) {
        fromIntervalEnd = posTo;
    }

    public int getToIntervalStart() {
        return toIntervalStart;
    }

    public void setToIntervalStart( int posFrom ) {
        toIntervalStart = posFrom;
    }

    public int getToIntervalEnd() {
        return toIntervalEnd;
    }

    public void setToIntervalEnd( int posTo ) {
        toIntervalEnd = posTo;
    }
    /*
public boolean isUndetermined() {
       return charToBoolean( undetermined );
   }

   /**
    * Undetermined is true only both fuzzy types are of UNDETERMINED type. For all other instances, it is false.
    *
   public void setUndetermined() {
       // Set only when we have fuzzy types.
       if ( ( fromCvFuzzyType != null ) && ( toCvFuzzyType != null ) ) {
           undetermined = booleanToChar( fromCvFuzzyType.getShortLabel().equals(
                   CvFuzzyType.UNDETERMINED ) && toCvFuzzyType.getShortLabel().equals(
                   CvFuzzyType.UNDETERMINED ) );
       } else {
           undetermined = booleanToChar( false );
       }
   }

   public boolean isLinked() {
       return charToBoolean( link );
   }

   public void setLink( boolean isLinked ) {
       link = booleanToChar( isLinked );
   } */


    @Type( type = "yes_no" )
    public boolean isUndetermined() {
        return undetermined;
    }

    /**
     * Undetermined is true only both fuzzy types are of UNDETERMINED type. For all other instances, it is false.
     */
    public void setUndetermined() {
        // Set only when we have fuzzy types.
        if ( ( fromCvFuzzyType != null ) && ( toCvFuzzyType != null ) ) {
            undetermined = fromCvFuzzyType.getShortLabel().equals(
                    CvFuzzyType.UNDETERMINED ) && toCvFuzzyType.getShortLabel().equals(
                    CvFuzzyType.UNDETERMINED );
        } else {
            undetermined = false;
        }
    }

    public void setUndetermined( boolean undetermined ) {
        this.undetermined = undetermined;
    }

    @Column( name = "link" )
    @Type( type = "yes_no" )
    public boolean isLinked() {
        return linked;
    }

    public void setLinked( boolean linked ) {
        this.linked = linked;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "fromfuzzytype_ac" )
    public CvFuzzyType getFromCvFuzzyType() {
        return fromCvFuzzyType;
    }

    /**
     * Sets the from fuzzy type. The user must ensure that {@link #setSequence(String)} method is called <b>after</b>
     * calling this method because the sequence to set is determined by this type.
     *
     * @param type the fuzzy type to set.
     */
    public void setFromCvFuzzyType( CvFuzzyType type ) {
        fromCvFuzzyType = type;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "tofuzzytype_ac" )
    public CvFuzzyType getToCvFuzzyType() {
        return toCvFuzzyType;
    }

    public void setToCvFuzzyType( CvFuzzyType type ) {
        toCvFuzzyType = type;
    }

    /**
     * Sets the sequence using a raw string. The internal sequence is set using the from fuzzy type and from start
     * values. <b>Important</b>This method must be called after any changes either to 'from' fuzzy type ({@link
     * #setFromCvFuzzyType(CvFuzzyType)}) or from start value ({@link #setFromIntervalStart(int)}).
     * <p/>
     * </p> The logic in setting the sequence as follows (x refers to max seq size): 1. For C-terminals, the sequence is
     * set to last x bytes. 2. For N-terminals and undetermined types, the first x bytes are set. 3. Fo all other types,
     * x bytes starting from from interval start is used.
     *
     * @param sequence the raw sequence (generally this string is the full sequence).
     */
    public void setSequence( String sequence ) {
        this.sequence = sequence;
    }

    public String prepareSequence( String sequence ) {
        // Get the sequence from start if there is no fuzzy type.
        if ( fromCvFuzzyType == null ) {
            setSequenceIntern( getSequenceStartingFrom( sequence, fromIntervalStart ) );
            return this.sequence;
        }
        // Truncate according to type.
        if ( fromCvFuzzyType.isCTerminal() ) {
            setSequenceIntern( getLastSequence( sequence ) );
        } else if ( fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined() ) {
            setSequenceIntern( getFirstSequence( sequence ) );
        } else {
            setSequenceIntern( getSequenceStartingFrom( sequence, fromIntervalStart ) );
        }

        return this.sequence;
    }

    public String getSequence() {
        return this.sequence;
    }

    /**
     * Equality for Ranges is currently based on equality for <code>Modification</code>, position from and position to
     * (ints).
     *
     * @param o The object to check
     *
     * @return true if the parameter equlas this object, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Range ) ) {
            return false;
        }

        final Range range = ( Range ) o;

        //check the intervals are the same
        if ( fromIntervalStart != range.fromIntervalStart ) {
            return false;
        }
        if ( fromIntervalEnd != range.fromIntervalEnd ) {
            return false;
        }
        if ( toIntervalStart != range.toIntervalStart ) {
            return false;
        }
        if ( toIntervalEnd != range.toIntervalEnd ) {
            return false;
        }

        //check the booleans
        if ( linked != range.isLinked() ) {
            return false;
        }
        if ( undetermined != range.undetermined ) {
            return false;
        }

        //check the from fuzzy types
        if ( fromCvFuzzyType != null ) {
            if ( !fromCvFuzzyType.equals( range.fromCvFuzzyType ) ) {
                return false;
            }
        } else {
            if ( range.fromCvFuzzyType != null ) {
                return false;
            }
        }

        //check the to fuzzy types
        if ( toCvFuzzyType != null ) {
            if ( !toCvFuzzyType.equals( range.toCvFuzzyType ) ) {
                return false;
            }
        } else {
            if ( range.toCvFuzzyType != null ) {
                return false;
            }
        }

        //check the sequence
        if ( sequence != null ) {
            if ( !sequence.equals( range.sequence ) ) {
                return false;
            }
        } else {
            if ( range.sequence != null ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = fromIntervalStart;

        result = 29 * result + fromIntervalEnd;
        result = 29 * result + toIntervalStart;
        result = 29 * result + toIntervalEnd;

        //add in the sequence hashcode
        if ( sequence != null ) {
            result = 29 * result + sequence.hashCode();
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        // Saves the from type as a short label
        String fromType = "";

        // Set the fuzzy type first as they are used in set range methods.
        if ( fromCvFuzzyType != null ) {
            fromType = fromCvFuzzyType.getShortLabel();
        }
        sb.append( getRange( fromType, fromIntervalStart, fromIntervalEnd ) );

        sb.append( "-" );

        // Saves the to type as a short label
        String toType = "";

        if ( toCvFuzzyType != null ) {
            toType = toCvFuzzyType.getShortLabel();
        }
        sb.append( getRange( toType, toIntervalStart, toIntervalEnd ) );
        return sb.toString();
    }


    /**
     * Returns a cloned version of the current object.
     *
     * @return a cloned version of the current Range. The fuzzy types are not cloned (shared).
     *
     * @throws CloneNotSupportedException for errors in cloning this object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Range copy = ( Range ) super.clone();
        // Reset the parent ac.
        // copy.featureAc = null;
        return copy;
    }

    //---------------- private utility methods -----------------------

    /**
     * A helper method to return the last sequence.
     *
     * @param sequence the full sequence
     *
     * @return the last {@link #getMaxSequenceSize()} characters of the sequence; could be null if <code>sequence</code>
     *         is empty or null.
     */
    private static String getLastSequence( String sequence ) {
        return getSequence( sequence, 0, false );
    }

    /**
     * A helper method to return the first sequence.
     *
     * @param sequence the full sequence
     *
     * @return the first {@link #getMaxSequenceSize()} characters of the sequence; could be null if
     *         <code>sequence</code> is empty or null.
     */
    private static String getFirstSequence( String sequence ) {
        return getSequence( sequence, 0, true );
    }

    /**
     * A helper method to return the sequence starting at given index.
     *
     * @param sequence the full sequence
     * @param start    the starting number for the sequence to return.
     *
     * @return the sequence starting at <code>start</code> with max of {@link #getMaxSequenceSize()} characters of the
     *         sequence; could be null if <code>sequence</code> is empty or null.
     */
    private static String getSequenceStartingFrom( String sequence, int start ) {
        return getSequence( sequence, start, true );
    }

    @ManyToOne
    @JoinColumn( name = "feature_ac" )
    public Feature getFeature() {
        return feature;
    }

    public void setFeature( Feature feature ) {
        this.feature = feature;
    }

    /**
     * Constructs a sequence.
     *
     * @param sequence the full sequence
     * @param start    the starting number for the sequence to return.
     * @param first    true if we want to start the sequence from 0.
     *
     * @return the sequence constructed using given parameters. This sequence contains a maximum of {@link
     *         #getMaxSequenceSize()} characters.
     */
    private static String getSequence( String sequence, int start, boolean first ) {
        String seq = null;

        if ( ( sequence == null ) || sequence.length() == 0 || ( sequence.length() < start ) ) {
            return seq;
        }

        if ( sequence.length() <= getMaxSequenceSize() ) {
            if ( start == 0 ) {
                seq = sequence;
            } else {
                seq = sequence.substring( Math.max( 0, start - 1 ) ); // we make sure that we don't request index < 0.
            }
            return seq;
        }

        // full sequence is greater than the required length.
        if ( first ) {
            if ( sequence.length() >= start + getMaxSequenceSize() ) {
                // The given sequence is large enough to go upto max size
                seq = sequence.substring( start, start + getMaxSequenceSize() );
            } else {
                // Exceeds the current sequence length
                seq = sequence.substring( Math.max( 0, start - 1 ) ); // we make sure that we don't request index < 0.
            }
        } else {
            // returning the last 'size' characters
            seq = sequence.substring( sequence.length() - getMaxSequenceSize() );
        }

        return seq;
    }

    private void setSequenceIntern( String seq ) {
        //don't allow default empty String to be replaced by null. Check size also
        //to avoid unnecessary DB call for a seq that is too big...
        if ( seq != null ) {
            if ( seq.length() > getMaxSequenceSize() ) {
                throw new IllegalArgumentException( "Sequence too big! Max allowed: " + getMaxSequenceSize() );
            }
        }
        this.sequence = seq;
    }

    /**
     * Simple converter.
     *
     * @param val boolean
     *
     * @return "Y" if the boolean is true, "N" otherwise
     */
    private String booleanToChar( boolean val ) {
        if ( val ) {
            return "Y";
        }
        return "N";
    }

    /**
     * Simple converter
     *
     * @param st The String to convert
     *
     * @return true if the String is "Y", false otherwise
     */
    private boolean charToBoolean( String st ) {
        return !st.equals( "N" );
    }
}




