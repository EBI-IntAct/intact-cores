/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.impl.*;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Factory for all the intact DAOs using Hibernate
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class DaoFactory implements Serializable {

    private static final Log log = LogFactory.getLog( DaoFactory.class );

    private static final String DAO_FACTORY_ATT_NAME = DaoFactory.class.getName();

    private AbstractHibernateDataConfig dataConfig;

    private IntactSession intactSession;

    private IntactTransaction currentTransaction;

    private DaoFactory( DataConfig dataConfig, IntactSession intactSession ) {
        this.dataConfig = ( AbstractHibernateDataConfig ) dataConfig;
        this.intactSession = intactSession;
    }

    public static DaoFactory getCurrentInstance( IntactContext context ) {
        return getCurrentInstance( context.getSession(), context.getConfig().getDefaultDataConfig() );
    }

    public static DaoFactory getCurrentInstance( IntactContext context, String dataConfigName ) {
        return getCurrentInstance( context.getSession(), context.getConfig().getDataConfig( dataConfigName ) );
    }

    public static DaoFactory getCurrentInstance( IntactSession session, DataConfig dataConfig ) {
        String attName = DAO_FACTORY_ATT_NAME + "-" + dataConfig.getName();

        // when an application starts (with IntactConfigurator) the request is not yet available
        // the we store the daoFactory in application scope
        if ( !session.isRequestAvailable() ) {
            log.debug( "Getting DaoFactory from application, because request is not available at this point" +
                       " (probably the application is initializing)" );
            if ( session.getApplicationAttribute( attName ) != null ) {
                return ( DaoFactory ) session.getApplicationAttribute( attName );
            }

            DaoFactory daoFactory = new DaoFactory( dataConfig, session );
            session.setApplicationAttribute( attName, daoFactory );

            return daoFactory;
        }

        if ( session.getRequestAttribute( attName ) != null ) {
            return ( DaoFactory ) session.getRequestAttribute( attName );
        }

        DaoFactory daoFactory = new DaoFactory( dataConfig, session );
        session.setRequestAttribute( attName, daoFactory );

        return daoFactory;
    }

    public static DaoFactory getCurrentInstance( IntactSession session, DataConfig dataConfig, boolean forceCreationOfFactory ) {
        if ( forceCreationOfFactory ) {
            return new DaoFactory( dataConfig, session );
        }

        String attName = DAO_FACTORY_ATT_NAME + "-" + dataConfig.getName();

        if ( session.getRequestAttribute( attName ) != null ) {
            return ( DaoFactory ) session.getRequestAttribute( attName );
        }

        DaoFactory daoFactory = new DaoFactory( dataConfig, session );
        session.setRequestAttribute( attName, daoFactory );

        return daoFactory;
    }

    public AliasDao<Alias> getAliasDao() {
        return new AliasDaoImpl( Alias.class, getCurrentSession(), intactSession );
    }

    public <T extends Alias> AliasDao<T> getAliasDao( Class<T> aliasType ) {
        return new AliasDaoImpl<T>( aliasType, getCurrentSession(), intactSession );
    }

    public AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao() {
        return new AnnotatedObjectDaoImpl<AnnotatedObject>( AnnotatedObject.class, getCurrentSession(), intactSession );
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao( Class<T> entityType ) {
        HibernateBaseDaoImpl.validateEntity( entityType );

        return new AnnotatedObjectDaoImpl<T>( entityType, getCurrentSession(), intactSession );
    }

    public AnnotationDao getAnnotationDao() {
        return new AnnotationDaoImpl( getCurrentSession(), intactSession );
    }

    public BaseDao getBaseDao() {
        // It is returning an ExperimentDaoImpl because HibernateBaseDaoImpl is an abstract class, and ExperimentDaoImpl
        // implement all HibernateBaseDaoImpl anyway.
        return new ExperimentDaoImpl( getCurrentSession(), intactSession );
    }

    public BioSourceDao getBioSourceDao() {
        return new BioSourceDaoImpl( getCurrentSession(), intactSession );
    }

    public ComponentDao getComponentDao() {
        return new ComponentDaoImpl( getCurrentSession(), intactSession );
    }

    public CvObjectDao<CvObject> getCvObjectDao() {
        return new CvObjectDaoImpl<CvObject>( CvObject.class, getCurrentSession(), intactSession );
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao( Class<T> entityType ) {
        return new CvObjectDaoImpl<T>( entityType, getCurrentSession(), intactSession );
    }

    public DbInfoDao getDbInfoDao() {
        return new DbInfoDaoImpl( getCurrentSession(), intactSession );
    }

    public ExperimentDao getExperimentDao() {
        return new ExperimentDaoImpl( getCurrentSession(), intactSession );
    }

    public FeatureDao getFeatureDao() {
        return new FeatureDaoImpl( getCurrentSession(), intactSession );
    }

    public InstitutionDao getInstitutionDao() {
        return new InstitutionDaoImpl( getCurrentSession(), intactSession );
    }

    public IntactObjectDao<IntactObject> getIntactObjectDao() {
        return new IntactObjectDaoImpl<IntactObject>( IntactObject.class, getCurrentSession(), intactSession );
    }

    public <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao( Class<T> entityType ) {
        HibernateBaseDaoImpl.validateEntity( entityType );

        return new IntactObjectDaoImpl<T>( entityType, getCurrentSession(), intactSession );
    }

    public InteractionDao getInteractionDao() {
        return new InteractionDaoImpl( getCurrentSession(), intactSession );
    }

    public <T extends InteractorImpl> InteractorDao<T> getInteractorDao( Class<T> entityType ) {
        return new InteractorDaoImpl<T>( entityType, getCurrentSession(), intactSession );
    }

    public InteractorDao<InteractorImpl> getInteractorDao() {
        return new InteractorDaoImpl<InteractorImpl>( InteractorImpl.class, getCurrentSession(), intactSession );
    }

    /**
     * @since 1.5
     */
    public MineInteractionDao getMineInteractionDao() {
        return new MineInteractionDaoImpl( getCurrentSession(), intactSession );
    }

    public PolymerDao<PolymerImpl> getPolymerDao() {
        return new PolymerDaoImpl<PolymerImpl>( PolymerImpl.class, getCurrentSession(), intactSession );
    }

    public <T extends PolymerImpl> PolymerDao<T> getPolymerDao( Class<T> clazz ) {
        return new PolymerDaoImpl<T>( clazz, getCurrentSession(), intactSession );
    }

    public ProteinDao getProteinDao() {
        return new ProteinDaoImpl( getCurrentSession(), intactSession );
    }

    public PublicationDao getPublicationDao() {
        return new PublicationDaoImpl( getCurrentSession(), intactSession );
    }

    public RangeDao getRangeDao() {
        return new RangeDaoImpl( getCurrentSession(), intactSession );
    }

    public SearchableDao getSearchableDao() {
        return new SearchableDaoImpl( getCurrentSession(), intactSession );
    }

    public SearchItemDao getSearchItemDao() {
        return new SearchItemDaoImpl( getCurrentSession(), intactSession );
    }

    public XrefDao<Xref> getXrefDao() {
        return new XrefDaoImpl<Xref>( Xref.class, getCurrentSession(), intactSession );
    }

    public <T extends Xref> XrefDao<T> getXrefDao( Class<T> xrefClass ) {
        return new XrefDaoImpl<T>( xrefClass, getCurrentSession(), intactSession );
    }

    public Connection connection() {
        return getCurrentSession().connection();
    }

    public IntactTransaction beginTransaction() {
        log.debug( "Starting transaction..." );
        Transaction transaction = getCurrentSession().beginTransaction();

        // wrap it
        currentTransaction = new IntactTransaction( intactSession, transaction );

        return currentTransaction;
    }

    public synchronized Session getCurrentSession() {
        Session session = dataConfig.getSessionFactory().getCurrentSession();

        if (!dataConfig.isAutoFlush()) {
            session.setFlushMode(FlushMode.MANUAL);
        }

        if ( !session.isOpen() ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Opening new session because the current is closed" );
            }
            session = dataConfig.getSessionFactory().openSession();
        }

        return session;
    }

    public boolean isTransactionActive() {
        boolean active = ( currentTransaction != null && !currentTransaction.wasCommitted() );
        if( log.isDebugEnabled() ) {
            log.debug( "Current transaction is " + (currentTransaction == null ? "null" : ( active ? "active" : "committed" ) ) );
        }
        return active;
    }

    public IntactTransaction getCurrentTransaction() {
        return currentTransaction;
    }
}
