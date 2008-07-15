/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Publication;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
@Mockable
public interface PublicationDao extends AnnotatedObjectDao<Publication> {

    /**
     * Retrieve a Publication using a pubmed identifier.
     * @param pubmedId The pubmed identifier
     * @return The publication if found, null otherwise
     */
    Publication getByPubmedId(String pubmedId);

}
