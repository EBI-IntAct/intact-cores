/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.annotation.PotentialThreat;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dao to play with CVs
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-May-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@Scope(org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE)
@SuppressWarnings( "unchecked" )
public class CvObjectDaoImpl<T extends CvObject> extends AnnotatedObjectDaoImpl<T> implements CvObjectDao<T> {

    public CvObjectDaoImpl() {
        super((Class<T>) CvObject.class);
    }

    public CvObjectDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager );
    }

    public CvObjectDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    public List<T> getByPsiMiRefCollection( Collection<String> psiMis ) {
        return getSession().createCriteria( getEntityClass() )
                .add( Restrictions.in( "miIdentifier", psiMis ) ).list();
    }

    /**
     *
     * @deprecated use getByIdentifier(id)
     */
    @Deprecated
    public T getByPsiMiRef( String psiMiRef ) {
        return getByIdentifier(psiMiRef);
    }

     public T getByIdentifier( String id ) {
        Query query = getEntityManager().createQuery(
                "select cv from "+getEntityClass().getName()+" cv " +
                "where identifier = '"+id+"'");

        return uniqueResult(query);
    }

    public List<T> getByObjClass( Class[] objClasses ) {
        Criteria criteria = getSession().createCriteria( CvObject.class );

        Disjunction disj = Restrictions.disjunction();

        for ( Class objClass : objClasses ) {
            disj.add( Restrictions.eq( "objClass", objClass.getName() ) );
        }

        criteria.add( disj );

        return criteria.list();
    }


    @Override
    @Deprecated
    @PotentialThreat( description = "Labels are not unique in the database, so you could " +
                                    "get more than one result and this method would fail" )
    public T getByShortLabel( String value ) {
        return super.getByShortLabel( value );
    }

    public <T extends CvObject> T getByShortLabel( Class<T> cvType, String label ) {
        return ( T ) getSession().createCriteria( cvType )
                .add( Restrictions.eq( "shortLabel", label ) ).uniqueResult();
    }

    public <T extends CvObject> T getByPrimaryId( Class<T> cvType, String miRef ) {
        return ( T ) getSession().createCriteria( cvType )
                .createCriteria( "xrefs" )
                .add( Restrictions.eq( "primaryId", miRef ) ).uniqueResult();
    }

    public Collection<String> getNucleicAcidMIs() {
         final CvObjectDao<CvInteractorType> itdao = IntactContext.getCurrentInstance().getDataContext()
                    .getDaoFactory().getCvObjectDao( CvInteractorType.class);

        // 1. load the root term
        CvInteractorType root = itdao.getByPsiMiRef( CvInteractorType.NUCLEIC_ACID_MI_REF );

        Collection<String> collectedMIs = new ArrayList<String>( );
        if( root != null ) {
            // 2. traverse children and collect their MIs
            CvObjectUtils.getChildrenMIs( root, collectedMIs );
        }
        
        return collectedMIs;
    }

    public Integer getLastCvIdentifierWithPrefix(String prefix) {
        // query that returns all the primaryIds that contain the prefix
        Query query = getEntityManager().createQuery("select xref.primaryId from CvObjectXref xref " +
                                                       "where xref.cvXrefQualifier.identifier = :identityQualifier " +
                                                       "and xref.primaryId like :primaryIdPrefix");
        query.setParameter("identityQualifier", CvXrefQualifier.IDENTITY_MI_REF);
        query.setParameter("primaryIdPrefix", prefix+":%");

        List<String> idsWithPrefix = query.getResultList();

        // if no xrefs with this prefix, return null
        if (idsWithPrefix.isEmpty()) {
            return null;
        }

        int max = -1;

        for (String id : idsWithPrefix) {
            String strNumber = id.split(":")[1];

            int number = 0;
            try {
                number = Integer.parseInt(strNumber);
            } catch (NumberFormatException e) {
                throw new IntactException("The following id with prefix '"+prefix+"' was found, and no number could be parsed " +
                                          "after the colon: "+strNumber, e);
            }

            max = Math.max(max, number);
        }

        return max;
    }
}
