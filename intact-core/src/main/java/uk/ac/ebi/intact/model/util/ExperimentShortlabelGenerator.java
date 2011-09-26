/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that generated experiment's shortlabel suffix according to a known context.
 * <br>
 * The shortlabel's suffix is generated on the basis of an author's name, a year of publication and a pubmed ID.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Jul-2005</pre>
 */
public class ExperimentShortlabelGenerator {

    ////////////////////////
    // Inner class

    public static class SuffixBean {

        public static final int INITIAL_COUNT = 1;


        private final String character;
        private int count;

        public SuffixBean( String character ) {
            this.character = character;
            this.count = INITIAL_COUNT;
        }

        public String getCharacter() {
            return character;
        }

        public int getNextCount() {
            return count++;
        }
    }

    public static class SuffixKey {

        ////////////////////////
        // Constants
        public static final String NO_CHAR = "";
        public static final String FIRST_CHAR = "a";
        public static final String LAST_CHAR = "z";

        ////////////////////////
        // Instance variables
        private final String author;
        private final int year;

        private String lastCharUsed = FIRST_CHAR;


        ////////////////////////
        // Constructors
        public SuffixKey( String author, int year ) {

            if ( author == null ) {
                throw new IllegalArgumentException( "author can not be null." );
            }

            this.author = author;
            this.year = year;

            // initialise the char suffix
            this.lastCharUsed = NO_CHAR;
        }


        //////////////
        // Getters
        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }

        public String getNextChar() {
            String nextChar = lastCharUsed;
            nextChar();

            return nextChar;
        }


        //////////////////////
        // Utility methods
        private String nextChar() {

            if ( NO_CHAR.equals( lastCharUsed ) ) {

                lastCharUsed = FIRST_CHAR;

            } else {
                // 'a' or higher
                char ch = lastCharUsed.charAt( 0 );
                ch++;

                // we can't go over 'z'
                if ( ch > LAST_CHAR.charAt( 0 ) ) {
                    throw new IllegalArgumentException( "Can't go over 'z', please gind an other way to do it." );
                }

                lastCharUsed = "" + ch;
            }

            return lastCharUsed;
        }


        ///////////////////////////////////////
        // Equality for Collection managment
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof SuffixKey ) ) {
                return false;
            }

            final SuffixKey suffixKey = (SuffixKey) o;

            if ( year != suffixKey.year ) {
                return false;
            }
            if ( author != null ? !author.equals( suffixKey.author ) : suffixKey.author != null ) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result;
            result = ( author != null ? author.hashCode() : 0 );
            result = 29 * result + year;
            return result;
        }
    }


    ///////////////////////
    // Instance variable

    private Map<SuffixKey,Map<String,SuffixBean>> author2pubmed = new HashMap<SuffixKey,Map<String,SuffixBean>>();


    ////////////////////////
    // Constructor

    public ExperimentShortlabelGenerator() {
    }

    ////////////////////////
    // Public method

    /**
     * That method generate a suffix for a combination of author/year/pubmedID. <br> The syntax of the suffix is:
     * [optional char]-[integer going from 1 to n] <br> if the same combination of author/year/pubmedID is given many
     * times, the integer is incremented. <br> if the combination author/year is given with different pubmedID, then we
     * add a character
     * <p/>
     * <pre>
     * example:
     *           jonhdoe / 2005 / 12345678   ---->    -1
     *           jonhdoe / 2005 / 12345678   ---->    -2
     *           jonhdoe / 2005 / 12345678   ---->    -3
     *           jonhdoe / 2005 / 12345678   ---->    -4
     * <p/>
     *           jonhdoe / 2005 / 87654321   ---->    a-1
     *           jonhdoe / 2005 / 87654321   ---->    a-2
     *           jonhdoe / 2005 / 87654321   ---->    a-3
     * <p/>
     *           jonhdoe / 2005 / 11111111   ---->    c-1
     *           jonhdoe / 2005 / 11111111   ---->    c-2
     * <p/>
     *           jonhdoe / 1999 / 99999999   ---->    -1
     *           jonhdoe / 1999 / 99999999   ---->    -2
     * </pre>
     *
     * @param author   the last name of the fisrt author of the publication
     * @param year     the year of publication
     * @param pubmedId the pubmed ID of the publication
     *
     * @return the suffix of the IntAct experiment holding those data.
     */
    public String getSuffix( String author, int year, String pubmedId ) {

        String suffix = null;

        SuffixKey key = new SuffixKey( author, year );
        SuffixBean suffixBean = null;

        // check if that key exists already and keep it's reference as it holds the next char to use
        boolean keyExists = false;

        for (SuffixKey _key : author2pubmed.keySet()) {
            if (_key.equals(key)) {
                // found it ... keep its reference
                key = _key;
                keyExists = true;
            }
        }

        if ( keyExists ) {

            // then check if it has the pubmed if
            Map<String,SuffixBean> pubmed2suffix = author2pubmed.get( key );

            if ( pubmed2suffix.containsKey( pubmedId ) ) {

                // if it has already that pubmedId, then the suffix is already initialized.
                suffixBean = pubmed2suffix.get( pubmedId );

            } else {

                // initialize the suffix
                suffixBean = new SuffixBean( key.getNextChar() );

                // update the map
                pubmed2suffix.put( pubmedId, suffixBean );
            }

        } else {

            // create the Map and initialize the suffix
            suffixBean = new SuffixBean( key.getNextChar() );

            Map<String,SuffixBean> newPubmed2suffix = new HashMap<String,SuffixBean>( 2 );
            newPubmed2suffix.put( pubmedId, suffixBean );

            author2pubmed.put( key, newPubmed2suffix );
        }

        // Generate the suffix
        suffix = suffixBean.getCharacter() + "-" + suffixBean.getNextCount();

        return suffix;
    }

    public void clearGeneratedSuffixes() {
        author2pubmed.clear();
    }
}