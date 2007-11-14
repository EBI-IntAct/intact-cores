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
import org.hibernate.ejb.HibernateEntityManager;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.impl.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
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

    private DataConfig dataConfig;

    private EntityManager currentEntityManager;

    private IntactSession intactSession;

    private IntactTransaction currentTransaction;

    protected DaoFactory( DataConfig dataConfig, IntactSession intactSession ) {
        this.dataConfig = dataConfig;
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
        return new AliasDaoImpl( Alias.class, getEntityManager(), intactSession );
    }

    public <T extends Alias> AliasDao<T> getAliasDao( Class<T> aliasType ) {
        return new AliasDaoImpl<T>( aliasType, getEntityManager(), intactSession );
    }

    public AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao() {
        return new AnnotatedObjectDaoImpl<AnnotatedObject>( AnnotatedObject.class, getEntityManager(), intactSession );
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao( Class<T> entityType ) {
        HibernateBaseDaoImpl.validateEntity( entityType );

        return new AnnotatedObjectDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public AnnotationDao getAnnotationDao() {
        return new AnnotationDaoImpl( getEntityManager(), intactSession );
    }

    public BaseDao getBaseDao() {
        // It is returning an ExperimentDaoImpl because HibernateBaseDaoImpl is an abstract class, and ExperimentDaoImpl
        // implement all HibernateBaseDaoImpl anyway.
        return new ExperimentDaoImpl( getEntityManager(), intactSession );
    }

    public BioSourceDao getBioSourceDao() {
        return new BioSourceDaoImpl( getEntityManager(), intactSession );
    }

    public ComponentDao getComponentDao() {
        return new ComponentDaoImpl( getEntityManager(), intactSession );
    }

    public CvObjectDao<CvObject> getCvObjectDao() {
        return new CvObjectDaoImpl<CvObject>( CvObject.class, getEntityManager(), intactSession );
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao( Class<T> entityType ) {
        return new CvObjectDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public DbInfoDao getDbInfoDao() {
        return new DbInfoDaoImpl( getEntityManager(), intactSession );
    }

    public ExperimentDao getExperimentDao() {
        return new ExperimentDaoImpl( getEntityManager(), intactSession );
    }

    public FeatureDao getFeatureDao() {
        return new FeatureDaoImpl( getEntityManager(), intactSession );
    }

    /**
     * @since 1.7.2
     */
    public ImexImportDao getImexImportDao() {
        return new ImexImportDaoImpl(getEntityManager(), intactSession);
    }

    /**
     * @since 1.7.2
     */
    public ImexImportPublicationDao getImexImportPublicationDao() {
        return new ImexImportPublicationDaoImpl(getEntityManager(), intactSession);
    }

    public InstitutionDao getInstitutionDao() {
        return new InstitutionDaoImpl( getEntityManager(), intactSession );
    }

    public IntactObjectDao<IntactObject> getIntactObjectDao() {
        return new IntactObjectDaoImpl<IntactObject>( IntactObject.class, getEntityManager(), intactSession );
    }

    public <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao( Class<T> entityType ) {
        HibernateBaseDaoImpl.validateEntity( entityType );

        return new IntactObjectDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public InteractionDao getInteractionDao() {
        return new InteractionDaoImpl( getEntityManager(), intactSession );
    }

    public <T extends InteractorImpl> InteractorDao<T> getInteractorDao( Class<T> entityType ) {
        return new InteractorDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public InteractorDao<InteractorImpl> getInteractorDao() {
        return new InteractorDaoImpl<InteractorImpl>( InteractorImpl.class, getEntityManager(), intactSession );
    }

    /**
     * @since 1.5
     */
    public MineInteractionDao getMineInteractionDao() {
        return new MineInteractionDaoImpl( getEntityManager(), intactSession );
    }

    public PolymerDao<PolymerImpl> getPolymerDao() {
        return new PolymerDaoImpl<PolymerImpl>( PolymerImpl.class, getEntityManager(), intactSession );
    }

    public <T extends PolymerImpl> PolymerDao<T> getPolymerDao( Class<T> clazz ) {
        return new PolymerDaoImpl<T>( clazz, getEntityManager(), intactSession );
    }

    public ProteinDao getProteinDao() {
        return new ProteinDaoImpl( getEntityManager(), intactSession );
    }

    public PublicationDao getPublicationDao() {
        return new PublicationDaoImpl( getEntityManager(), intactSession );
    }

    public RangeDao getRangeDao() {
        return new RangeDaoImpl( getEntityManager(), intactSession );
    }

    public SearchableDao getSearchableDao() {
        return new SearchableDaoImpl( getEntityManager(), intactSession );
    }

    public SearchItemDao getSearchItemDao() {
        return new SearchItemDaoImpl( getEntityManager(), intactSession );
    }

    public XrefDao<Xref> getXrefDao() {
        return new XrefDaoImpl<Xref>( Xref.class, getEntityManager(), intactSession );
    }

    public <T extends Xref> XrefDao<T> getXrefDao( Class<T> xrefClass ) {
        return new XrefDaoImpl<T>( xrefClass, getEntityManager(), intactSession );
    }

    public Connection connection() {
        return getCurrentSession().connection();
    }

    public IntactTransaction beginTransaction() {
        log.debug("Starting transaction...");
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        currentTransaction = new JpaIntactTransaction( intactSession, transaction);

        return currentTransaction;
    }

    public void commitTransaction() {
        if (currentEntityManager.getTransaction().isActive()) {
            if (log.isDebugEnabled()) log.debug("Committing transaction");

            //if (getEntityManager().getFlushMode() == FlushModeType.COMMIT) {
                getEntityManager().flush();
            //}

            currentEntityManager.getTransaction().commit();
            currentEntityManager.close();
            currentTransaction = null;
        } else {
            if (log.isWarnEnabled()) log.warn("Attempted commit on a transaction that was not active");
        }
    }

    public EntityManager getEntityManager() {
            if (currentEntityManager == null || !currentEntityManager.isOpen()) {
                if (log.isDebugEnabled()) log.debug("Creating new EntityManager");

                if (dataConfig == null) {
                    throw new IllegalStateException("No DataConfig found");
                }

                EntityManagerFactory entityManagerFactory = dataConfig.getEntityManagerFactory();

                if (entityManagerFactory == null) {
                    throw new IllegalStateException("Null EntityManagerFactory for DataConfig with name: "+dataConfig.getName());
                }

                currentEntityManager = entityManagerFactory.createEntityManager();

                if (!dataConfig.isAutoFlush()) {
                    if (log.isDebugEnabled()) log.debug("Data-config is not autoflush. Using flush mode: "+ FlushModeType.COMMIT);
                    currentEntityManager.setFlushMode(FlushModeType.COMMIT);
                }
            }
            return currentEntityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        currentEntityManager = entityManager;
    }

    @Deprecated
    public synchronized Session getCurrentSession() {
        Session session = (Session) getEntityManager().getDelegate();

        //Session session = getSessionFromSessionFactory(dataConfig);

        if (!dataConfig.isAutoFlush()) {
            session.setFlushMode(FlushMode.MANUAL);
        }

        if ( !session.isOpen() ) {
            // this only should happen for hibernate data configs
            if ( log.isDebugEnabled() ) {
                log.debug( "Opening new session because the current is closed" );
            }
            session = ((AbstractHibernateDataConfig)dataConfig).getSessionFactory().openSession();
        }

        return session;
    }


    protected Session getSessionFromSessionFactory(DataConfig dataConfig) {
        return ((HibernateEntityManager)dataConfig.getEntityManagerFactory().createEntityManager()).getSession();
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
