/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.Interaction;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionShortLabelGenerator {

    private static final String INTERACTION_SEPARATOR = "-";

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog( InteractionShortLabelGenerator.class );

    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database.
     * <p/>
     * Create an IntAct shortlabel for a given interaction (ie. a set of [protein, role] ).
     * <p/>
     * - Stategy -
     * <p/>
     * Protein's role can be either: bait, prey or neutral the interaction shortlabel has the following pattern: X-Y-Z
     * with a limit in length of AnnotatedObject.MAX_SHORT_LABEL_LENGTH caracters.
     * <p/>
     * X is (in order of preference): 1. the gene name of the bait protein 2. the gene name of a prey protein (the first
     * one in alphabetical order) 3. the gene name of a neutral protein (the first one in alphabetical order)
     * <p/>
     * <p/>
     * <p/>
     * -- REMOVE NEXT SECTION - not done in this method
     * <p/>
     * Y is : 1. the gene name of a prey protein (the first one in alphabetical order or second if the first has been
     * used already) 2. the gene name of a neutral protein (the first one in alphabetical order or second if the first
     * has been used already)
     * Z is : an Integer that gives the number of occurence in intact.
     * <p/>
     * eg. 1. bait(baaa), prey(paaa, pbbb, pccc), neutral(naaa) should gives us: baaa-paaa-1
     * <p/>
     * 2. bait(baaa), prey(), neutral(naaa) should gives us: baaa-naaa-1
     * <p/>
     * 3. bait(), prey(paaa, pbbb, pccc), neutral(naaa) should gives us: paaa-pbbb-1
     * <p/>
     * 4. bait(), prey(paaa), neutral(naaa) should gives us: paaa-naaa-1
     *
     * @param interaction
     * @throws uk.ac.ebi.intact.core.IntactException
     *
     * @since 1.6
     */
    public static String createCandidateShortLabel( final Interaction interaction ) {
        List<Component> components = new ArrayList<Component>(interaction.getComponents());

        Collections.sort(components, new ComponentRoleComparator());

        if (components.size() == 1) {
            return getLabel(components.get(0));
        } else if (components.size() > 1) {
            String shortLabel1 = getLabel(components.get(0));
            String shortLabel2 = getLabel(components.get(1));

            return createCandidateShortLabel( shortLabel1, shortLabel2 );
        }

        return null;
    }

    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database
     *
     * @param completeLabel label
     * @return the short label
     */
    protected static String createCandidateShortLabel( String completeLabel ) throws IllegalLabelFormatException {
        InteractionShortLabel label = new InteractionShortLabel( completeLabel );
        return label.getCompleteLabel();
    }

    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database
     *
     * @param baitShortLabel bait gene name
     * @param preyShortLabel prey gene name
     * @return the short label
     */
    protected static String createCandidateShortLabel( String baitShortLabel, String preyShortLabel ) {
        return createCandidateShortLabel( baitShortLabel, preyShortLabel, null );
    }

    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database
     *
     * @param baitShortLabel bait gene name
     * @param preyShortLabel prey gene name
     * @param suffix         e.g. "1"
     * @return the short label
     */
    protected static String createCandidateShortLabel( String baitShortLabel, String preyShortLabel, Integer suffix ) {
        InteractionShortLabel label = new InteractionShortLabel( baitShortLabel, preyShortLabel, suffix );
        return label.getCompleteLabel();
    }

    /**
     * Search for the first string (in alphabetical order).
     *
     * @param geneNames   a collection of non ordered gene names.
     * @param failOnError if <code>true</code> throw an IntactException when no gene name is found, if
     *                    <code>false</code> sends back <code>null</code>.
     * @return either a String or null according to the failOnError parameter.
     * @throws uk.ac.ebi.intact.core.IntactException
     *          thrown when the failOnError parameter is true and no string can be returned.
     */
    private static String getLabelFromCollection( Collection<String> geneNames, boolean failOnError ) throws IntactException {
        String shortlabel = null;

        if ( geneNames == null ) {
            throw new IllegalArgumentException( "You must give a non null collection of gene name." );
        }

        switch ( geneNames.size() ) {
            case 0:
                // ERROR, we should have a bait.
                // This should have been detected during step 1 or 2.
                if ( failOnError ) {
                    throw new IntactException( "Could not find gene name for that interaction." );
                }
                break;
            case 1:
                shortlabel = geneNames.iterator().next();
                break;

            default:
                // more than one ... need sorting
                String[] _geneNames = geneNames.toArray( new String[geneNames.size()] );
                Arrays.sort( _geneNames, String.CASE_INSENSITIVE_ORDER );
                shortlabel = _geneNames[0];
                break;
        }

        return shortlabel;
    }

    /**
     * Removes the suffix of a shortLabel
     *
     * @param shortLabel a shortLabel with or without suffix
     * @return the shortlabel without suffix
     */
    protected static String removeSuffix( String shortLabel ) throws IllegalLabelFormatException {
        InteractionShortLabel label = new InteractionShortLabel( shortLabel );
        return label.getCompleteLabel( false );
    }

    /**
     * Gets the next available suffix for a provided shortLabel
     *
     * @param shortLabel Can already have a suffix or not.
     * @return The next available shortLabel
     */
    public static String nextAvailableShortlabel( String shortLabel ) throws IllegalLabelFormatException {
        Integer nextSuffix = calculateNextSuffix( shortLabel );

        InteractionShortLabel label = new InteractionShortLabel( shortLabel );

        if ( nextSuffix != null ) {
            label.setSuffix( nextSuffix );
        }

        return label.getCompleteLabel();
    }

    /**
     * Calculates the next available suffix using a short label - which is
     * the highest suffix + 1
     *
     * @param shortLabel the label to use
     * @return the next available suffix.
     */
    protected static Integer calculateNextSuffix( String shortLabel ) throws IllegalLabelFormatException {
        String labelWithoutSuffix = removeSuffix( shortLabel );

        // we get all the labels with the same bait-prey combination
        List<String> shortLabelsWithSuffix = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getInteractionDao().getShortLabelsLike( labelWithoutSuffix + "%" );

        int maxSuffix = -1;

        for ( String labelWithSuffix : shortLabelsWithSuffix ) {
            InteractionShortLabel label = new InteractionShortLabel( labelWithSuffix );

            Integer suffix = label.getSuffix();

            if ( suffix != null ) {
                maxSuffix = Math.max( maxSuffix, suffix );
            } else {
                maxSuffix = 0;
            }
        }

        if ( maxSuffix == -1 ) {
            return null;
        }

        return maxSuffix + 1;
    }

    protected static class InteractionShortLabel {

        private String baitLabel;
        private String preyLabel;
        private Integer suffix;

        private Boolean selfInteraction;

        public InteractionShortLabel( String completeLabel ) throws IllegalLabelFormatException {
            parse( completeLabel );
        }

        public InteractionShortLabel( String baitLabel, String preyLabel, Integer suffix ) {
            this.baitLabel = prepareLabel( baitLabel );
            this.preyLabel = prepareLabel( preyLabel );
            this.suffix = suffix;

            if ( this.baitLabel.contains( INTERACTION_SEPARATOR ) ) {
                log.warn( "Interaction separator character '-' found in Bait label (" + baitLabel + "). Replaced by '_'" );
                this.baitLabel = baitLabel.replaceAll( INTERACTION_SEPARATOR, "_" );
            }
            if ( this.preyLabel != null && preyLabel.contains( INTERACTION_SEPARATOR ) ) {
                log.warn( "Interaction separator character '-' found in Prey label (" + preyLabel + "). Replaced by '_'" );
                this.preyLabel = preyLabel.replaceAll( INTERACTION_SEPARATOR, "_" );
            }

        }

        public String getBaitLabel() {
            return baitLabel;
        }

        public String getPreyLabel() {
            return preyLabel;
        }

        public Integer getSuffix() {
            return suffix;
        }

        public void setSuffix( Integer suffix ) {
            this.suffix = suffix;
        }

        public String getCompleteLabel() {
            return getCompleteLabel( true );
        }

        public String getCompleteLabel( boolean includeSuffix ) {
            truncateLabelsIfNecessary();

            String strPrey = "";
            if ( preyLabel != null ) { //not a self-interaction
                strPrey = INTERACTION_SEPARATOR + preyLabel;
            }

            String strSuffix = "";
            if ( includeSuffix && suffix != null ) {
                strSuffix = INTERACTION_SEPARATOR + suffix;
            }

            String complete = baitLabel + strPrey + strSuffix;
            return complete;
        }

        /**
         * Interactions follow the nomenclature baitLabel-preyLabel-suffix (where suffix is optional integer).
         * Self-interactions follow label-suffix (where suffix is optional integer).
         *
         * @param completeLabel
         */
        private void parse( String completeLabel ) throws IllegalLabelFormatException {
            if (completeLabel == null) return;

            String[] baitPrayLabels = completeLabel.split( INTERACTION_SEPARATOR );

            if ( baitPrayLabels.length > 3 ) {
                throw new IllegalLabelFormatException( "This label is not an interaction label (contain more than one '" + INTERACTION_SEPARATOR + "'): " + completeLabel );
            }

            // self interactions
            boolean isSelfInteraction = isSelfInteraction( completeLabel );

            this.baitLabel = baitPrayLabels[0];

            if ( !isSelfInteraction ) {
                this.preyLabel = baitPrayLabels[1];
            } else {
                if ( baitPrayLabels.length == 1 ) {
                    suffix = null;
                } else {
                    try {
                        suffix = Integer.valueOf( baitPrayLabels[1] );
                    } catch ( NumberFormatException e ) {
                        throw new IllegalLabelFormatException( completeLabel, "Illegal value for self-interaction label. It was expecting a number for the second element." );
                    }
                }
            }

            if ( baitPrayLabels.length == 3 ) {
                if ( isSelfInteraction ) {
                    try {
                        suffix = Integer.valueOf( baitPrayLabels[1] );
                    } catch ( NumberFormatException e ) {
                        throw new IllegalLabelFormatException( completeLabel, "Illegal value for self-interaction label. It was expecting a number for the second element." );
                    }
                } else {
                    try {
                        suffix = Integer.valueOf( baitPrayLabels[2] );
                    } catch ( NumberFormatException e ) {
                        throw new IllegalLabelFormatException( completeLabel, "Illegal value for interaction label. It was expecting a number for the third element." );
                    }
                }
            }
        }

        private boolean isSelfInteraction( String completeLabel ) {
            if ( selfInteraction != null ) {
                return selfInteraction;
            }

            String[] baitPrayLabels = completeLabel.split( INTERACTION_SEPARATOR );

            // self interactions
            boolean isSelfInteraction = false;

            if ( baitPrayLabels.length == 2 ) {
                String possibleSuffix = baitPrayLabels[1];

                boolean suffixIsAnInteger = possibleSuffix.matches( "\\d+" );
                isSelfInteraction = suffixIsAnInteger;
            } else if ( baitPrayLabels.length == 1 ) {
                isSelfInteraction = true;
            }

            return isSelfInteraction;
        }

        private void truncateLabelsIfNecessary() {
            while ( calculateLabelLength() > AnnotatedObject.RECOMMENDED_SHORT_LABEL_LEN ) {
                int baitLength = baitLabel.length();
                int preyLength = ( preyLabel == null ) ? 0 : preyLabel.length();

                if ( baitLength > preyLength ) {
                    baitLabel = baitLabel.substring( 0, baitLabel.length() - 1 ); // truncate, remove last charachter (from bait)
                } else {
                    if ( preyLabel != null ) {
                        preyLabel = preyLabel.substring( 0, preyLabel.length() - 1 ); // truncate, remove last charachter (from prey)
                    }
                }

            } // while

        }

        private int calculateLabelLength() {
            int preyLength = 0;
            if ( preyLabel != null ) { // not a self-interaction
                preyLength = preyLabel.length() + INTERACTION_SEPARATOR.length();
            }
            // NOTE: if we called here to getCompleteLabel().length() it would cause an StackTraceError
            int labelLength;

            if (baitLabel == null) {
                labelLength = preyLength;
            } else {
                labelLength = baitLabel.length() + preyLength;
            }
            if ( suffix != null )
                labelLength = labelLength + String.valueOf( suffix ).length() + INTERACTION_SEPARATOR.length();
            return labelLength;
        }


        protected static String prepareLabel( String label ) {
            if ( label == null ) return null;

            // convert bad characters ('-', ' ', '.') to '_'
            label = label.toLowerCase();
            label = label.replaceAll( INTERACTION_SEPARATOR, "_" );
            label = label.replaceAll("\\W", "_");

            return label;
        }

    }

    protected static class ComponentRoleComparator implements Comparator<Component> {
        public int compare(Component comp1, Component comp2) {
            String name1 = getLabel(comp1);
            String name2 = getLabel(comp2);

            // same role? alphabetical order
            CvExperimentalRole role1 = null;
            CvExperimentalRole role2 = null;

            if (!comp1.getExperimentalRoles().isEmpty()) {
                role1 = comp1.getExperimentalRoles().iterator().next();
            }

            if (!comp2.getExperimentalRoles().isEmpty()) {
                role2 = comp2.getExperimentalRoles().iterator().next();
            }

            if (role1 == null && role2 == null) return name1.compareTo(name2);
            if (role1 != null && role2 == null) return -1;
            if (role1 == null) return 1;

            // the order is: bait -> prey -> other

            final boolean isBait1 = ComponentUtils.isBait(comp1.getExperimentalRoles());
            final boolean isBait2 = ComponentUtils.isBait(comp2.getExperimentalRoles());
            final boolean isPrey1 = ComponentUtils.isPrey(comp1.getExperimentalRoles());
            final boolean isPrey2 = ComponentUtils.isPrey(comp2.getExperimentalRoles());

            if (isBait1 && !isBait2) return -1;
            if (isBait1 && isBait2) return name1.compareTo(name2);
            if (isBait2) return 1;

            if (isPrey1 && !isPrey2) return -1;
            if (isPrey1 && isPrey2) return name1.compareTo(name2);
            if (isPrey2) return 1;

            return name1.compareTo(name2);
        }


    }

    private static String getLabel(Component comp1) {
        String name = ProteinUtils.getGeneName(comp1.getInteractor());
        if (name == null) name = "null";
        name = name.toLowerCase();
        return name;
    }
}