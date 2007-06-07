/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.persistence.dao.BioSourceDao;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Jun-2006</pre>
 */
public class BioSourceDaoImpl extends AnnotatedObjectDaoImpl<BioSource> implements BioSourceDao {

    private static final Log log = LogFactory.getLog( BioSourceDaoImpl.class );

    public BioSourceDaoImpl( Session session, IntactSession intactSession ) {
        super( BioSource.class, session, intactSession );
    }


    public BioSource getByTaxonIdUnique( String taxonId ) {

        if ( taxonId == null ) {
            throw new NullPointerException( "taxonId must not be null." );
        }

        Collection<BioSource> biosources = getByTaxonId( taxonId );

        // Get the biosource with null values for cell type and tisse
        //  (there is only one of them exists).
        for ( BioSource biosrc : biosources ) {
            if ( ( biosrc.getCvCellType() == null ) && ( biosrc.getCvTissue() == null ) ) {
                return biosrc;
            }
        }
        // None found.
        return null;
    }

    public Collection<BioSource> getByTaxonId( String taxonId ) {

        if ( taxonId == null ) {
            throw new NullPointerException( "taxonId must not be null." );
        }

        return getSession().createCriteria( BioSource.class )
                .add( Restrictions.eq( "taxId", taxonId ) ).list();
    }
}
