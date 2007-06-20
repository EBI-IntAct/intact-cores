/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.InteractorImpl;

import java.util.List;

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

    public int countInteractorInvolvedInInteraction();

    public List<T> getInteractorInvolvedInInteraction( Integer firstResult, Integer maxResults );
}
