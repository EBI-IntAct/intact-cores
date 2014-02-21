package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.ModelledEntity;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.*;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.ComplexLifecycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Implementation of IntactDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20/02/14</pre>
 */
@Repository
@Lazy
public class IntactDaoImpl implements IntactDao{
    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;

    private SynchronizerContext synchronizerContext;
    private AliasDao aliasDao;
    private XrefDao xrefDao;
    private AnnotationDao annotationDao;
    private AllosteryDao allosteryDao;
    private CausalRelationshipDao causalRelationshipDao;
    private ChecksumDao checksumDao;
    private ComplexDao complexDao;
    private ConfidenceDao confidenceDao;
    private CooperativeEffectDao cooperativeEffectDao;
    private CooperativityEvidenceDao cooperativityEvidenceDao;
    private CvTermDao cvTermDao;
    private EntityDao entityDao;
    private ExperimentalEntityDao<IntactExperimentalEntity> experimentalEntityDao;
    private ExperimentalEntityPoolDao experimentalEntityPoolDao;
    private ParticipantEvidenceDao<IntactParticipantEvidence> participantEvidenceDao;
    private ModelledEntityDao<IntactModelledEntity> modelledEntityDao;
    private ModelledParticipantDao<IntactModelledParticipant> modelledParticipantDao;
    private ModelledEntityPoolDao modelledEntityPoolDao;
    private FeatureDao featureDao;
    private FeatureEvidenceDao featureEvidenceDao;
    private ExperimentDao experimentDao;
    private InteractionDao interactionDao;
    private InteractorDao interactorDao;
    private InteractorPoolDao interactorPoolDao;
    private BioactiveEntityDao bioactiveEntityDao;
    private LifeCycleEventDao lifecycleEventDao;
    private OrganismDao organismDao;
    private ParameterDao parameterDao;
    private PolymerDao polymerDao;
    private PreferenceDao preferenceDao;
    private UserDao userDao;
    private RoleDao roleDao;
    private PublicationDao<IntactPublication> publicationDao;
    private CuratedPublicationDao curatedPublicationDao;
    private RangeDao rangeDao;
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

    public <T extends AbstractIntactAlias> AliasDao<T> getAliasDao(Class<T> aliasClass) {
        if (this.aliasDao == null){
            this.aliasDao = new AliasDaoImpl(AbstractIntactAlias.class, getEntityManager(), getSynchronizerContext());
        }
        this.aliasDao.setEntityClass(aliasClass);
        return this.aliasDao;
    }

    public AliasDao<CvTermAlias> getCvAliasDao() {
        return getAliasDao(CvTermAlias.class);
    }

    public AliasDao<SourceAlias> getSourceAliasDao() {
        return getAliasDao(SourceAlias.class);
    }

    public AliasDao<OrganismAlias> getOrganismAliasDao() {
        return getAliasDao(OrganismAlias.class);
    }

    public AliasDao<FeatureAlias> getFeatureAliasDao() {
        return getAliasDao(FeatureAlias.class);
    }

    public AliasDao<EntityAlias> getEntityAliasDao() {
        return getAliasDao(EntityAlias.class);
    }

    public AliasDao<InteractorAlias> getInteractorAliasDao() {
        return getAliasDao(InteractorAlias.class);
    }

    public <T extends AbstractIntactXref> XrefDao<T> getXrefDao(Class<T> xrefClass) {
        if (this.xrefDao == null){
           this.xrefDao = new XrefDaoImpl(AbstractIntactXref.class, getEntityManager(), getSynchronizerContext());
        }
        this.xrefDao.setEntityClass(xrefClass);
        return this.xrefDao;
    }

    public XrefDao<CvTermXref> getCvXrefDao() {
        return getXrefDao(CvTermXref.class);
    }

    public XrefDao<SourceXref> getSourceXrefDao() {
        return getXrefDao(SourceXref.class);
    }

    public XrefDao<PublicationXref> getPublicationXrefDao() {
        return getXrefDao(PublicationXref.class);
    }

    public XrefDao<ExperimentXref> getExperimentXrefDao() {
        return getXrefDao(ExperimentXref.class);
    }

    public XrefDao<InteractionXref> getInteractionXrefDao() {
        return getXrefDao(InteractionXref.class);
    }

    public XrefDao<InteractorXref> getInteractorXrefDao() {
        return getXrefDao(InteractorXref.class);
    }

    public XrefDao<FeatureXref> getFeatureXrefDao() {
        return getXrefDao(FeatureXref.class);
    }

    public XrefDao<EntityXref> getEntityXrefDao() {
        return getXrefDao(EntityXref.class);
    }

    public XrefDao<ResultingSequenceXref> getResultingSequenceXrefDao() {
        return getXrefDao(ResultingSequenceXref.class);
    }

