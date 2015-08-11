/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface InteractionDao extends InteractorDao<InteractionImpl> {

    Integer countInteractorsByInteractionAc( String interactionAc );

    Integer countAllComplexes();

    List<String> getNestedInteractionAcsByInteractionAc( String interactionAc );

    List<Interaction> getInteractionByExperimentShortLabel( String[] experimentLabels, Integer firstResult, Integer maxResults );

    List<Interaction> getInteractionsByInteractorAc( String interactorAc );

    @Deprecated
    List<Interaction> getInteractionsForProtPair( String protAc1, String protAc2 );

    List<Interaction> getInteractionsForProtPairAc( String protAc1, String protAc2 );

    Collection<Interaction> getSelfBinaryInteractionsByProtAc( String protAc );

    /**
     * Retrieves those interactions that contain the interactors with the provided primary IDs.<br/>
     * The search can be exact (only those interactions where the number of components equals the number
     * of passed primaryIDs) or inexact (gets those interactions that contain all the passed primaryIDs, but may
     * contain more components).<br/>
     * When searching self interactions, if you pass only one primaryId it will get those interactions that only
     * contain only one component (exactComponents has to be true - otherwise the method would return all the interactions
     * where that primaryID is found). In the case where an interaction contains two or more components with the same interactor,
     * you should pass to the method as many -repeated- primaryID as components contain the interaction.
     * @param exactComponents true, if the number of components must match the number of primaryIDs
     * @param primaryIds the number of primaryIDs to search
     * @return the interactions for those primaryIDs
     *
     * @since 1.7.2
     */
    List<Interaction> getByInteractorsPrimaryId(boolean exactComponents, String... primaryIds);

    /**
     * Gets an interaction by its CRC.
     * @param crc The Crc to use
     * @return The interaction (if more than one are found, return the first one and log an error)
     *
     * @since 1.8.0
     */
    Interaction getByCrc(String crc);

    /**
     * Finds interactions that have a matching "lastImexUpdate", using a range of dates.
     * <p/>
     * i.e. fromDate <= d <= toDate
     * @param fromDate The from date (inclusive)
     * @param toDate The to date (inclusive)
     * @return The interaction with the "lastImexUpdate" between the two dates.
     */
    List<Interaction> getByLastImexUpdate( Date fromDate, Date toDate);

    /**
     * Fetches all interaction belonging to a given experiment. Results are returned in pages, the size of which is
     * defined by the parameters firstResult and maxResult. Results are ordered by creation date.
     *
     * @param experimentAc the experiment of which we want the interactions.
     * @param firstResult the first result
     * @param maxResult the maximum size of the page to be read
     * @return a non null list of interactions
     */
    List<Interaction> getByExperimentAc( String experimentAc, int firstResult, int maxResult );

    int countAll( boolean includeNegative );
}
