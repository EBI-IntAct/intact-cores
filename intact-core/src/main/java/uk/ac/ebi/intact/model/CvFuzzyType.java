package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * <p/>
 * Represents types of &quot;fuzzy&quot; Range start and end positions. Examples are &quot;less than&quot;,
 * &quot;greater than&quot;, &quot;undetermined&quot;. </p>
 *
 * @author Chris Lewington $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvFuzzyType" )
@EditorTopic
public class CvFuzzyType extends CvObject implements Editable {

    /**
     * The constant name for <.
     */
    public static final String LESS_THAN = "less-than";
    public static final String LESS_THAN_MI_REF = "MI:0337";

    /**
     * The constant for >.
     */
    public static final String GREATER_THAN = "greater-than";
    public static final String GREATER_THAN_MI_REF = "MI:0336";

    /**
     * The constant for range.
     */
    public static final String RANGE = "range";
    public static final String RANGE_MI_REF = "MI:0338";

    /**
     * The constant for undetermined.
     */
    public static final String UNDETERMINED = "undetermined";
    public static final String UNDETERMINED_MI_REF = "MI:0339";

    /**
     * The constant for c-terminal.
     */
    public static final String C_TERMINAL = "c-terminal";
    public static final String C_TERMINAL_MI_REF = "MI:0334";

    /**
     * The constant for n-terminal.
     */
    public static final String N_TERMINAL = "n-terminal";
    public static final String N_TERMINAL_MI_REF = "MI:0340";

    // -- Start of Inner class ------------------------------------------------

    /**
     * This class is responsible for converting to/from CvFuzzyType to display formats.
     *
     * @author Sugath Mudali (smudali@ebi.ac.uk)
     * @version $Id$
     */
    public static class Converter {

        // Class Data

        /**
         * Only instance of this class.
         */
        private static Converter ourInstance = new Converter();

        /**
         * Maps: CvFuzzy Labels -> Display labels.
         */
        private static Map<String, String> ourNormalMap = new HashMap<String, String>();

        static {
            ourNormalMap.put( LESS_THAN, "<" );
            ourNormalMap.put( GREATER_THAN, ">" );
            ourNormalMap.put( UNDETERMINED, "?" );
            ourNormalMap.put( C_TERMINAL, "c" );
            ourNormalMap.put( N_TERMINAL, "n" );
            ourNormalMap.put( RANGE, ".." );
        }

        /**
         * @return the only instance of this class.
         */
        public static Converter getInstance() {
            return ourInstance;
        }

        /**
         * Returns the display value for given short label value.
         *
         * @param shortLabel the short label to return the corresponding display value.
         *
         * @return the display value for given short label or an empty string if there is no mapping for
         *         <code>label</code> (as with the case of no fuzzy type).
         */
        @Transient
        public String getDisplayValue( String shortLabel ) {
            if ( ourNormalMap.containsKey( shortLabel ) ) {
                return ourNormalMap.get( shortLabel );
            }
            return "";
        }

        /**
         * Returns the corresponding CvFuzzy short label for given display value.
         *
         * @param value the display value.
         *
         * @return the fuzzy label if there is a matching found <code>matcher</code> object or an empty string to denote
         *         for no fuzzy or unknown type.
         */
        @Transient
        public String getFuzzyShortLabel( String value ) {
            if ( ourNormalMap.containsValue( value ) ) {
                return getKey( value );
            }
            return null;
        }

        /**
         * Returns the corresponding CvFuzzy short label using given matcher object.
         *
         * @param matcher the Matcher object to analyse
         *
         * @return the fuzzy label if there is a matching found <code>matcher</code> object or else an empty string (for
         *         a normal range).
         */
        @Transient
        public String getFuzzyShortLabel( Matcher matcher ) {
            if ( !matcher.matches() ) {
                throw new IllegalArgumentException( "No match found" );
            }
            // The short label to return.
            String shortLabel = "";

            // for ? or c or n types
            if ( matcher.group( 1 ) != null ) {
                shortLabel = getKey( matcher.group( 1 ) );
            } else if ( matcher.group( 4 ) != null ) {
                if ( matcher.group( 2 ) != null ) {
                    // For < or > types
                    shortLabel = getKey( matcher.group( 2 ) );
                }
            } else {
                // Range type
                shortLabel = CvFuzzyType.RANGE;
            }
            return shortLabel;
        }

        // Helper methods

        /**
         * Returns a key for given value.
         *
         * @param value the value to search for the key.
         *
         * @return the corresponding key for given <code>value</code>.
         */
        private static String getKey( String value ) {
            for ( Map.Entry<String, String> entry : ourNormalMap.entrySet() ) {
                if ( entry.getValue().equals( value ) ) {
                    return entry.getKey();
                }
            }
//            assert false;

            // Should never happen.
            throw new IllegalArgumentException( "Value(" + value + ") cannot be found in the map." );
//            return null;
        }
    }

    // --- End of Inner class -------------------------------------------------

    /**
     * @param type the CvFuzzy label to compare
     *
     * @return true if <code>type</code> is of Untermined or C or N terminal types. False is returned for all other
     *         instances.
     */
    @Transient
    public static boolean isSingleType( String type ) {
        return type.equals( UNDETERMINED ) || type.equals( C_TERMINAL ) || type.equals( N_TERMINAL );
    }

    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public CvFuzzyType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvFuzzyType instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvFuzzyType
     * @param owner      The Institution which owns this CvFuzzyType
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvFuzzyType( Institution owner, String shortLabel ) {
        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

    /**
     * @return true if this current type is of c_terminal type.
     */
    @Transient
    public final boolean isCTerminal() {
        return hasMiIdentity( C_TERMINAL_MI_REF ) || getShortLabel().equals( C_TERMINAL );
    }

    /**
     * @return true if this current type is of n_terminal type.
     */
    @Transient
    public final boolean isNTerminal() {
        return hasMiIdentity( N_TERMINAL_MI_REF ) || getShortLabel().equals( N_TERMINAL );
    }

    /**
     * @return true if this current type is of undetermined type.
     */
    @Transient
    public final boolean isUndetermined() {
        return hasMiIdentity( UNDETERMINED_MI_REF ) || getShortLabel().equals( UNDETERMINED );
    }

    @Transient
    private boolean hasMiIdentity( String miRef ) {
        return CvObjectUtils.hasIdentity( this, miRef );
    }
}
