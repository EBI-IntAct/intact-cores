/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface InteractionDao extends InteractorDao<InteractionImpl> {

    Integer countInteractorsByInteractionAc( String interactionAc );

    List<String> getNestedInteractionAcsByInteractionAc( String interactionAc );

    List<Interaction> getInteractionByExperimentShortLabel( String[] experimentLabels, Integer firstResult, Integer maxResults );

    List<Interaction> getInteractionsByInteractorAc( String interactorAc );

    List<Interaction> getInteractionsForProtPair( String protAc1, String protAc2 );

    Collection<Interaction> getSelfBinaryInteractionsByProtAc( String protAc );
}
