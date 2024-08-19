/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.BioSourceDao;
import uk.ac.ebi.intact.model.BioSource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Jun-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
public class BioSourceDaoImpl extends AnnotatedObjectDaoImpl<BioSource> implements BioSourceDao {

    private static final Log log = LogFactory.getLog( BioSourceDaoImpl.class );

    public BioSourceDaoImpl() {
        super(BioSource.class);
    }

    public BioSourceDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( BioSource.class, entityManager, intactSession );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
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

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Collection<BioSource> getByTaxonId( String taxonId ) {

        if ( taxonId == null ) {
            throw new NullPointerException( "taxonId must not be null." );
        }

        return getSession().createCriteria( BioSource.class )
                .add( Restrictions.eq( "taxId", taxonId ) ).list();
    }
}
