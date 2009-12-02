/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.impl.*;
import uk.ac.ebi.intact.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Factory for all the intact DAOs using Hibernate
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@org.springframework.stereotype.Component
public class DaoFactory implements Serializable {

    private static final Log log = LogFactory.getLog( DaoFactory.class );

    @PersistenceContext(unitName = "intact-core-default")
    private EntityManager currentEntityManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired AnnotationDao annotationDao;
    @Autowired BioSourceDao bioSourceDao;
    @Autowired ComponentDao componentDao;
    @Autowired ComponentParameterDao componentParameterDao;
    @Autowired ConfidenceDao confidenceDao;
    @Autowired DbInfoDao dbInfoDao;
    @Autowired ExperimentDao experimentDao;
    @Autowired FeatureDao featureDao;
    @Autowired ImexExportInteractionDao imexExportInteractionDao;
    @Autowired ImexExportReleaseDao imexExportReleaseDao;
    @Autowired InstitutionDao institutionDao;
    @Autowired InteractionDao interactionDao;
    @Autowired InteractionParameterDao interactionParameterDao;
    @Autowired MineInteractionDao mineInteractionDao;
    @Autowired ProteinDao proteinDao;
    @Autowired PublicationDao publicationDao;
    @Autowired RangeDao rangeDao;
    @Autowired SearchableDao searchableDao;

    public DaoFactory() {

    }

    public static DaoFactory getCurrentInstance( IntactContext context ) {
        return context.getDataContext().getDaoFactory();
    }

    public AliasDao<Alias> getAliasDao() {
        return getAliasDao(Alias.class);
    }

    public <T extends Alias> AliasDao<T> getAliasDao( Class<T> aliasType ) {
        AliasDao aliasDao = (AliasDao) applicationContext.getBean("aliasDaoImpl");
        aliasDao.setEntityClass(aliasType);
        return aliasDao;
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao( Class<T> entityType ) {
        if (entityType.isAssignableFrom(Institution.class)) {
            return (AnnotatedObjectDao<T>)getInstitutionDao();
        } else if (Publication.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getPublicationDao();
        } else if (CvObject.class.isAssignableFrom(entityType)) {
            return getCvObjectDao((Class)entityType);
        } else if (Experiment.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getExperimentDao();
        } else if (Interaction.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getInteractionDao();
        } else if (Interactor.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getInteractorDao();
        } else if (BioSource.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getBioSourceDao();
        } else if (Component.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getComponentDao();
        } else if (Feature.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)getFeatureDao();
        } else {
            throw new IllegalArgumentException( "No Dao for entity type: "+entityType.getClass().getName());
        }
    }

    public AnnotationDao getAnnotationDao() {
        return getBean(AnnotationDaoImpl.class);
    }

    public BaseDao getBaseDao() {
        // It is returning an ExperimentDaoImpl because HibernateBaseDaoImpl is an abstract class, and ExperimentDaoImpl
        // implement all HibernateBaseDaoImpl anyway.
        return experimentDao;
    }

    public BioSourceDao getBioSourceDao() {
        return bioSourceDao;
    }

    public ComponentDao getComponentDao() {
        return componentDao;
    }

    public CvObjectDao<CvObject> getCvObjectDao() {
        return getCvObjectDao(CvObject.class);
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao( Class<T> entityType ) {
        CvObjectDao cvObjectDao = getBean(CvObjectDaoImpl.class);
        cvObjectDao.setEntityClass(entityType);
        return cvObjectDao;
    }

    public DbInfoDao getDbInfoDao() {
        return dbInfoDao;
    }

    public ExperimentDao getExperimentDao() {
        return experimentDao;
    }

    public FeatureDao getFeatureDao() {
        return featureDao;
    }

    public ImexExportInteractionDao getImexExportInteractionDao() {
        return imexExportInteractionDao;
    }

    public ImexExportReleaseDao getImexExportReleaseDao() {
        return imexExportReleaseDao;
    }

    public InstitutionDao getInstitutionDao() {
        return institutionDao;
    }

    public IntactObjectDao<? extends IntactObject> getIntactObjectDao() {
        return experimentDao;
    }

    public InteractionDao getInteractionDao() {
        return interactionDao;
    }

    public <T extends InteractorImpl> InteractorDao<T> getInteractorDao( Class<T> entityType ) {
        InteractorDao interactorDao = getBean(InteractorDaoImpl.class);
        interactorDao.setEntityClass(entityType);
        return interactorDao;
    }

    public InteractorDao<InteractorImpl> getInteractorDao() {
        return getInteractorDao((Class) InteractorImpl.class);
    }

    /**
     * @since 1.5
     */
    public MineInteractionDao getMineInteractionDao() {
        return mineInteractionDao;
    }

    public PolymerDao<PolymerImpl> getPolymerDao() {
        return getPolymerDao(PolymerImpl.class);
    }

    public <T extends PolymerImpl> PolymerDao<T> getPolymerDao( Class<T> clazz ) {
        PolymerDao polymerDao = getBean(PolymerDaoImpl.class);
        polymerDao.setEntityClass(clazz);
        return polymerDao;
    }

    public ProteinDao getProteinDao() {
        return proteinDao;
    }

    public PublicationDao getPublicationDao() {
        return publicationDao;
    }

    public RangeDao getRangeDao() {
        return rangeDao;
    }

    public ConfidenceDao getConfidenceDao(){
        return confidenceDao;
    }
    
    public InteractionParameterDao getInteractionParameterDao(){
        return interactionParameterDao;
    }
    
    public ComponentParameterDao getComponentParameterDao(){
        return componentParameterDao;
    }

    public SearchableDao getSearchableDao() {
        return searchableDao;
    }

    public XrefDao<Xref> getXrefDao() {
        return getXrefDao(Xref.class);
    }

    public <T extends Xref> XrefDao<T> getXrefDao( Class<T> xrefClass ) {
        XrefDao dao = getBean(XrefDaoImpl.class);
        dao.setEntityClass(xrefClass);
        return dao;
    }

    public EntityManager getEntityManager() {
        return currentEntityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        currentEntityManager = entityManager;
    }

    private <T> T getBean(Class<T> beanType) {
        return (T) applicationContext.getBean(StringUtils.uncapitalize(beanType.getSimpleName()));
    }
}
