/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.ComplexLifecycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;

/**
 * DAO for accessing objects in IntAct.
 *
 * This dao gives access to the entity manager, synchronizer context and other specialised dao
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface IntactDao {

    public EntityManager getEntityManager();

    public SynchronizerContext getSynchronizerContext();

    public <T extends AbstractIntactAlias> AliasDao<T> getAliasDao(Class<T> aliasClass);

    public AliasDao<CvTermAlias> getCvAliasDao();

    public AliasDao<SourceAlias> getSourceAliasDao();

    public AliasDao<OrganismAlias> getOrganismAliasDao();

    public AliasDao<FeatureAlias> getFeatureAliasDao();

    public AliasDao<EntityAlias> getEntityAliasDao();

    public AliasDao<InteractorAlias> getInteractorAliasDao();

    public <T extends AbstractIntactXref> XrefDao<T> getXrefDao(Class<T> xrefClass);

    public XrefDao<CvTermXref> getCvXrefDao();

    public XrefDao<SourceXref> getSourceXrefDao();

    public XrefDao<PublicationXref> getPublicationXrefDao();

    public XrefDao<ExperimentXref> getExperimentXrefDao();

    public XrefDao<InteractionXref> getInteractionXrefDao();

    public XrefDao<InteractorXref> getInteractorXrefDao();

    public XrefDao<FeatureXref> getFeatureXrefDao();

    public XrefDao<EntityXref> getEntityXrefDao();

    public XrefDao<ResultingSequenceXref> getResultingSequenceXrefDao();

    public <T extends AbstractIntactAnnotation> AnnotationDao<T> getAnnotationDao(Class<T> annotationClass);

    public AnnotationDao<CvTermAnnotation> getCvAnnotationDao();

    public AnnotationDao<SourceAnnotation> getSourceAnnotationDao();

    public AnnotationDao<PublicationAnnotation> getPublicationAnnotationDao();

    public AnnotationDao<ExperimentAnnotation> getExperimentAnnotationDao();

    public AnnotationDao<InteractionAnnotation> getInteractionAnnotationDao();

    public AnnotationDao<InteractorAnnotation> getInteractorAnnotationDao();

    public AnnotationDao<FeatureAnnotation> getFeatureAnnotationDao();

    public AnnotationDao<EntityAnnotation> getEntityAnnotationDao();

    public AnnotationDao<CooperativeEffectAnnotation> getCooperativeEffectAnnotationDao();

    public CvTermDao getCvTermDao();

    public SourceDao getSourceDao();

    public <T extends AbstractIntactCooperativeEffect> CooperativeEffectDao<T> getCooperativeEffectDao(Class<T> effectClass);

    public CooperativeEffectDao<IntactPreassembly> getPreAssemblyDao();

    public AllosteryDao getAllosteryDao();

    public CooperativityEvidenceDao getCooperativityEvidenceDao();

    public <T extends IntactInteractor> InteractorDao<T> getInteractorDao(Class<T> interactorClass);

    public ComplexDao getComplexDao();

    public InteractorDao<IntactInteractor> getInteractorBaseDao();

    public <T extends IntactPolymer> PolymerDao<T> getPolymerDao(Class<T> polymerClass);

    public PolymerDao<IntactPolymer> getPolymerBaseDao();

    public PolymerDao<IntactProtein> getProteinDao();

    public PolymerDao<IntactNucleicAcid> getNucleicAcidDao();

    public InteractorDao<IntactMolecule> getMoleculeDao();

    public InteractorDao<IntactGene> getGeneDao();

    public InteractorPoolDao getInteractorPoolDao();

    public BioactiveEntityDao getBioactiveEntityDao();

    public CausalRelationshipDao getCausalRelationshipDao();

    public <T extends AbstractIntactChecksum> ChecksumDao<T> getChecksumDao(Class<T> checksumClass);

    public ChecksumDao<InteractionChecksum> getInteractionChecksumDao();

    public ChecksumDao<InteractorChecksum> getInteractorChecksumDao();

    public <T extends AbstractIntactConfidence> ConfidenceDao<T> getConfidenceDao(Class<T> confidenceClass);

    public ConfidenceDao<ComplexConfidence> getComplexConfidenceDao();

    public ConfidenceDao<InteractionEvidenceConfidence> getInteractionConfidenceDao();

    public ConfidenceDao<ExperimentalEntityConfidence> getEntityConfidenceDao();

    public <T extends AbstractIntactParameter> ParameterDao<T> getParameterDao(Class<T> parameterClass);

    public ParameterDao<ComplexParameter> getComplexParameterDao();

    public ParameterDao<InteractionEvidenceParameter> getInteractionParameterDao();

    public ParameterDao<ExperimentalEntityParameter> getEntityParameterDao();

    public OrganismDao getOrganismDao();

    public RangeDao getRangeDao();

    public PreferenceDao getPreferenceDao();

    public RoleDao getRoleDao();

    public UserDao getUserDao();

    public PublicationDao getPublicationDao();

    public ExperimentDao getExperimentDao();

    public <T extends AbstractLifecycleEvent> LifeCycleEventDao<T> getLifecycleDao(Class<T> lifecycleClass);

    public LifeCycleEventDao<ComplexLifecycleEvent> getComplexLifecycleDao();

    public LifeCycleEventDao<PublicationLifecycleEvent> getPublicationLifecycleDao();

    public InteractionDao getInteractionDao();

    public <T extends AbstractIntactFeature> FeatureDao<T> getFeatureDao(Class<T> featureClass);

    public FeatureEvidenceDao getFeatureEvidenceDao();

    public FeatureDao<IntactModelledFeature> getModelledFeatureDao();

    public VariableParameterDao getVariableParameterDao();

    public VariableParameterValueDao getVariableParameterValueDao();

    public VariableParameterValueSetDao getVariableParameterValueSetDao();

    public <T extends AbstractIntactEntity> EntityDao<T> getEntityDao(Class<T> entityClass);

    public ModelledEntityDao<IntactModelledEntity> getModelledEntityDao();

    public ModelledParticipantDao<IntactModelledParticipant> getModelledParticipantDao();

    public ModelledEntityPoolDao getModelledEntityPoolDao();

    public ExperimentalEntityDao<IntactExperimentalEntity> getExperimentalEntityDao();

    public ParticipantEvidenceDao<IntactParticipantEvidence> getParticipantEvidenceDao();

    public ExperimentalEntityPoolDao getExperimentalEntityPoolDao();
}
