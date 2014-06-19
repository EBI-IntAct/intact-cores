package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.*;
import uk.ac.ebi.intact.jami.model.lifecycle.AbstractLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.ComplexLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.PublicationLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Implementation of IntactDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20/02/14</pre>
 */
@Repository(value = "intactDao")
@Lazy
public class IntactDaoImpl implements IntactDao {
    @PersistenceContext(unitName = "intact-jami")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-jami", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private SynchronizerContext synchronizerContext;
    private ComplexDao complexDao;
    private CvTermDao cvTermDao;
    private ParticipantDao entityDao;
    private ParticipantEvidenceDao<IntactParticipantEvidence> participantEvidenceDao;
    private ModelledParticipantDao<IntactModelledParticipant> modelledParticipantDao;
    private FeatureDao featureDao;
    private FeatureEvidenceDao featureEvidenceDao;
    private FeatureDao<IntactModelledFeature> modelledFeatureDao;
    private ExperimentDao experimentDao;
    private InteractionDao interactionDao;
    private InteractorDao interactorDao;
    private InteractorPoolDao interactorPoolDao;
    private BioactiveEntityDao bioactiveEntityDao;
    private LifeCycleEventDao lifecycleEventDao;
    private OrganismDao organismDao;
    private PolymerDao polymerDao;
    private PreferenceDao preferenceDao;
    private UserDao userDao;
    private RoleDao roleDao;
    private PublicationDao publicationDao;
    private SourceDao sourceDao;
    private VariableParameterDao variableParameterDao;
    private VariableParameterValueDao variableParameterValueDao;
    private VariableParameterValueSetDao variableParameterValueSetDao;

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public SynchronizerContext getSynchronizerContext() {
        if (this.synchronizerContext == null){
            this.synchronizerContext = new DefaultSynchronizerContext(getEntityManager());
        }
        return this.synchronizerContext;
    }

