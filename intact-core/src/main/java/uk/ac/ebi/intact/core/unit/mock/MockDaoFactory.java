/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.unit.mock;

import org.hibernate.Session;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.*;

import javax.persistence.EntityTransaction;
import java.sql.Connection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MockDaoFactory extends DaoFactory {

    private IntactSession instactSession;

    private AliasDao<? extends Alias> aliasDao;
    private AnnotatedObjectDao<? extends AnnotatedObject> annotatedObjectDao;
    private AnnotationDao annotationDao;
    private BaseDao baseDao;
    private BioSourceDao bioSourceDao;
    private ComponentDao componentDao;
    private CvObjectDao<? extends CvObject> cvObjectDao;
    private DbInfoDao dbInfoDao;
    private ExperimentDao experimentDao;
    private FeatureDao featureDao;
    private InstitutionDao institutionDao;
    private IntactObjectDao<? extends IntactObject> intactObjectDao;
    private InteractionDao interactionDao;
    private ConfidenceDao confidenceDao;
    private InteractionParameterDao interactionParameterDao;
    private ComponentParameterDao componentParameterDao;
    private InteractorDao<? extends Interactor> interactorDao;
    private MineInteractionDao mineInteractionDao;
    private PolymerDao polymerDao;
    private ProteinDao proteinDao;
    private PublicationDao publicationDao;
    private RangeDao rangeDao;
    private SearchableDao searchableDao;
    private SearchItemDao searchItemDao;
    private XrefDao<? extends Xref> xrefDao;

    protected MockDaoFactory(DataConfig dataConfig, IntactSession intactSession) {
        super(dataConfig, intactSession);
        this.instactSession = intactSession;
    }

    @Override
    public EntityTransaction beginTransaction() {
        return null;
    }

    @Override
    public Connection connection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AliasDao<Alias> getAliasDao() {
        checkIfDaoIsSet(aliasDao, AliasDao.class);
        return (AliasDao<Alias>) aliasDao;
    }

    @Override
    public <T extends Alias> AliasDao<T> getAliasDao(Class<T> aliasType) {
        checkIfDaoIsSet(aliasDao, AliasDao.class);
        return (AliasDao<T>) aliasDao;
    }

    @Override
    public AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao() {
        checkIfDaoIsSet(annotatedObjectDao, AnnotatedObjectDao.class);
        return (AnnotatedObjectDao<AnnotatedObject>) annotatedObjectDao;
    }

    @Override
    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao(Class<T> entityType) {
        checkIfDaoIsSet(annotatedObjectDao, AnnotatedObjectDao.class);
        return (AnnotatedObjectDao<T>) annotatedObjectDao;
    }

    @Override
    public AnnotationDao getAnnotationDao() {
        checkIfDaoIsSet(annotationDao, AnnotationDao.class);
        return annotationDao;
    }

    @Override
    public BaseDao getBaseDao() {
        checkIfDaoIsSet(baseDao, BaseDao.class);
        return baseDao;
    }

    @Override
    public BioSourceDao getBioSourceDao() {
        checkIfDaoIsSet(bioSourceDao, BioSourceDao.class);
        return bioSourceDao;
    }

    @Override
    public ComponentDao getComponentDao() {
        checkIfDaoIsSet(componentDao, ComponentDao.class);
        return componentDao;
    }

    @Override
    public synchronized Session getCurrentSession() {
        return super.getCurrentSession();
    }

    @Override
    public EntityTransaction getCurrentTransaction() {
        return super.getCurrentTransaction();
    }

    @Override
    public CvObjectDao<CvObject> getCvObjectDao() {
        checkIfDaoIsSet(cvObjectDao, CvObjectDao.class);
        return (CvObjectDao<CvObject>) cvObjectDao;
    }

    @Override
    public <T extends CvObject> CvObjectDao<T> getCvObjectDao(Class<T> entityType) {
        checkIfDaoIsSet(cvObjectDao, CvObjectDao.class);
        return (CvObjectDao<T>) cvObjectDao;
    }

    @Override
    public DbInfoDao getDbInfoDao() {
        checkIfDaoIsSet(dbInfoDao, DbInfoDao.class);
        return dbInfoDao;
    }

    @Override
    public ExperimentDao getExperimentDao() {
        checkIfDaoIsSet(experimentDao, ExperimentDao.class);
        return experimentDao;
    }

    @Override
    public FeatureDao getFeatureDao() {
        checkIfDaoIsSet(featureDao, FeatureDao.class);
        return featureDao;
    }

    @Override
    public InstitutionDao getInstitutionDao() {
        checkIfDaoIsSet(institutionDao, InstitutionDao.class);
        return institutionDao;
    }

    @Override
    public IntactObjectDao<IntactObject> getIntactObjectDao() {
        checkIfDaoIsSet(intactObjectDao, IntactObjectDao.class);
        return (IntactObjectDao<IntactObject>) intactObjectDao;
    }

    @Override
    public <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao(Class<T> entityType) {
        checkIfDaoIsSet(interactionDao, IntactObjectDao.class);
        return (IntactObjectDao<T>) intactObjectDao;
    }

    @Override
    public InteractionDao getInteractionDao() {
        checkIfDaoIsSet(interactionDao, InteractionDao.class);
        return interactionDao;
    }

    @Override
    public InteractorDao<InteractorImpl> getInteractorDao() {
        checkIfDaoIsSet(interactorDao, InteractorDao.class);
        return (InteractorDao<InteractorImpl>) interactorDao;
    }

    @Override
    public <T extends InteractorImpl> InteractorDao<T> getInteractorDao(Class<T> entityType) {
        checkIfDaoIsSet(interactorDao, InteractorDao.class);
        return (InteractorDao<T>)  interactorDao;
    }

    @Override
    public MineInteractionDao getMineInteractionDao() {
        checkIfDaoIsSet(mineInteractionDao, MineInteractionDao.class);
        return mineInteractionDao;
    }

    @Override
    public PolymerDao<PolymerImpl> getPolymerDao() {
        checkIfDaoIsSet(polymerDao, PolymerDao.class);
        return polymerDao;
    }

    @Override
    public <T extends PolymerImpl> PolymerDao<T> getPolymerDao(Class<T> clazz) {
        checkIfDaoIsSet(polymerDao, PolymerDao.class);
        return polymerDao;
    }

    @Override
    public ProteinDao getProteinDao() {
        checkIfDaoIsSet(proteinDao, ProteinDao.class);
        return proteinDao;
    }

    @Override
    public PublicationDao getPublicationDao() {
        checkIfDaoIsSet(publicationDao, PublicationDao.class);
        return publicationDao;
    }

    @Override
    public RangeDao getRangeDao() {
        checkIfDaoIsSet(rangeDao, RangeDao.class);
        return rangeDao;
    }

    @Override
    public ConfidenceDao getConfidenceDao(){
        checkIfDaoIsSet(confidenceDao, ConfidenceDao.class);
        return confidenceDao;
    }
    
    @Override
    public InteractionParameterDao getInteractionParameterDao(){
        checkIfDaoIsSet(interactionParameterDao, InteractionParameterDao.class);
        return interactionParameterDao;
    }
    
    @Override
    public ComponentParameterDao getComponentParameterDao(){
        checkIfDaoIsSet(componentParameterDao, ComponentParameterDao.class);
        return componentParameterDao;
    }

    @Override
    public SearchableDao getSearchableDao() {
        checkIfDaoIsSet(searchableDao, SearchableDao.class);
        return searchableDao;
    }

    @Override
    public SearchItemDao getSearchItemDao() {
        checkIfDaoIsSet(searchItemDao, SearchItemDao.class);
        return searchItemDao;
    }

    @Override
    public XrefDao<Xref> getXrefDao() {
        checkIfDaoIsSet(xrefDao, XrefDao.class);
        return (XrefDao<Xref>) xrefDao;
    }

    @Override
    public <T extends Xref> XrefDao<T> getXrefDao(Class<T> xrefClass) {
        checkIfDaoIsSet(xrefDao, XrefDao.class);
        return (XrefDao<T>) xrefDao;
    }

    @Override
    public boolean isTransactionActive() {
        return super.isTransactionActive();
    }

    private void checkIfDaoIsSet(Object dao, Class expectedDaoClass) {
        if (dao == null) {
            throw new IllegalMockDaoException("Mock dao not registered, and it has been called:  "+expectedDaoClass.getName());
        }
    }

    // mock dao setters

    public void setMockAliasDao(AliasDao<? extends Alias> aliasDao) {
        this.aliasDao = aliasDao;
    }

    public void setMockAnnotatedObjectDao(AnnotatedObjectDao<? extends AnnotatedObject> annotatedObjectDao) {
        this.annotatedObjectDao = annotatedObjectDao;
    }

    public void setMockAnnotationDao(AnnotationDao annotationDao) {
        this.annotationDao = annotationDao;
    }

    public void setMockBaseDao(BaseDao baseDao) {
        this.baseDao = baseDao;
    }

    public void setMockBioSourceDao(BioSourceDao bioSourceDao) {
        this.bioSourceDao = bioSourceDao;
    }

    public void setMockComponentDao(ComponentDao componentDao) {
        this.componentDao = componentDao;
    }

    public void setMockCvObjectDao(CvObjectDao<? extends CvObject> cvObjectDao) {
        this.cvObjectDao = cvObjectDao;
    }

    public void setMockDbInfoDao(DbInfoDao dbInfoDao) {
        this.dbInfoDao = dbInfoDao;
    }

    public void setMockExperimentDao(ExperimentDao experimentDao) {
        this.experimentDao = experimentDao;
    }

    public void setMockFeatureDao(FeatureDao featureDao) {
        this.featureDao = featureDao;
    }

    public void setMockIntactSession(IntactSession instactSession) {
        this.instactSession = instactSession;
    }

    public void setMockInstitutionDao(InstitutionDao institutionDao) {
        this.institutionDao = institutionDao;
    }

    public void setMockIntactObjectDao(IntactObjectDao<? extends IntactObject> intactObjectDao) {
        this.intactObjectDao = intactObjectDao;
    }

    public void setMockInteractionDao(InteractionDao interactionDao) {
        this.interactionDao = interactionDao;
    }

    public void setMockInteractorDao(InteractorDao<? extends Interactor> interactorDao) {
        this.interactorDao = interactorDao;
    }

    public void setMockMineInteractionDao(MineInteractionDao mineInteractionDao) {
        this.mineInteractionDao = mineInteractionDao;
    }

    public void setMockPolymerDao(PolymerDao polymerDao) {
        this.polymerDao = polymerDao;
    }

    public void setMockProteinDao(ProteinDao proteinDao) {
        this.proteinDao = proteinDao;
    }

    public void setMockPublicationDao(PublicationDao publicationDao) {
        this.publicationDao = publicationDao;
    }

    public void setMockRangeDao(RangeDao rangeDao) {
        this.rangeDao = rangeDao;
    }

    public void setMockConfidenceDao(ConfidenceDao confidenceDao) {
        this.confidenceDao = confidenceDao;        
    }
    
    public void setMockInteractionParameterDao(InteractionParameterDao interactionParameterDao) {
        this.interactionParameterDao = interactionParameterDao;        
    }   

    public void setMockSearchableDao(SearchableDao searchableDao) {
        this.searchableDao = searchableDao;
    }

    public void setMockSearchItemDao(SearchItemDao searchItemDao) {
        this.searchItemDao = searchItemDao;
    }

    public void setMockXrefDao(XrefDao<? extends Xref> xrefDao) {
        this.xrefDao = xrefDao;
    }
}
