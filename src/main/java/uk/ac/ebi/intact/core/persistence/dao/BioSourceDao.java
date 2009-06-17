/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.BioSource;

import java.util.Collection;

/**
 * To access to biosources
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Jun-2006</pre>
 */
@Mockable
public interface BioSourceDao extends AnnotatedObjectDao<BioSource> {

    /**
     * Searches for a BioSource having the given taxid and no CvTissue or CvCellType.
     *
     * @param taxonId the taxid we are looking for (non null).
     *
     * @return a biosource, can be null if not found.
     */
    BioSource getByTaxonIdUnique( String taxonId );

    /**
     * Searches for all BioSources having the given taxid.
     *
     * @param taxonId the taxid we are looking for (non null).
     *
     * @return a non null collection of Biosource. May be empty.
     */
    Collection<BioSource> getByTaxonId( String taxonId );
}