    public CvTermDao getCvTermDao() {
        if (this.cvTermDao == null){
            this.cvTermDao = new CvTermDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.cvTermDao;
    }

    public SourceDao getSourceDao() {
        if (this.sourceDao == null){
            this.sourceDao = new SourceDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.sourceDao;
    }

    public <T extends IntactInteractor> InteractorDao<T> getInteractorDao(Class<T> interactorClass) {
        if (this.interactorDao == null){
            this.interactorDao = new InteractorDaoImpl(IntactInteractor.class, getEntityManager(), getSynchronizerContext());
        }
        this.interactorDao.setEntityClass(interactorClass);
        return this.interactorDao;
    }

    public ComplexDao getComplexDao() {
        if (this.complexDao == null){
            this.complexDao = new ComplexDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.complexDao;
    }

    public InteractorDao<IntactInteractor> getInteractorBaseDao() {
        return getInteractorDao(IntactInteractor.class);
    }

    public <T extends IntactPolymer> PolymerDao<T> getPolymerDao(Class<T> polymerClass) {
        if (this.polymerDao == null){
            this.polymerDao = new PolymerDaoImpl(IntactPolymer.class, getEntityManager(), getSynchronizerContext());
        }
        this.polymerDao.setEntityClass(polymerClass);
        return this.polymerDao;
    }

    public PolymerDao<IntactPolymer> getPolymerBaseDao() {
        return getPolymerDao(IntactPolymer.class);
    }

    public PolymerDao<IntactProtein> getProteinDao() {
        return getPolymerDao(IntactProtein.class);
    }

    public PolymerDao<IntactNucleicAcid> getNucleicAcidDao() {
        return getPolymerDao(IntactNucleicAcid.class);
    }

    public InteractorDao<IntactMolecule> getMoleculeDao() {
        return getInteractorDao(IntactMolecule.class);
    }

    public InteractorDao<IntactGene> getGeneDao() {
        return getInteractorDao(IntactGene.class);
    }

    public InteractorPoolDao getInteractorPoolDao() {
        if (this.interactorPoolDao == null){
            this.interactorPoolDao = new InteractorPoolDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.interactorPoolDao;
    }

    public BioactiveEntityDao getBioactiveEntityDao() {
        if (this.bioactiveEntityDao == null){
            this.bioactiveEntityDao = new BioactiveEntityDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.bioactiveEntityDao;
    }


    public OrganismDao getOrganismDao() {
        if (this.organismDao == null){
            this.organismDao = new OrganismDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.organismDao;
    }

    public PreferenceDao getPreferenceDao() {
        if (this.preferenceDao == null){
            this.preferenceDao = new PreferenceDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.preferenceDao;
    }

    public RoleDao getRoleDao() {
        if (this.roleDao == null){
            this.roleDao = new RoleDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.roleDao;
    }

    public UserDao getUserDao() {
        if (this.userDao == null){
            this.userDao = new UserDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.userDao;
    }

    public PublicationDao getPublicationDao() {
        if (this.publicationDao == null){
            this.publicationDao = new PublicationDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.publicationDao;
    }

    public ExperimentDao getExperimentDao() {
        if (this.experimentDao == null){
            this.experimentDao = new ExperimentDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.experimentDao;
    }

    public <T extends AbstractLifeCycleEvent> LifeCycleEventDao<T> getLifecycleDao(Class<T> lifecycleClass) {
        if (this.lifecycleEventDao == null){
            this.lifecycleEventDao = new LifeCycleDaoImpl(AbstractLifeCycleEvent.class, getEntityManager(), getSynchronizerContext());
        }
        this.lifecycleEventDao.setEntityClass(lifecycleClass);
        return this.lifecycleEventDao;
    }

    public LifeCycleEventDao<ComplexLifeCycleEvent> getComplexLifecycleDao() {
        return getLifecycleDao(ComplexLifeCycleEvent.class);
    }

    public LifeCycleEventDao<PublicationLifeCycleEvent> getPublicationLifecycleDao() {
        return getLifecycleDao(PublicationLifeCycleEvent.class);
    }

    public InteractionDao getInteractionDao() {
        if (this.interactionDao == null){
            this.interactionDao = new InteractionDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.interactionDao;
    }

    public <T extends AbstractIntactFeature> FeatureDao<T> getFeatureDao(Class<T> featureClass) {
        if (this.featureDao == null){
            this.featureDao = new FeatureDaoImpl(AbstractIntactFeature.class, getEntityManager(), getSynchronizerContext());
        }
        this.featureDao.setEntityClass(featureClass);
        return this.featureDao;
    }

    public FeatureEvidenceDao getFeatureEvidenceDao() {
        if (this.featureEvidenceDao == null){
            this.featureEvidenceDao = new FeatureEvidenceDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.featureEvidenceDao;
    }

    public FeatureDao<IntactModelledFeature> getModelledFeatureDao() {
        if (this.modelledFeatureDao == null){
            this.modelledFeatureDao = new ModelledFeatureDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.modelledFeatureDao;
    }

    public VariableParameterDao getVariableParameterDao() {
        if (this.variableParameterDao == null){
            this.variableParameterDao = new VariableParameterDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.variableParameterDao;
    }

    public VariableParameterValueDao getVariableParameterValueDao() {
        if (this.variableParameterValueDao == null){
            this.variableParameterValueDao = new VariableParameterValueDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.variableParameterValueDao;
    }

    public VariableParameterValueSetDao getVariableParameterValueSetDao() {
        if (this.variableParameterValueSetDao == null){
            this.variableParameterValueSetDao = new VariableParameterValueSetDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.variableParameterValueSetDao;
    }

    public <T extends AbstractIntactParticipant> ParticipantDao<T> getParticipantDao(Class<T> entityClass) {
        if (this.entityDao == null){
            this.entityDao = new ParticipantDaoImpl(AbstractIntactParticipant.class, getEntityManager(), getSynchronizerContext());
        }
        this.entityDao.setEntityClass(entityClass);
        return this.entityDao;
    }

    public ModelledParticipantDao<IntactModelledParticipant> getModelledParticipantDao() {
        if (this.modelledParticipantDao == null){
            this.modelledParticipantDao = new ModelledParticipantDaoImpl<ModelledParticipant, IntactModelledParticipant>(IntactModelledParticipant.class, getEntityManager(), getSynchronizerContext());
        }
        return this.modelledParticipantDao;
    }

    public ParticipantEvidenceDao<IntactParticipantEvidence> getParticipantEvidenceDao() {
        if (this.participantEvidenceDao == null){
            this.participantEvidenceDao = new ParticipantEvidenceDaoImpl<ParticipantEvidence, IntactParticipantEvidence>(IntactParticipantEvidence.class, getEntityManager(), getSynchronizerContext());
        }
        return this.participantEvidenceDao;
    }

}
