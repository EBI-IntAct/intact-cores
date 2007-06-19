/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.SearchReplace;

import java.util.*;

/**
 * Util methods for interactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class InteractionUtils {

    private static final Log log = LogFactory.getLog( InteractionUtils.class );
    private static final String INTERACTION_SEPARATOR = "-";

    /**
     * Checks if the interaction is a binary interaction
     *
     * @param interaction
     *
     * @return
     */
    public static boolean isBinaryInteraction( Interaction interaction ) {
        Collection<Component> components = interaction.getComponents();
        int componentCount = components.size();

        if ( componentCount == 1 ) {
            Component component1 = components.iterator().next();
            if ( component1.getStoichiometry() == 2 ) {
                log.debug( "Binary interaction " + interaction.getAc() + ". Stoichiometry 2, each component with stoichiometry 1" );
                return true;
            }
        } else if ( componentCount == 2 ) {
            Iterator<Component> iterator1 = components.iterator();

            Component component1 = iterator1.next();
            float stochio1 = component1.getStoichiometry();
            if ( stochio1 == 1 ) {
                Component component2 = iterator1.next();
                if ( component2.getStoichiometry() == 1 ) {
                    log.debug( "Binary interaction " + interaction.getAc() + ". Stoichiometry 2, each component with stoichiometry 1" );
                    return true;
                }
            } else if ( stochio1 == 0 ) {
                Component component2 = iterator1.next();
                if ( component2.getStoichiometry() == 0 ) {
                    log.debug( "Binary interaction " + interaction.getAc() + ". Stoichiometry 0, components 2" );
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the interaction is a self interaction
     *
     * @param interaction
     *
     * @return
     */
    public static boolean isSelfInteraction( Interaction interaction ) {
        if ( isSelfBinaryInteraction( interaction ) ) {
            return true;
        }

        Collection<Component> components = interaction.getComponents();
        int componentCount = components.size();

        if ( componentCount == 1 ) {
            Component comp = components.iterator().next();

            if ( comp.getStoichiometry() >= 2 ) {
                return true;
            }
        } else if ( componentCount > 1 ) {
            String interactorAc = null;

            for ( Component comp : components ) {
                if ( interactorAc == null ) {
                    interactorAc = comp.getInteractorAc();
                }

                if ( !interactorAc.equals( comp.getInteractorAc() ) ) {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the interaction is a binary self interaction
     *
     * @param interaction
     *
     * @return
     */
    public static boolean isSelfBinaryInteraction( Interaction interaction ) {
        Collection<Component> components = interaction.getComponents();
        int componentCount = components.size();

        if ( componentCount == 1 ) {
            Component comp = components.iterator().next();

            if ( comp.getStoichiometry() == 2 ) {
                return true;
            }
        } else if ( componentCount == 2 ) {
            Iterator<Component> iter = components.iterator();
            Component comp1 = iter.next();
            Component comp2 = iter.next();

            return ( comp1.getInteractorAc().equals( comp2.getInteractorAc() ) );
        }

        return false;
    }

    /**
     * Checks if an interaction contain other interactor types than Protein
     *
     * @param interaction
     *
     * @return
     */
    public static boolean containsNonProteinInteractors( Interaction interaction ) {
        for ( Component component : interaction.getComponents() ) {
            Interactor interactor = component.getInteractor();
            if ( !( interactor instanceof ProteinImpl ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the interaction involves a single component. That a single component with a stoichiometry of at most 1.
     *
     * @param interaction
     *
     * @return
     *
     * @since 1.5
     */
    public static boolean isUnaryInteraction( Interaction interaction ) {

        int componentCount = interaction.getComponents().size();

        if ( componentCount == 1 ) {
            Component c = interaction.getComponents().iterator().next();
            if ( c.getStoichiometry() <= 1f ) {
                return true;
            }
        }

        return false;
    }


    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database.
     *
     * Create an IntAct shortlabel for a given interaction (ie. a set of [protein, role] ).
     * <p/>
     * - Stategy -
     * <p/>
     * Protein's role can be either: bait, prey or neutral the interaction shortlabel has the following patter: X-Y-Z
     * with a limit in length of AnnotatedObject.MAX_SHORT_LABEL_LENGTH caracters.
     * <p/>
     * X is (in order of preference): 1. the gene name of the bait protein 2. the gene name of a prey protein (the first
     * one in alphabetical order) 3. the gene name of a neutral protein (the first one in alphabetical order)
     * <p/>
     *
     *
     * -- REMOVE NEXT SECTION - not done in this method
     *
     * Y is : 1. the gene name of a prey protein (the first one in alphabetical order or second if the first has been
     * used already) 2. the gene name of a neutral protein (the first one in alphabetical order or second if the first
     * has been used already) Z is : an Integer that gives the number of occurence in intact.
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
     *
     * @since 1.6
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     */
    public static String createCandidateShortLabel(final Interaction interaction) {
        String shortLabel = null;

        // this collections will contain the geneNames, and will be filled according the experimental roles of
        // the components
        Collection<String> baits = new ArrayList<String>(2);
        Collection<String> preys = new ArrayList<String>(2);
        Collection<String> neutrals = new ArrayList<String>(2);

        // Search for a gene name in the set, if none exist, take the protein ID.
        for (Component component : interaction.getComponents()) {

            Interactor interactor = component.getInteractor();
            String geneName = ProteinUtils.getGeneName(interactor);

            CvExperimentalRole role = component.getCvExperimentalRole();
            String roleMi = CvObjectUtils.getPsiMiIdentityXref(role).getPrimaryId();

            if (roleMi.equals(CvExperimentalRole.PREY_PSI_REF)) {
                preys.add(geneName);
            } else if (roleMi.equals(CvExperimentalRole.BAIT_PSI_REF)) {
                baits.add(geneName);
            } else if (roleMi.equals(CvExperimentalRole.UNSPECIFIED_PSI_REF)) {
                neutrals.add(geneName);
            } else if (roleMi.equals(CvExperimentalRole.NEUTRAL_PSI_REF)) {
                neutrals.add(geneName);
            } else {
                // we should never get in here if RoleChecker plays its role !
                throw new IllegalStateException("Found role: " + role.getShortLabel() + " (" + roleMi + ") which is not supported at the moment (" +
                                                "so far only bait, prey and 'neutral component' are accepted).");
            }

        }

        // we can have either 1..n bait with 1..n prey
        // or 2..n neutral
        String baitShortlabel = null;
        String preyShortlabel = null;

        if (baits.isEmpty() && preys.isEmpty()) {
            // we have only neutral

            String[] _geneNames = neutrals.toArray(new String[neutrals.size()]);
            Arrays.sort(_geneNames, new GeneNameIgnoreCaseComparator());

            baitShortlabel = _geneNames[0];

            if (_geneNames.length > 2) {
                // if more than 2 components, get one and add the cound of others.
                preyShortlabel = (_geneNames.length - 1) + "";
            } else {
                preyShortlabel =  _geneNames[1];
            }

        } else {

            // bait-prey
            baitShortlabel = getLabelFromCollection(baits, true); // fail on error
            preyShortlabel = getLabelFromCollection(preys, false); // don't fail on error
            if (preyShortlabel == null) {
                preyShortlabel = getLabelFromCollection(neutrals, true); // fail on error
            }
        }


        String candidateShortLabel = createCandidateShortLabel(baitShortlabel, preyShortlabel);

        return candidateShortLabel;
        // that updates the experiment collection and only leaves those for which we have to create a new interaction
        //createInteractionShortLabels( interaction, experiments, baitShortlabel, preyShortlabel );

    }

    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database
     * @param baitShortLabel bait gene name
     * @param preyShortLabel prey gene name
     * @return the short label
     */
    protected static String createCandidateShortLabel(String baitShortLabel, String preyShortLabel) {
        return createCandidateShortLabel(baitShortLabel, preyShortLabel, null);
    }

    /**
     * Creates a candiate short label - not taking into account if an interaction with the same name exists in the database
     * @param baitShortLabel bait gene name
     * @param preyShortLabel prey gene name
     * @param suffix e.g. "1"
     * @return the short label
     */
    protected static String createCandidateShortLabel(String baitShortLabel, String preyShortLabel, String suffix) {
        baitShortLabel = prepareLabel(baitShortLabel);
        preyShortLabel = prepareLabel(preyShortLabel);

        if (baitShortLabel.contains(INTERACTION_SEPARATOR)) {
            throw new IllegalArgumentException("Bait label cannot contain '"+INTERACTION_SEPARATOR+"': "+baitShortLabel);
        }
        if (preyShortLabel.contains(INTERACTION_SEPARATOR)) {
            throw new IllegalArgumentException("Prey label cannot contain '"+INTERACTION_SEPARATOR+"': "+baitShortLabel);
        }
        if (suffix != null && suffix.contains(INTERACTION_SEPARATOR)) {
            throw new IllegalArgumentException("Suffix cannot contain '"+INTERACTION_SEPARATOR+"': "+baitShortLabel);
        }

        String strSuffix = "";
        if (suffix != null) {
            strSuffix = INTERACTION_SEPARATOR+suffix;
        }

        String candidateShortLabel = baitShortLabel+ INTERACTION_SEPARATOR +preyShortLabel+strSuffix;
        candidateShortLabel = truncateInteractionLabelIfNecessary(candidateShortLabel);

        return candidateShortLabel;
    }

    protected static String prepareLabel(String label) {
        // convert bad characters ('-', ' ', '.') to '_'
        label = label.toLowerCase();
        label = SearchReplace.replace( label, "-", "_" );
        label = SearchReplace.replace( label, " ", "_" );
        label = SearchReplace.replace( label, ".", "_" );

        return label;
    }

    public static String truncateInteractionLabelIfNecessary(String interactionLabel) {
        if (!interactionLabel.contains(INTERACTION_SEPARATOR)) {
            throw new IllegalArgumentException("This label is not an interaction label (does not contain '"+INTERACTION_SEPARATOR+"'): "+interactionLabel);
        }

        String[] baitPrayLabels = interactionLabel.split(INTERACTION_SEPARATOR);

        if (baitPrayLabels.length > 3) {
           throw new IllegalArgumentException("This label is not an interaction label (contain more than one '"+INTERACTION_SEPARATOR+"'): "+interactionLabel);
        }

        String baitLabel = baitPrayLabels[0];
        String preyLabel = baitPrayLabels[1];
        String suffix = null;

        if (baitPrayLabels.length == 3) {
            suffix = baitPrayLabels[2];
        }

        while (interactionLabel.length() > AnnotatedObject.MAX_SHORT_LABEL_LEN) {
            if (baitLabel.length() > preyLabel.length()) {
                baitLabel = baitLabel.substring(0, baitLabel.length() - 1); // truncate, remove last charachter (from bait)
            } else {
                preyLabel = preyLabel.substring(0, preyLabel.length() - 1); // truncate, remove last charachter (from prey)
            }

            interactionLabel = createCandidateShortLabel(baitLabel, preyLabel, suffix);
        } // while

        return interactionLabel;
    }

    /**
     * Search for the first string (in alphabetical order).
     *
     * @param geneNames   a collection of non ordered gene names.
     * @param failOnError if <code>true</code> throw an IntactException when no gene name is found, if
     *                    <code>false</code> sends back <code>null</code>.
     *
     * @return either a String or null according to the failOnError parameter.
     *
     * @throws IntactException thrown when the failOnError parameter is true and no string can be returned.
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
                String[] _geneNames = geneNames.toArray(new String[geneNames.size()]);
                Arrays.sort( _geneNames, new GeneNameIgnoreCaseComparator() );
                shortlabel = _geneNames[ 0 ];
                break;
        }

        return shortlabel;
    }

    /**
     * Create an interaction shortlabel out of two shortlabels. <br> Take care about the maximum length of the field.
     * <br> It checks as well if the generated shortlabel as already been associated to an other Interaction.
     *
     * @param interaction The interaction we are investigating on.
     * @param experiments    Collection in which after processing we have all ExperimentWrapper (shortlabel +
     *                       experimentDescription) in which the interaction hasn't been created yet.
     * @param bait           the label for the bait (could be gene name or SPTR entry AC)
     * @param prey           the label for the prey (could be gene name or SPTR entry AC)
     */ /*  
    public static void createInteractionShortLabels( final Interaction interaction,
                                                      final Collection<Experiment> experiments,
                                                      String bait,
                                                      String prey )
            throws IntactException {

        // convert bad characters ('-', ' ', '.') to '_'
        bait = bait.toLowerCase();
        bait = SearchReplace.replace( bait, "-", "_" );
        bait = SearchReplace.replace( bait, " ", "_" );
        bait = SearchReplace.replace( bait, ".", "_" );

        prey = prey.toLowerCase();
        prey = SearchReplace.replace( prey, "-", "_" );
        prey = SearchReplace.replace( prey, " ", "_" );
        prey = SearchReplace.replace( prey, ".", "_" );

        int count = 0;
        String _bait = bait;
        String _prey = prey;
        boolean allLabelFound = false;
        String label = null;
        String suffix = null;

        // check out the curation rules to know how to create an interaction shortlabel.
        // http://www3.ebi.ac.uk/internal/seqdb/curators/intact/Intactcurationrules_000.htm

        while ( !allLabelFound ) {

            if ( count == 0 ) {
                suffix = null;
                label = _bait + "-" + _prey;
            } else {
                suffix = "-" + count;
                label = _bait + "-" + _prey + suffix;
            }

            count = ++count;

            // check if truncation needed.
            // if so, remove one character from the longest between bait and prey ... until the length is right.
            while ( label.length() > AnnotatedObject.MAX_SHORT_LABEL_LEN ) {
                if ( _bait.length() > _prey.length() ) {
                    _bait = _bait.substring( 0, _bait.length() - 1 ); // truncate, remove last charachter (from bait)
                } else {
                    _prey = _prey.substring( 0, _prey.length() - 1 ); // truncate, remove last charachter (from prey)
                }

                if ( suffix == null ) {
                    label = _bait + "-" + _prey;
                } else {
                    label = _bait + "-" + _prey + suffix;
                }
            } // while

            // we have the right label's size now ... search for existing one !
            if ( log.isDebugEnabled() ) {
                log.debug( "Search interaction by label: " + label );
            }

            Collection<InteractionImpl> interactions = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao().getByShortLabelLike(label);

            if ( interactions.size() == 0 ) {

                if ( log.isDebugEnabled()  ) {
                    log.debug( "No interaction found with the label: " + label );
                }

                // Give the remaining experiment a shortlabel.
                // takes care of gaps in the shortlabel sequence (label-1, label-2, label-3 ...).
                // could create new gaps if some already exists.
                boolean atLeastOneInteractionWithoutShortlabel = false;
                boolean oneExperimentHasAlreadyBeenUpdated = false;

                for ( Iterator<Experiment> iterator = experiments.iterator(); iterator.hasNext() && !atLeastOneInteractionWithoutShortlabel; )
                {
                    ExperimentWrapper experimentWrapper = (ExperimentWrapper) iterator.next();
                    // we want to associate only one shortlabel per loop and check if there is at least one
                    // more experiment to update.
                    if ( log.isDebugEnabled() ) {
                        log.debug( "Work on " + experimentWrapper );
                    }
                    if ( oneExperimentHasAlreadyBeenUpdated ) {
                        if ( !experimentWrapper.hasShortlabel() ) {
                            atLeastOneInteractionWithoutShortlabel = true; // exit the loop.
                            if ( log.isDebugEnabled() ) {
                                log.debug( "At least one more experiment to which we have to give a shortlabel" );
                            }
                        } else {
                            if ( log.isDebugEnabled() ) {
                                log.debug( "has already a shortlabel" );
                            }
                        }
                    } else {
                        if ( !experimentWrapper.hasShortlabel() ) {
                            experimentWrapper.setShortlabel( label );
                            oneExperimentHasAlreadyBeenUpdated = true;
                            if ( log.isDebugEnabled() ) {
                                log.debug( "Experiment " + experimentWrapper.getExperiment().getShortlabel()
                                                    + " has been given the interaction shortlabel: " + label );
                            }
                        } else {
                            if ( log.isDebugEnabled() ) {
                                log.debug( "none has been set up to now and the current one has already a shortlabel" );
                            }
                        }
                    }
                }

                if ( log.isDebugEnabled() ) {
                    if ( atLeastOneInteractionWithoutShortlabel == true ) {
                        log.debug( "All experiment have been given an interaction shortlabel." );
                    }
                }

                allLabelFound = !atLeastOneInteractionWithoutShortlabel;
            } else {

                if ( log.isDebugEnabled() ) {
                    log.debug( interactions.size() + " interactions found with the label: " + label );
                }

                /**
                 * An interaction already exists in an experiment if:
                 *       (1) The shortlabel has the prefix bait-prey
                 *       (2) if components involved (Protein + Role) are identical.
                 *           If the components are somehow different, a new Interaction should be created
                 *           with the same prefix and a suffix that is not already in use.
                 *           eg.
                 *                We have 3 Proteins:
                 *                    - P1: gene-name -> gene1
                 *                    - P1-1: gene-name -> gene1 (got from P1)
                 *                    - P2: gene-name -> gene2
                 *
                 *                We have 2 interactions
                 *                    - Interaction 1 have interaction between P1(bait) and P2(prey)
                 *                      gives us the shortlabel gene1-gene2-1
                 *                    - Interaction 1 have interaction between P1-1(bait) and P2(prey)
                 *                      gives us the shortlabel gene1-gene2-2
                 *                      (!) the components involved are different even if the gene name are identical
                 *                          hence, we get same interaction name with suffixes 1 and 2.
                 *

                for ( Iterator iterator = interactions.iterator(); iterator.hasNext(); ) {
                    Interaction intactInteraction = (Interaction) iterator.next();

                    // that updates the experiment collection and only leaves those for which we have to create a new interaction
                    alreadyExistsInIntact( psiInteraction, experiments, intactInteraction ); // update experiments !

                } // intact interaction
            }
        } // while
    }
         */
    private static class GeneNameIgnoreCaseComparator implements Comparator<String> {

        ////////////////////////////////////////////////
        // Implementation of the Comparable interface
        ////////////////////////////////////////////////

        /**
         * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
         * integer as this object is less than, equal to, or greater than the specified object.<p>
         *
         * @param o1 the String to be compared.
         * @param o2 the String to compare with.
         *
         * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater
         *         than the specified object.
         *
         * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
         */
        public int compare( String o1, String o2 ) {

            final String s1 = o1.toLowerCase();
            final String s2 = o2.toLowerCase();

            // the current string comes first if it's before in the alphabetical order
            if ( !( s1.equals( s2 ) ) ) {
                return s1.compareTo( s2 );
            } else {
                return 0;
            }
        }

    }
}