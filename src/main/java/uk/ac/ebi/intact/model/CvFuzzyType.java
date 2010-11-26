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
public class CvFuzzyType extends CvDagObject implements Editable {

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
    public static final String RAGGED_N_TERMINUS = "ragged n-terminus";
    public static final String RAGGED_N_TERMINUS_MI_REF = "MI:0341";

    /**
     * The constant for certain.
     */
    public static final String CERTAIN = "certain";
    public static final String CERTAIN_MI_REF = "MI:0335";

    /**
     * The constant for n-terminal region.
     */
    public static final String N_TERMINAL_REGION = "n-terminal region"; // TODO update with n-terminal range
    public static final String N_TERMINAL_REGION_MI_REF = "MI:1040";

    /**
     * The constant for c-terminal region.
     */
    public static final String C_TERMINAL_REGION = "c-terminal region"; // TODO update with c-terminal range
    public static final String C_TERMINAL_REGION_MI_REF = "MI:1039";

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
            ourNormalMap.put( C_TERMINAL_REGION, "c" );
            ourNormalMap.put( N_TERMINAL_REGION, "n" );
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
                throw new IllegalArgumentException( "No match found using pattern: "+matcher.pattern() );
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
     * @return true if <code>type</code> is of Untermined or C or N terminal region types. False is returned for all other
     *         instances.
     */
    @Transient
    public static boolean isSingleType( String type ) {
        return type.equals( UNDETERMINED ) || type.equals( C_TERMINAL_REGION ) || type.equals( N_TERMINAL_REGION );
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

        if (this.getIdentifier() != null){
           return hasMiIdentity( C_TERMINAL_MI_REF );
        }
        else{
            return C_TERMINAL.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is of c_terminal region type.
     */
    @Transient
    public final boolean isCTerminalRegion() {

        if (this.getIdentifier() != null){
           return hasMiIdentity( C_TERMINAL_REGION_MI_REF );
        }
        else{
            return C_TERMINAL_REGION.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is of n_terminal type.
     */
    @Transient
    public final boolean isNTerminal() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( N_TERMINAL_MI_REF );
        }
        else{
            return N_TERMINAL.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is of n_terminal region type.
     */
    @Transient
    public final boolean isNTerminalRegion() {

        if (this.getIdentifier() != null){
           return hasMiIdentity( N_TERMINAL_REGION_MI_REF );
        }
        else{
            return N_TERMINAL_REGION.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is of ragged_n_terminal type.
     */
    @Transient
    public final boolean isRaggedNTerminus() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( RAGGED_N_TERMINUS_MI_REF );
        }
        else{
            return RAGGED_N_TERMINUS.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is of undetermined type.
     */
    @Transient
    public final boolean isUndetermined() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( UNDETERMINED_MI_REF );
        }
        else{
            return UNDETERMINED.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is of less-than type.
     */
    @Transient
    public final boolean isLessThan() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( LESS_THAN_MI_REF );
        }
        else{
            return LESS_THAN.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is greater-than type.
     */
    @Transient
    public final boolean isGreaterThan() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( GREATER_THAN_MI_REF );
        }
        else{
            return GREATER_THAN.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is range type.
     */
    @Transient
    public final boolean isRange() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( RANGE_MI_REF );
        }
        else{
            return RANGE.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    /**
     * @return true if this current type is certain type.
     */
    @Transient
    public final boolean isCertain() {
        if (this.getIdentifier() != null){
           return hasMiIdentity( CERTAIN_MI_REF );
        }
        else{
            return CERTAIN.equalsIgnoreCase( this.getShortLabel() );
        }
    }

    @Transient
    private boolean hasMiIdentity( String miRef ) {
        if (getIdentifier() != null) {
            return getIdentifier().equals(miRef);
        } else {
            return CvObjectUtils.hasIdentity( this, miRef );
        }
    }
}
