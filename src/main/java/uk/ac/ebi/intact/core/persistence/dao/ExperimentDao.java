/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Date;
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

    /**
     * Get a list of experiments for the provided publication id
     * @param pubId the publication id
     * @return experiments for that publication
     */
    List<Experiment> getByPubId( String pubId );

    /**
     * Get a list of experiments for the provided publication ac in IntAct
     * @param pubAc the publication accession in IntAct
     * @return experiments for that publication
     */
    List<Experiment> getByPubAc(String pubAc);

    /**
     * Get a list of experiments for the provided publication id and with a label like the provided
     * @param pubId the publication id
     * @param labelLike the label to use. It has to contain '%'
     * @return experiments for that publication
     */
    List<Experiment> getByPubIdAndLabelLike( String pubId, String labelLike );

    /**
     * Finds experiments that have a matching "lastImexUpdate", using a range of dates.
     * <p/>
     * i.e. fromDate <= d <= toDate
     * @param fromDate The from date (inclusive)
     * @param toDate The to date (inclusive)
     * @return The experiments with the "lastImexUpdate" between the two dates.
     */
    List<Experiment> getByLastImexUpdate( Date fromDate, Date toDate);

    /**
     *
     * @param biosourceAc
     * @return the list of experiments having this host organism
     */
    List<Experiment> getByHostOrganism(String biosourceAc);
}