    public <T extends AbstractIntactAnnotation> AnnotationDao<T> getAnnotationDao(Class<T> annotationClass) {
        if (this.annotationDao == null){
           this.annotationDao = new AnnotationDaoImpl(AbstractIntactAnnotation.class, getEntityManager(), getSynchronizerContext());
        }
        this.annotationDao.setEntityClass(annotationClass);
        return this.annotationDao;
    }

    public AnnotationDao<CvTermAnnotation> getCvAnnotationDao() {
        return getAnnotationDao(CvTermAnnotation.class);
    }

    public AnnotationDao<SourceAnnotation> getSourceAnnotationDao() {
        return getAnnotationDao(SourceAnnotation.class);
    }

    public AnnotationDao<PublicationAnnotation> getPublicationAnnotationDao() {
        return getAnnotationDao(PublicationAnnotation.class);
    }

    public AnnotationDao<ExperimentAnnotation> getExperimentAnnotationDao() {
        return getAnnotationDao(ExperimentAnnotation.class);
    }

    public AnnotationDao<InteractionAnnotation> getInteractionAnnotationDao() {
        return getAnnotationDao(InteractionAnnotation.class);
    }

    public AnnotationDao<InteractorAnnotation> getInteractorAnnotationDao() {
        return getAnnotationDao(InteractorAnnotation.class);
    }

    public AnnotationDao<FeatureAnnotation> getFeatureAnnotationDao() {
        return getAnnotationDao(FeatureAnnotation.class);
    }

    public AnnotationDao<EntityAnnotation> getEntityAnnotationDao() {
        return getAnnotationDao(EntityAnnotation.class);
    }

