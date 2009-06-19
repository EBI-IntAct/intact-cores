/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.PublicationDao;
import uk.ac.ebi.intact.model.Publication;

import javax.persistence.EntityManager;

/**
 * DAO for publications
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-aug-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class PublicationDaoImpl extends AnnotatedObjectDaoImpl<Publication> implements PublicationDao {

    public PublicationDaoImpl( ) {
        super( Publication.class );
    }

    public PublicationDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Publication.class, entityManager, intactSession );
    }

    public Publication getByPubmedId(String pubmedId) {
        return getByShortLabel(pubmedId);
    }
}
