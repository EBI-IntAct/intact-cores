/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Iterator;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface ExperimentDao extends AnnotatedObjectDao<Experiment> {

    Integer countInteractionsForExperimentWithAc( String ac );

    List<Interaction> getInteractionsForExperimentWithAc( String ac, int firstResult, int maxResults );

    Iterator<Interaction> getInteractionsForExperimentWithAcIterator( String ac );

    List<Interaction> getInteractionsForExperimentWithAcExcluding( String ac, String[] excludedAcs, int firstResult, int maxResults );

    List<Interaction> getInteractionsForExperimentWithAcExcludingLike( String ac, String[] excludedAcsLike, int firstResult, int maxResults );
}
