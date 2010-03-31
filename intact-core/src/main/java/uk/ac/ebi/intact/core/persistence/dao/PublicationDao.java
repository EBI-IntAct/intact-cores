/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Publication;

import java.util.Date;
import java.util.List;

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

    /**
     * Finds publications that have a matching "lastImexUpdate", using a range of dates.
     * <p/>
     * i.e. fromDate <= d <= toDate
     * @param fromDate The from date (inclusive)
     * @param toDate The to date (inclusive)
     * @return The publications with the "lastImexUpdate" between the two dates.
     */
    List<Publication> getByLastImexUpdate( Date fromDate, Date toDate);

    /**
     * Count the number of experiments attached to this publication.
     * @param ac AC of the publication.
     * @return the count of experiment.
     */
    int countExperimentsForPublicationAc( String ac );

    /**
     * Count the number of interactions attached to this publication.
     * @param ac AC of the publication.
     * @return the count of interactions.
     */
    int countInteractionsForPublicationAc( String ac );
}