    public AnnotationDao<CooperativeEffectAnnotation> getCooperativeEffectAnnotationDao() {
        return getAnnotationDao(CooperativeEffectAnnotation.class);
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

    public <T extends AbstractIntactCooperativeEffect> CooperativeEffectDao<T> getCooperativeEffectDao(Class<T> effectClass) {
        if (this.cooperativeEffectDao == null){
            this.cooperativeEffectDao = new CooperativeEffectDaoImpl(AbstractIntactCooperativeEffect.class, getEntityManager(), getSynchronizerContext());
        }
        this.cooperativeEffectDao.setEntityClass(effectClass);
        return this.cooperativeEffectDao;
    }

    public CooperativeEffectDao<IntactPreassembly> getPreAssemblyDao() {
        return getCooperativeEffectDao(IntactPreassembly.class);
    }

    public AllosteryDao getAllosteryDao() {
        if (this.allosteryDao == null){
           this.allosteryDao = new AllosteryDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.allosteryDao;
    }

    public CooperativityEvidenceDao getCooperativityEvidenceDao() {
        if (this.cooperativityEvidenceDao == null){
            this.cooperativityEvidenceDao = new CooperativityEvidenceDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.cooperativityEvidenceDao;
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

    public CausalRelationshipDao getCausalRelationshipDao() {
        if (this.causalRelationshipDao == null){
            this.causalRelationshipDao = new CausalRelationshipDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.causalRelationshipDao;
    }

    public <T extends AbstractIntactChecksum> ChecksumDao<T> getChecksumDao(Class<T> checksumClass) {
        if (this.checksumDao == null){
            this.checksumDao = new ChecksumDaoImpl(AbstractIntactChecksum.class, getEntityManager(), getSynchronizerContext());
        }
        this.checksumDao.setEntityClass(checksumClass);
        return this.checksumDao;
    }

    public ChecksumDao<InteractionChecksum> getInteractionChecksumDao() {
        return getChecksumDao(InteractionChecksum.class);
    }

    public ChecksumDao<InteractorChecksum> getInteractorChecksumDao() {
        return getChecksumDao(InteractorChecksum.class);
    }

    public <T extends AbstractIntactConfidence> ConfidenceDao<T> getConfidenceDao(Class<T> confidenceClass) {
        if (this.confidenceDao == null){
            this.confidenceDao = new ConfidenceDaoImpl(AbstractIntactConfidence.class, getEntityManager(), getSynchronizerContext());
        }
        this.confidenceDao.setEntityClass(confidenceClass);
        return this.confidenceDao;
    }

    public ConfidenceDao<ComplexConfidence> getComplexConfidenceDao() {
        return getConfidenceDao(ComplexConfidence.class);
    }

    public ConfidenceDao<InteractionEvidenceConfidence> getInteractionConfidenceDao() {
        return getConfidenceDao(InteractionEvidenceConfidence.class);
    }

    public ConfidenceDao<ExperimentalEntityConfidence> getEntityConfidenceDao() {
        return getConfidenceDao(ExperimentalEntityConfidence.class);
    }

    public <T extends AbstractIntactParameter> ParameterDao<T> getParameterDao(Class<T> parameterClass) {
        if (this.parameterDao == null){
            this.parameterDao = new ParameterDaoImpl(AbstractIntactParameter.class, getEntityManager(), getSynchronizerContext());
        }
        this.parameterDao.setEntityClass(parameterClass);
        return this.parameterDao;
    }

    public ParameterDao<ComplexParameter> getComplexParameterDao() {
        return getParameterDao(ComplexParameter.class);
    }

    public ParameterDao<InteractionEvidenceParameter> getInteractionParameterDao() {
        return getParameterDao(InteractionEvidenceParameter.class);
    }

    public ParameterDao<ExperimentalEntityParameter> getEntityParameterDao() {
        return getParameterDao(ExperimentalEntityParameter.class);
    }

    public OrganismDao getOrganismDao() {
        if (this.organismDao == null){
            this.organismDao = new OrganismDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.organismDao;
    }

    public RangeDao getRangeDao() {
        if (this.rangeDao == null){
            this.rangeDao = new RangeDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.rangeDao;
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

    public PublicationDao<IntactPublication> getPublicationDao() {
        if (this.publicationDao == null){
            this.publicationDao = new PublicationDaoImpl<IntactPublication>(IntactPublication.class, getEntityManager(), getSynchronizerContext());
        }
        return this.publicationDao;
    }

    public CuratedPublicationDao getCuratedPublicationDao() {
        if (this.curatedPublicationDao == null){
            this.curatedPublicationDao = new CuratedPublicationDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.curatedPublicationDao;
    }

    public ExperimentDao getExperimentDao() {
        if (this.experimentDao == null){
            this.experimentDao = new ExperimentDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.experimentDao;
    }

    public <T extends AbstractLifecycleEvent> LifeCycleEventDao<T> getLifecycleDao(Class<T> lifecycleClass) {
        if (this.lifecycleEventDao == null){
            this.lifecycleEventDao = new LifeCycleDaoImpl(AbstractLifecycleEvent.class, getEntityManager(), getSynchronizerContext());
        }
        this.lifecycleEventDao.setEntityClass(lifecycleClass);
        return this.lifecycleEventDao;
    }

    public LifeCycleEventDao<ComplexLifecycleEvent> getComplexLifecycleDao() {
        return getLifecycleDao(ComplexLifecycleEvent.class);
    }

    public LifeCycleEventDao<PublicationLifecycleEvent> getPublicationLifecycleDao() {
        return getLifecycleDao(PublicationLifecycleEvent.class);
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
        return getFeatureDao(IntactModelledFeature.class);
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

    public <T extends AbstractIntactEntity> EntityDao<T> getEntityDao(Class<T> entityClass) {
        if (this.entityDao == null){
            this.entityDao = new EntityDaoImpl(AbstractIntactEntity.class, getEntityManager(), getSynchronizerContext());
        }
        this.entityDao.setEntityClass(entityClass);
        return this.entityDao;
    }

    public ModelledEntityDao<IntactModelledEntity> getModelledEntityDao() {
        if (this.modelledEntityDao == null){
            this.modelledEntityDao = new ModelledEntityDaoImpl<ModelledEntity, IntactModelledEntity>(IntactModelledEntity.class, getEntityManager(), getSynchronizerContext());
        }
        return this.modelledEntityDao;
    }

    public ModelledParticipantDao<IntactModelledParticipant> getModelledParticipantDao() {
        if (this.modelledParticipantDao == null){
            this.modelledParticipantDao = new ModelledParticipantDaoImpl<ModelledParticipant, IntactModelledParticipant>(IntactModelledParticipant.class, getEntityManager(), getSynchronizerContext());
        }
        return this.modelledParticipantDao;
    }

    public ModelledEntityPoolDao getModelledEntityPoolDao() {
        if (this.modelledEntityPoolDao == null){
            this.modelledEntityPoolDao = new ModelledEntityPoolDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.modelledEntityPoolDao;
    }

    public ExperimentalEntityDao<IntactExperimentalEntity> getExperimentalEntityDao() {
        if (this.experimentalEntityDao == null){
            this.experimentalEntityDao = new ExperimentalEntityDaoImpl<ExperimentalEntity, IntactExperimentalEntity>(IntactExperimentalEntity.class, getEntityManager(), getSynchronizerContext());
        }
        return this.experimentalEntityDao;
    }

    public ParticipantEvidenceDao<IntactParticipantEvidence> getParticipantEvidenceDao() {
        if (this.participantEvidenceDao == null){
            this.participantEvidenceDao = new ParticipantEvidenceDaoImpl<ParticipantEvidence, IntactParticipantEvidence>(IntactParticipantEvidence.class, getEntityManager(), getSynchronizerContext());
        }
        return this.participantEvidenceDao;
    }

    public ExperimentalEntityPoolDao getExperimentalEntityPoolDao() {
        if (this.experimentalEntityPoolDao == null){
            this.experimentalEntityPoolDao = new ExperimentalEntityPoolDaoImpl(getEntityManager(), getSynchronizerContext());
        }
        return this.experimentalEntityPoolDao;
    }

}
