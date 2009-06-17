/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.core.persistence.dao.XrefDao;

import javax.persistence.EntityManager;
import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Scope;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
@Repository
@Transactional
@Scope(org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE)
public class XrefDaoImpl<T extends Xref> extends IntactObjectDaoImpl<T> implements XrefDao<T> {

    public XrefDaoImpl() {
        super((Class<T>) Xref.class);
    }

    public XrefDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    public Collection<T> getByPrimaryId( String primaryId ) {
        return getColByPropertyName( "primaryId", primaryId );
    }

    public Collection<T> getByPrimaryId( String primaryId, boolean ignoreCase ) {
        return getColByPropertyName( "primaryId", primaryId, ignoreCase );
    }

    public Collection<T> getByPrimaryIdLike( String primaryId ) {
        return getByPropertyNameLike( "primaryId", primaryId );
    }

    public Collection<T> getByParentAc( String parentAc ) {
        return getColByPropertyName( "parentAc", parentAc );
    }

    public Collection<T> getByParentAc( String parentAc, boolean ignoreCase ) {
        return getColByPropertyName( "parentAc", parentAc, ignoreCase );
    }
}
