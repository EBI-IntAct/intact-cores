/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.persistence.dao.PublicationDao;

/**
 * DAO for publications
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-aug-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class PublicationDaoImpl extends AnnotatedObjectDaoImpl<Publication> implements PublicationDao {

    public PublicationDaoImpl( Session session, IntactSession intactSession ) {
        super( Publication.class, session, intactSession );
    }
}
