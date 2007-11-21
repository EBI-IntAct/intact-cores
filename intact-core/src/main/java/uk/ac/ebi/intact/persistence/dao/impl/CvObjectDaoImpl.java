/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.annotation.PotentialThreat;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
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
@SuppressWarnings( "unchecked" )
public class CvObjectDaoImpl<T extends CvObject> extends AnnotatedObjectDaoImpl<T> implements CvObjectDao<T> {

    public CvObjectDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    public List<T> getByPsiMiRefCollection( Collection<String> psiMis ) {
        return getSession().createCriteria( getEntityClass() ).createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvDatabase", "cvDb" )
                .createAlias( "cvDb.xrefs", "cvDbXref" )
                .add( Restrictions.eq( "cvDbXref.primaryId", CvDatabase.PSI_MI_MI_REF ) )
                .add( Restrictions.in( "xref.primaryId", psiMis ) ).list();
    }

    public T getByPsiMiRef( String psiMiRef ) {
        Query query = getEntityManager().createQuery(
                "select cv from "+getEntityClass().getName()+" cv " +
                "left join cv.xrefs as xref " +
                        "join xref.cvDatabase as cvDb join cvDb.xrefs as cvDbXref " +
                        "where cvDbXref.primaryId = '" + CvDatabase.PSI_MI_MI_REF +
                        "' and xref.primaryId = '"+psiMiRef+"'");

        //query.setParameter("dbMiRef", CvDatabase.PSI_MI_MI_REF);
        //query.setParameter("psiMiRef", psiMiRef);

        query.setFlushMode(FlushModeType.COMMIT);

        return uniqueResult(query);
        /* 
        return ( T ) getSession().createCriteria( getEntityClass() ).createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvDatabase", "cvDb" )
                .createAlias( "cvDb.xrefs", "cvDbXref" )
                .add( Restrictions.eq( "cvDbXref.primaryId", CvDatabase.PSI_MI_MI_REF ) )
                .add( Restrictions.eq( "xref.primaryId", psiMiRef ) ).uniqueResult();  */
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

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final CvObjectDao<CvInteractorType> itdao = daoFactory.getCvObjectDao( CvInteractorType.class );

        // 1. load the root term
        CvInteractorType root = itdao.getByPsiMiRef( CvInteractorType.NUCLEIC_ACID_MI_REF );

        Collection<String> collectedMIs = new ArrayList<String>( );
        if( root != null ) {
            // 2. traverse children and collect their MIs
            CvObjectUtils.getChildrenMIs( root, collectedMIs );
        }
        
        return collectedMIs;
    }
}
