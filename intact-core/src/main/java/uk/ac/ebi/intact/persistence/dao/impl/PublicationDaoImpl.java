/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.persistence.dao.PublicationDao;

import javax.persistence.EntityManager;

/**
 * DAO for publications
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-aug-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class PublicationDaoImpl extends AnnotatedObjectDaoImpl<Publication> implements PublicationDao {

    public PublicationDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Publication.class, entityManager, intactSession );
    }

    public Publication getByPubmedId(String pubmedId) {
        return getByShortLabel(pubmedId);
    }
}
