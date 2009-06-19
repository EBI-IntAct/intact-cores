/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;

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

    public static final String INTERACTION_TEMP_LABEL_PREFIX = "unk-unk-";
    private static final String INTERACTION_TEMP_LABEL_PATTERN = INTERACTION_TEMP_LABEL_PREFIX+"\\d*";

    /**
     * Checks if the interaction is a binary interaction
     *
     * @param interaction
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
     * @return
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
     * Creates a shortLabel based on the participants of an interaction - NOT in sync with the database
     * (when interactions are persisted the short label needs to be in sync with the database. If there are
     * existing interactions with the same participants, the short label would be the same, so a prefix
     * with a number should be added)
     *
     * @param interaction the interaction used to calculate the shortlabel
     * @return the short label
     * @see uk.ac.ebi.intact.model.util.InteractionShortLabelGenerator
     * @since 1.6
     */
    public static String calculateShortLabel( final Interaction interaction ) {
        return InteractionShortLabelGenerator.createCandidateShortLabel( interaction );
    }

    /**
     * Syncs a short label with the database, checking that there are no duplicates and that the correct suffix is added.
     * <p/>
     * Concurrency note: just after getting the new short label, it is recommended to persist/update the interaction immediately
     * in the database - so this method should ONLY be used before saving the interaction to the database. In some
     * race conditions, two interactions could be created with the same id; currently there is no way to
     * reserve a short label
     *
     * @param shortLabel the short label to sync
     * @return the synced short label
     * @see uk.ac.ebi.intact.model.util.InteractionShortLabelGenerator
     * @since 1.6
     */
    public static String syncShortLabelWithDb( String shortLabel ) throws IllegalLabelFormatException {
        if (isTemporaryLabel(shortLabel)) {
            if (log.isDebugEnabled()) log.debug("Label for interaction was temporary ("+shortLabel+"), hence not synced with the database");
            return shortLabel;
        }
        return InteractionShortLabelGenerator.nextAvailableShortlabel( shortLabel );
    }

    /**
     * Retreives Imex identifier stored in the Xrefs.
     *
     * @param interaction the interaction to search on.
     * @return an imex id or null if not found.
     */
    public static String getImexIdentifier( Interaction interaction ) {

        if ( interaction == null ) {
            throw new IllegalArgumentException( "You must give a non null interaction" );
        }

        for ( InteractorXref xref : interaction.getXrefs() ) {
            if ( CvDatabase.IMEX_MI_REF.equals( xref.getCvDatabase().getIdentifier() ) ) {
                return xref.getPrimaryId();
            }
        }

        // Could not find an IMEx id
        return null;
    }

    /**
     * checkes if the given interaction has an IMEx identifier.
     *
     * @param interaction the interaction to seach on.
     * @return true if the interaction has an IMEx identifier, false otherwise.
     */
    public static boolean hasImexIdentifier( Interaction interaction ) {
        return getImexIdentifier( interaction ) != null;
    }

    /**
     * Answers the question: "Has the given interaction got only interactors of the given type ?".
     * <p/>
     * note: an interaction without interactor is not valid.
     *
     * @param interaction the interaction.
     * @param type        the interactor type.
     * @return true if all interactor are of the given type, false otherwise.
     */
    public static boolean hasOnlyInteractorOfType( Interaction interaction, CvInteractorType type ) {

        if ( type == null ) {
            throw new IllegalArgumentException( "You must give a non null CvInteractorType." );
        }

        if ( interaction == null ) {
            throw new IllegalArgumentException( "You must give a non null Interaction." );
        }

        if ( interaction.getComponents().isEmpty() ) {
            return false;
        }

        for ( Component component : interaction.getComponents() ) {
            Interactor interactor = component.getInteractor();
            if ( !type.equals( interactor.getCvInteractorType() ) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Collect a distinct set of Interactor associated to the given Interaction.
     *
     * @param interaction the interaction
     * @return a non null Set of Interactor.
     */
    public static Set<Interactor> selectDistinctInteractors( Interaction interaction ) {
        Set<Interactor> interactors = new HashSet<Interactor>( interaction.getComponents().size() );
        for ( Component component : interaction.getComponents() ) {
            interactors.add( component.getInteractor() );
        }
        return interactors;
    }

    /**
     * Returns true if the label for the interaction is temporary
     * @param label
     * @return
     *
     * @since 1.6.3
     */
    public static boolean isTemporaryLabel(String label) {
        return label.matches(INTERACTION_TEMP_LABEL_PATTERN);
    }

    /**
     * Gets the Interactor Identifiers for that interaction
     *
     * @since 1.7.2
     */
    public static List<String> getInteractorPrimaryIDs(Interaction interaction) {
        final Collection<Component> components = interaction.getComponents();

        List<String> ids = new ArrayList<String>(components.size());

        for (Component component : components) {
            Interactor interactor = component.getInteractor();
            final Collection<InteractorXref> idXrefs = XrefUtils.getIdentityXrefs(interactor);

            if (idXrefs.size() > 0) {
                final Iterator<InteractorXref> iterator = idXrefs.iterator();
                ids.add(iterator.next().getPrimaryId());

                if (log.isDebugEnabled()) {
                    if (iterator.hasNext()) {
                        log.debug("Interaction contains interactor with more than one identities. Interaction: "+interaction.getShortLabel()+"("+interaction.getAc()+") "+
                        " - Xrefs: "+ idXrefs);
                    }
                }
            }
        }

        return ids;
    }

}