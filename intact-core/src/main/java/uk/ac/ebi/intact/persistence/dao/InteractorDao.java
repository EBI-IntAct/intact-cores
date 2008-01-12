/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.InteractorImpl;
import uk.ac.ebi.intact.model.Interactor;

import java.util.List;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface InteractorDao<T extends InteractorImpl> extends AnnotatedObjectDao<T> {

    Integer countInteractionsForInteractorWithAc( String ac );

    Integer countComponentsForInteractorWithAc( String ac );

    List<String> getGeneNamesByInteractorAc( String proteinAc );

    List<T> getByBioSourceAc( String ac );

    int countInteractorInvolvedInInteraction();

    List<T> getInteractorInvolvedInInteraction( Integer firstResult, Integer maxResults );

    /**
     * Counts the interactors, excluding the interactions
     * @return the number of interactors, excluding the interactions
     */
    long countAllInteractors();

    /**
     * Gets the interactors, excluding the interactions
     * @param firstResult First index to fetch
     * @param maxResults Number of interactors to fetch
     * @return the interactors in that page
     */
    List<Interactor> getInteractors(Integer firstResult, Integer maxResults);

    /**
     * Counts the partners of the provided interactor AC
     * @param ac The AC to search
     * @return The number of parntners for the interactor AC
     *
     * @since 1.8.0
     */
    Integer countPartnersByAc( String ac );

    /**
     * Get the partners and the interaction ACs for the passes interactor AC
     * @param ac The AC to look parntners for
     * @return A Map containing the partner AC as key and a list of interaction ACs as value
     *
     * @since 1.8.0
     */
    Map<String, List<String>> getPartnersWithInteractionAcsByInteractorAc( String ac );
}
