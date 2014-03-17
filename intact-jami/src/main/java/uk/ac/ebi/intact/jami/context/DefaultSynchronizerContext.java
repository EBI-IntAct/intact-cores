package uk.ac.ebi.intact.jami.context;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.ComplexLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.impl.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;

/**
 * Default implementation of SynchronizerContext
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19/02/14</pre>
 */

public class DefaultSynchronizerContext implements SynchronizerContext {
    private EntityManager entityManager;

    // cv synchronizer
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> generalCvSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> databaseSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> qualifierSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> topicSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> aliasTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> featureTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalRoleSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> biologicalRoleSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> interactionDetectionMethodSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> interactionTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> participantDetectionMethodSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalPreparationSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> interactorTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> rangeStatusSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> confidenceTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> parameterTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> cellTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> tissueSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> featureDetectionMethodSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> lifecycleStatusSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> lifecycleEventSynchronizer;

    // source synchronizer
    private IntactDbSynchronizer<Source, IntactSource> sourceSynchronizer;

    // aliases synchronizers
    private AliasSynchronizer aliasSynchronizer;

    // annotation synchronizers
    private AnnotationSynchronizer annotationSynchronizer;

    // xrefs synchronizers
    private XrefSynchronizer xrefSynchronizer;

    // cooperative effect synchronizers
    private CooperativeEffectSynchronizer cooperativeEffectSynchronizer;
    private CooperativeEffectSynchronizer preAssemblySynchronizer;
    private CooperativeEffectSynchronizer allosterySynchronizer;

    // interactor synchronizers
    private InteractorSynchronizer<Interactor, IntactInteractor> interactorSynchronizer;
    private InteractorSynchronizer interactorBaseSynchronizer;
    private InteractorSynchronizer polymerSynchronizer;
    private InteractorSynchronizer<Complex, IntactComplex> complexSynchronizer;
    private InteractorSynchronizer<InteractorPool, IntactInteractorPool> interactorPoolSynchronizer;

    // causal relationship synchronizers
    private IntactDbSynchronizer causalRelationshipSynchronizer;

    // confidence synchronizers
    private ConfidenceSynchronizer confidenceSynchronizer;

    // parameters synchronizers
    private ParameterSynchronizer parameterSynchronizer;

    // organism synchronizers
    private IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer;

    // range synchronizers
    private IntactDbSynchronizer rangeSynchronizer;

    // preference synchronizers
    private IntactDbSynchronizer<Preference, Preference> preferenceSynchronizer;

    // role synchronizers
    private IntactDbSynchronizer<Role, Role> roleSynchronizer;

    // user synchronizers
    private IntactDbSynchronizer<User, User> userSynchronizer;

    // publication synchronizers
    private IntactDbSynchronizer<Publication, IntactPublication> simplePublicationSynchronizer;
    private IntactDbSynchronizer<Publication, IntactCuratedPublication> publicationSynchronizer;

    // experiment synchronizers
    private IntactDbSynchronizer<Experiment, IntactExperiment> experimentSynchronizer;

    // interaction synchronizers
    private IntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence> interactionEvidenceSynchronizer;

    // cooperativity evidence synchronizers
    private IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> cooperativityEvidenceSynchronizer;

    // lifecycle synchronizers
    private LifecycleEventSynchronizer lifecycleSynchronizer;

    // feature synchronizers
    private IntactDbSynchronizer<Feature, AbstractIntactFeature> featureSynchronizer;
    private IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> featureEvidenceSynchronizer;
    private IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> modelledFeatureSynchronizer;

    // variable parameter synchronizers
    private IntactDbSynchronizer<VariableParameter, IntactVariableParameter> variableParameterSynchronizer;

    // variable parameter value synchronizers
    private IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> variableParameterValueSynchronizer;

    // variable parameter value set
    private IntactDbSynchronizer<VariableParameterValueSet, IntactVariableParameterValueSet> variableParameterValueSetSynchronizer;

    // participant synchronizers
    private ParticipantSynchronizer<Participant, AbstractIntactParticipant> participantSynchronizer;
    private ParticipantSynchronizer<ModelledParticipant, IntactModelledParticipant> modelledParticipantSynchronizer;
    private ParticipantSynchronizer<ModelledParticipantPool, IntactModelledParticipantPool> modelledParticipantPoolSynchronizer;
    private ParticipantSynchronizer<ParticipantEvidence, IntactParticipantEvidence> participantEvidenceSynchronizer;
    private ParticipantSynchronizer<ParticipantEvidencePool, IntactParticipantEvidencePool> ParticipantEvidencePoolSynchronizer;

    public DefaultSynchronizerContext(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("Entity manager cannot be null in an IntAct database synchronizer context");
        }
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getDatabaseSynchronizer() {
        if (this.databaseSynchronizer == null){
            this.databaseSynchronizer = new CvTermSynchronizer(this, IntactUtils.DATABASE_OBJCLASS);
        }
        return databaseSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getQualifierSynchronizer() {
        if (this.qualifierSynchronizer == null){
            this.qualifierSynchronizer = new CvTermSynchronizer(this, IntactUtils.QUALIFIER_OBJCLASS);
        }
        return qualifierSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getTopicSynchronizer() {
        if (this.topicSynchronizer == null){
            this.topicSynchronizer = new CvTermSynchronizer(this, IntactUtils.TOPIC_OBJCLASS);
        }
        return topicSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getAliasTypeSynchronizer() {
        if (this.aliasTypeSynchronizer == null){
            this.aliasTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.ALIAS_TYPE_OBJCLASS);
        }
        return aliasTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getUnitSynchronizer() {
        if (this.unitSynchronizer == null){
            this.unitSynchronizer = new CvTermSynchronizer(this, IntactUtils.UNIT_OBJCLASS);
        }
        return unitSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getFeatureTypeSynchronizer() {
        if (this.featureTypeSynchronizer == null){
            this.featureTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.FEATURE_TYPE_OBJCLASS);
        }
        return featureTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getExperimentalRoleSynchronizer() {
        if (this.experimentalRoleSynchronizer == null){
            this.experimentalRoleSynchronizer = new CvTermSynchronizer(this, IntactUtils.EXPERIMENTAL_ROLE_OBJCLASS);
        }
        return experimentalRoleSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getBiologicalRoleSynchronizer() {
        if (this.biologicalRoleSynchronizer == null){
            this.biologicalRoleSynchronizer = new CvTermSynchronizer(this, IntactUtils.BIOLOGICAL_ROLE_OBJCLASS);
        }
        return biologicalRoleSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getInteractionDetectionMethodSynchronizer() {
        if (this.interactionDetectionMethodSynchronizer == null){
            this.interactionDetectionMethodSynchronizer = new CvTermSynchronizer(this, IntactUtils.INTERACTION_DETECTION_METHOD_OBJCLASS);
        }
        return interactionDetectionMethodSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getInteractionTypeSynchronizer() {
        if (this.interactionTypeSynchronizer == null){
            this.interactionTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.INTERACTION_TYPE_OBJCLASS);
        }
        return interactionTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getParticipantDetectionMethodSynchronizer() {
        if (this.participantDetectionMethodSynchronizer == null){
            this.participantDetectionMethodSynchronizer = new CvTermSynchronizer(this, IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS);
        }
        return participantDetectionMethodSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getExperimentalPreparationSynchronizer() {
        if (this.experimentalPreparationSynchronizer == null){
            this.experimentalPreparationSynchronizer = new CvTermSynchronizer(this, IntactUtils.PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS);
        }
        return experimentalPreparationSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getInteractorTypeSynchronizer() {
        if (this.interactorTypeSynchronizer == null){
            this.interactorTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.INTERACTOR_TYPE_OBJCLASS);
        }
        return interactorTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getRangeStatusSynchronizer() {
        if (this.rangeStatusSynchronizer == null){
            this.rangeStatusSynchronizer = new CvTermSynchronizer(this, IntactUtils.RANGE_STATUS_OBJCLASS);
        }
        return rangeStatusSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getConfidenceTypeSynchronizer() {
        if (this.confidenceTypeSynchronizer == null){
            this.confidenceTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        }
        return confidenceTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getParameterTypeSynchronizer() {
        if (this.parameterTypeSynchronizer == null){
            this.parameterTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.PARAMETER_TYPE_OBJCLASS);
        }
        return parameterTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getCellTypeSynchronizer() {
        if (this.cellTypeSynchronizer == null){
            this.cellTypeSynchronizer = new CvTermSynchronizer(this, IntactUtils.CELL_TYPE_OBJCLASS);
        }
        return cellTypeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getTissueSynchronizer() {
        if (this.tissueSynchronizer == null){
            this.tissueSynchronizer = new CvTermSynchronizer(this, IntactUtils.TISSUE_OBJCLASS);
        }
        return tissueSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getFeatureDetectionMethodSynchronizer() {
        if (this.featureDetectionMethodSynchronizer == null){
            this.featureDetectionMethodSynchronizer = new CvTermSynchronizer(this, IntactUtils.FEATURE_METHOD_OBJCLASS);
        }
        return featureDetectionMethodSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getLifecycleStatusSynchronizer() {
        if (this.lifecycleStatusSynchronizer == null){
            this.lifecycleStatusSynchronizer = new CvTermSynchronizer(this, IntactUtils.PUBLICATION_STATUS_OBJCLASS);
        }
        return lifecycleStatusSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getLifecycleEventSynchronizer() {
        if (this.lifecycleEventSynchronizer == null){
            this.lifecycleEventSynchronizer = new CvTermSynchronizer(this, IntactUtils.LIFECYCLE_EVENT_OBJCLASS);
        }
        return lifecycleEventSynchronizer;
    }

    public <A extends AbstractIntactAlias> AliasSynchronizer<A> getAliasSynchronizer(Class<A> aliasclass) {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(aliasclass);
        return this.aliasSynchronizer;
    }

    public <A extends AbstractIntactAnnotation> AnnotationSynchronizer<A> getAnnotationSynchronizer(Class<A> annotationclass) {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(annotationclass);
        return this.annotationSynchronizer;
    }

    public <A extends AbstractIntactXref> XrefSynchronizer<A> getXrefSynchronizer(Class<A> xrefclass) {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(xrefclass);
        return this.xrefSynchronizer;
    }

    public <A extends AbstractIntactConfidence> ConfidenceSynchronizer<Confidence, A> getConfidenceSynchronizer(Class<A> confidenceclass) {
        initialiseConfidenceTemplateIfNotDone();
        this.confidenceSynchronizer.setIntactClass(confidenceclass);
        return this.confidenceSynchronizer;
    }

    public <A extends AbstractIntactParameter> ParameterSynchronizer<Parameter, A> getParameterSynchronizer(Class<A> parameterclass) {
        initialiseParameterTemplateIfNotDone();
        this.parameterSynchronizer.setIntactClass(parameterclass);
        return this.parameterSynchronizer;
    }

    public <A extends AbstractLifecycleEvent> LifecycleEventSynchronizer<A> getLifecycleSynchronizer(Class<A> eventclass) {
        initialiseLifecycleTemplateIfNotDone();
        this.lifecycleSynchronizer.setIntactClass(eventclass);
        return this.lifecycleSynchronizer;
    }

    public AliasSynchronizer<CvTermAlias> getCvAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(CvTermAlias.class);
        return this.aliasSynchronizer;
    }

    public XrefSynchronizer<CvTermXref> getCvXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(CvTermXref.class);
        return this.xrefSynchronizer;
    }

    public AnnotationSynchronizer<CvTermAnnotation> getCvAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(CvTermAnnotation.class);
        return this.annotationSynchronizer;
    }

    public IntactDbSynchronizer<Source, IntactSource> getSourceSynchronizer() {
        if (this.sourceSynchronizer == null){
            this.sourceSynchronizer = new SourceSynchronizer(this);
        }
        return sourceSynchronizer;
    }

    public AliasSynchronizer<SourceAlias> getSourceAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(SourceAlias.class);
        return this.aliasSynchronizer;
    }

    public XrefSynchronizer<SourceXref> getSourceXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(SourceXref.class);
        return this.xrefSynchronizer;
    }

    public AnnotationSynchronizer<SourceAnnotation> getSourceAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(SourceAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AliasSynchronizer<OrganismAlias> getOrganismAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(OrganismAlias.class);
        return this.aliasSynchronizer;
    }

    public AliasSynchronizer<FeatureEvidenceAlias> getFeatureEvidenceAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(FeatureEvidenceAlias.class);
        return this.aliasSynchronizer;
    }

    public AliasSynchronizer<ModelledFeatureAlias> getModelledFeatureAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(ModelledFeatureAlias.class);
        return this.aliasSynchronizer;
    }

    public AliasSynchronizer<ParticipantEvidenceAlias> getParticipantEvidenceAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(ParticipantEvidenceAlias.class);
        return this.aliasSynchronizer;
    }

    public AliasSynchronizer<ModelledParticipantAlias> getModelledParticipantAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(ModelledParticipantAlias.class);
        return this.aliasSynchronizer;
    }

    public AliasSynchronizer<InteractorAlias> getInteractorAliasSynchronizer() {
        initialiseAliasTemplateIfNotDone();
        this.aliasSynchronizer.setIntactClass(InteractorAlias.class);
        return this.aliasSynchronizer;
    }

    public XrefSynchronizer<PublicationXref> getPublicationXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(PublicationXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<ExperimentXref> getExperimentXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(ExperimentXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<InteractionXref> getInteractionXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(InteractionXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<InteractorXref> getInteractorXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(InteractorXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<FeatureEvidenceXref> getFeatureEvidenceXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(FeatureEvidenceXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<ModelledFeatureXref> getModelledFeatureXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(ModelledFeatureXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<ParticipantEvidenceXref> getParticipantEvidenceXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(ParticipantEvidenceXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<ModelledParticipantXref> getModelledParticipantXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(ModelledParticipantXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<ExperimentalResultingSequenceXref> getExperimentalResultingSequenceXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(ExperimentalResultingSequenceXref.class);
        return this.xrefSynchronizer;
    }

    public XrefSynchronizer<ModelledResultingSequenceXref> getModelledResultingSequenceXrefSynchronizer() {
        initialiseXrefTemplateIfNotDone();
        this.xrefSynchronizer.setIntactClass(ModelledResultingSequenceXref.class);
        return this.xrefSynchronizer;
    }

    public AnnotationSynchronizer<PublicationAnnotation> getPublicationAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(PublicationAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<ExperimentAnnotation> getExperimentAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(ExperimentAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<InteractionAnnotation> getInteractionAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(InteractionAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<InteractorAnnotation> getInteractorAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(InteractorAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<FeatureEvidenceAnnotation> getFeatureEvidenceAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(FeatureEvidenceAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<ModelledFeatureAnnotation> getModelledFeatureAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(ModelledFeatureAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<ParticipantEvidenceAnnotation> getParticipantEvidenceAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(ParticipantEvidenceAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<ModelledParticipantAnnotation> getModelledParticipantAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(ModelledParticipantAnnotation.class);
        return this.annotationSynchronizer;
    }

    public AnnotationSynchronizer<CooperativeEffectAnnotation> getCooperativeEffectAnnotationSynchronizer() {
        initialiseAnnotationTemplateIfNotDone();
        this.annotationSynchronizer.setIntactClass(CooperativeEffectAnnotation.class);
        return this.annotationSynchronizer;
    }

    public CooperativeEffectSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect> getCooperativeEffectSynchronizer() {
        if (this.cooperativeEffectSynchronizer == null){
           this.cooperativeEffectSynchronizer = new CompositeCooperativeEffectSynchronizer(this);
        }
        return cooperativeEffectSynchronizer;
    }

    public CooperativeEffectSynchronizer<Preassembly, IntactPreassembly> getPreAssemblySynchronizer() {
        if (this.preAssemblySynchronizer == null){
            this.preAssemblySynchronizer = new CooperativeEffectSynchronizerTemplate(this, IntactPreassembly.class);
        }
        return this.preAssemblySynchronizer;
    }

    public CooperativeEffectSynchronizer<Allostery, AbstractIntactAllostery> getAllosterySynchronizer() {
        if (this.allosterySynchronizer == null){
            this.allosterySynchronizer = new AllosterySynchronizer(this);
        }
        return this.allosterySynchronizer;
    }

    public InteractorSynchronizer<Complex, IntactComplex> getComplexSynchronizer() {
        if (this.complexSynchronizer == null){
           this.complexSynchronizer = new ComplexSynchronizer(this);
        }
        return this.complexSynchronizer;
    }

    public InteractorSynchronizer<Interactor, IntactInteractor> getInteractorSynchronizer() {
        if (this.interactorSynchronizer == null){
            this.interactorSynchronizer = new CompositeInteractorSynchronizer(this);
        }
        return this.interactorSynchronizer;
    }

    public InteractorSynchronizer<Interactor, IntactInteractor> getInteractorBaseSynchronizer() {
        initialiseInteractorTemplateIfNotDone();
        this.interactorBaseSynchronizer.setIntactClass(IntactInteractor.class);
        return this.interactorBaseSynchronizer;
    }

    public InteractorSynchronizer<Polymer, IntactPolymer> getPolymerSynchronizer() {
        initialisePolymerTemplateIfNotDone();
        this.polymerSynchronizer.setIntactClass(IntactPolymer.class);
        return this.polymerSynchronizer;
    }

    public InteractorSynchronizer<Protein, IntactProtein> getProteinSynchronizer() {
        initialisePolymerTemplateIfNotDone();
        this.polymerSynchronizer.setIntactClass(IntactProtein.class);
        return this.polymerSynchronizer;
    }

    public InteractorSynchronizer<NucleicAcid, IntactNucleicAcid> getNucleicAcidSynchronizer() {
        initialisePolymerTemplateIfNotDone();
        this.polymerSynchronizer.setIntactClass(IntactNucleicAcid.class);
        return this.polymerSynchronizer;
    }

    public InteractorSynchronizer<Molecule, IntactMolecule> getMoleculeSynchronizer() {
        initialiseInteractorTemplateIfNotDone();
        this.interactorBaseSynchronizer.setIntactClass(IntactMolecule.class);
        return this.interactorBaseSynchronizer;
    }

    public InteractorSynchronizer<Gene, IntactGene> getGeneSynchronizer() {
        initialiseInteractorTemplateIfNotDone();
        this.interactorBaseSynchronizer.setIntactClass(IntactGene.class);
        return this.interactorBaseSynchronizer;
    }

    public InteractorSynchronizer<InteractorPool, IntactInteractorPool> getInteractorPoolSynchronizer() {
        if (this.interactorPoolSynchronizer == null){
            this.interactorPoolSynchronizer = new InteractorPoolSynchronizer(this);
        }
        return this.interactorPoolSynchronizer;
    }

    public InteractorSynchronizer<BioactiveEntity, IntactBioactiveEntity> getBioactiveEntitySynchronizer() {
        initialiseInteractorTemplateIfNotDone();
        this.interactorBaseSynchronizer.setIntactClass(IntactBioactiveEntity.class);
        return this.interactorBaseSynchronizer;
    }

    public <T extends AbstractIntactCausalRelationship> IntactDbSynchronizer<CausalRelationship, T> getCausalRelationshipSynchronizer(Class<T> intactClass) {
        initializeCausalRelationshipSynchronizerIfNotDone();
        this.causalRelationshipSynchronizer.setIntactClass(intactClass);
        return this.causalRelationshipSynchronizer;
    }

    public IntactDbSynchronizer<CausalRelationship, ModelledCausalRelationship> getModelledCausalRelationshipSynchronizer() {
        return getCausalRelationshipSynchronizer(ModelledCausalRelationship.class);
    }

    public IntactDbSynchronizer<CausalRelationship, ExperimentalCausalRelationship> getExperimentalCausalRelationshipSynchronizer() {
        return getCausalRelationshipSynchronizer(ExperimentalCausalRelationship.class);
    }

    public ConfidenceSynchronizer<Confidence, InteractionEvidenceConfidence> getInteractionConfidenceSynchronizer() {
        initialiseConfidenceTemplateIfNotDone();
        this.confidenceSynchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        return this.confidenceSynchronizer;
    }

    public ConfidenceSynchronizer<ModelledConfidence, ComplexConfidence> getComplexConfidenceSynchronizer() {
        initialiseConfidenceTemplateIfNotDone();
        this.confidenceSynchronizer.setIntactClass(ComplexConfidence.class);
        return this.confidenceSynchronizer;
    }

    public ConfidenceSynchronizer<Confidence, ParticipantEvidenceConfidence> getEntityConfidenceSynchronizer() {
        initialiseConfidenceTemplateIfNotDone();
        this.confidenceSynchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        return this.confidenceSynchronizer;
    }

    public ParameterSynchronizer<Parameter, InteractionEvidenceParameter> getInteractionParameterSynchronizer(){
        initialiseParameterTemplateIfNotDone();
        this.parameterSynchronizer.setIntactClass(InteractionEvidenceParameter.class);
        return this.parameterSynchronizer;
    }

    public ParameterSynchronizer<Parameter, ParticipantEvidenceParameter> getEntityParameterSynchronizer(){
        initialiseParameterTemplateIfNotDone();
        this.parameterSynchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        return this.parameterSynchronizer;
    }

    public ParameterSynchronizer<ModelledParameter, ComplexParameter> getComplexParameterSynchronizer() {
        initialiseParameterTemplateIfNotDone();
        this.parameterSynchronizer.setIntactClass(ComplexParameter.class);
        return this.parameterSynchronizer;
    }

    public IntactDbSynchronizer<Organism, IntactOrganism> getOrganismSynchronizer() {
        if (this.organismSynchronizer == null){
            this.organismSynchronizer = new OrganismSynchronizer(this);
        }
        return organismSynchronizer;
    }

    public <I extends AbstractIntactRange> IntactDbSynchronizer<Range, I> getRangeSynchronizer(Class<I> intactClass) {
        if (this.rangeSynchronizer == null){
            this.rangeSynchronizer = new RangeSynchronizerTemplate(this, AbstractIntactRange.class);
        }
        this.rangeSynchronizer.setIntactClass(intactClass);
        return rangeSynchronizer;
    }

    public IntactDbSynchronizer<Range, ModelledRange> getModelledRangeSynchronizer() {
        return getRangeSynchronizer(ModelledRange.class);
    }

    public IntactDbSynchronizer<Range, ExperimentalRange> getExperimentalRangeSynchronizer() {
        return getRangeSynchronizer(ExperimentalRange.class);
    }

    public IntactDbSynchronizer<Preference, Preference> getPreferenceSynchronizer() {
        if (this.preferenceSynchronizer == null){
            this.preferenceSynchronizer = new PreferenceSynchronizer(this);
        }
        return preferenceSynchronizer;
    }

    public IntactDbSynchronizer<Role, Role> getRoleSynchronizer() {
        if (this.roleSynchronizer == null){
            this.roleSynchronizer = new RoleSynchronizer(this);
        }
        return roleSynchronizer;
    }

    public IntactDbSynchronizer<User, User> getUserSynchronizer() {
        if (this.userSynchronizer == null){
            this.userSynchronizer = new UserSynchronizer(this);
        }
        return userSynchronizer;
    }

    public IntactDbSynchronizer<Publication, IntactPublication> getSimplePublicationSynchronizer() {
        if (this.simplePublicationSynchronizer == null){
            this.simplePublicationSynchronizer = new PublicationSynchronizer<IntactPublication>(this, IntactPublication.class);
        }
        return simplePublicationSynchronizer;
    }

    public IntactDbSynchronizer<Publication, IntactCuratedPublication> getPublicationSynchronizer() {
        if (this.publicationSynchronizer == null){
            this.publicationSynchronizer = new CuratedPublicationSynchronizer(this);
        }
        return publicationSynchronizer;
    }

    public IntactDbSynchronizer<Experiment, IntactExperiment> getExperimentSynchronizer() {
        if (this.experimentSynchronizer == null){
            this.experimentSynchronizer = new ExperimentSynchronizer(this);
        }
        return experimentSynchronizer;
    }

    public IntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence> getInteractionSynchronizer() {
        if (this.interactionEvidenceSynchronizer == null){
            this.interactionEvidenceSynchronizer = new InteractionEvidenceSynchronizer(this);
        }
        return interactionEvidenceSynchronizer;
    }

    public IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> getCooperativityEvidenceSynchronizer() {
        if (this.cooperativityEvidenceSynchronizer == null){
            this.cooperativityEvidenceSynchronizer = new CooperativityEvidenceSynchronizer(this);
        }
        return cooperativityEvidenceSynchronizer;
    }

    public IntactDbSynchronizer<LifeCycleEvent, ComplexLifecycleEvent> getComplexLifecycleSynchronizer() {
        initialiseLifecycleTemplateIfNotDone();
        this.lifecycleSynchronizer.setIntactClass(ComplexLifecycleEvent.class);
        return this.lifecycleSynchronizer;
    }

    public IntactDbSynchronizer<LifeCycleEvent, PublicationLifecycleEvent> getPublicationLifecycleSynchronizer() {
        initialiseLifecycleTemplateIfNotDone();
        this.lifecycleSynchronizer.setIntactClass(PublicationLifecycleEvent.class);
        return this.lifecycleSynchronizer;
    }

    public IntactDbSynchronizer<Feature, AbstractIntactFeature> getFeatureSynchronizer(){
        if (this.featureSynchronizer == null){
            this.featureSynchronizer = new CompositeFeatureSynchronizer(this);
        }
        return this.featureSynchronizer;
    }

    public IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> getFeatureEvidenceSynchronizer() {
        if (this.featureEvidenceSynchronizer == null){
            this.featureEvidenceSynchronizer = new FeatureEvidenceSynchronizer(this);
        }
        return featureEvidenceSynchronizer;
    }

    public IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> getModelledFeatureSynchronizer() {
        if (this.modelledFeatureSynchronizer == null){
            this.modelledFeatureSynchronizer = new FeatureSynchronizerTemplate<ModelledFeature, IntactModelledFeature>(this, IntactModelledFeature.class);
        }
        return modelledFeatureSynchronizer;
    }

    public IntactDbSynchronizer<VariableParameter, IntactVariableParameter> getVariableParameterSynchronizer() {
        if (this.variableParameterSynchronizer == null){
            this.variableParameterSynchronizer = new VariableParameterSynchronizer(this);
        }
        return variableParameterSynchronizer;
    }

    public IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> getVariableParameterValueSynchronizer() {
        if (this.variableParameterValueSynchronizer == null){
           this.variableParameterValueSynchronizer = new VariableParameterValueSynchronizer(this);
        }
        return variableParameterValueSynchronizer;
    }

    public IntactDbSynchronizer<VariableParameterValueSet, IntactVariableParameterValueSet> getVariableParameterValueSetSynchronizer() {
        if (this.variableParameterValueSetSynchronizer == null){
            this.variableParameterValueSetSynchronizer = new VariableParameterValueSetSynchronizer(this);
        }
        return variableParameterValueSetSynchronizer;
    }

    public ParticipantSynchronizer<Participant, AbstractIntactParticipant> getParticipantSynchronizer() {
        if (this.participantSynchronizer == null){
            this.participantSynchronizer = new CompositeParticipantSynchronizer(this);
        }
        return participantSynchronizer;
    }

    public ParticipantSynchronizer<ModelledParticipant, IntactModelledParticipant> getModelledParticipantSynchronizer() {
        if (this.modelledParticipantSynchronizer == null){
            this.modelledParticipantSynchronizer = new ModelledParticipantSynchronizerTemplate<ModelledParticipant, IntactModelledParticipant>(this, IntactModelledParticipant.class);
        }
        return modelledParticipantSynchronizer;
    }

    public ParticipantSynchronizer<ModelledParticipantPool, IntactModelledParticipantPool> getModelledParticipantPoolSynchronizer(){
        if (this.modelledParticipantPoolSynchronizer == null){
            this.modelledParticipantPoolSynchronizer = new ModelledParticipantPoolSynchronizer(this);
        }
        return this.modelledParticipantPoolSynchronizer;
    }

    public ParticipantSynchronizer<ParticipantEvidence, IntactParticipantEvidence> getParticipantEvidenceSynchronizer(){
        if (this.participantEvidenceSynchronizer == null){
            this.participantEvidenceSynchronizer = new ParticipantEvidenceSynchronizerTemplate<ParticipantEvidence, IntactParticipantEvidence>(this, IntactParticipantEvidence.class);
        }
        return participantEvidenceSynchronizer;
    }

    public ParticipantSynchronizer<ParticipantEvidencePool, IntactParticipantEvidencePool> getParticipantEvidencePoolSynchronizer(){
        if (this.ParticipantEvidencePoolSynchronizer == null){
            this.ParticipantEvidencePoolSynchronizer = new uk.ac.ebi.intact.jami.synchronizer.impl.ParticipantEvidencePoolSynchronizer(this);
        }
        return this.ParticipantEvidencePoolSynchronizer;
    }

    public void clearCache() {
        clearCache(this.databaseSynchronizer);
        clearCache(this.qualifierSynchronizer);
        clearCache(this.topicSynchronizer);
        clearCache(this.aliasTypeSynchronizer);
        clearCache(this.unitSynchronizer);
        clearCache(this.featureTypeSynchronizer);
        clearCache(this.experimentalRoleSynchronizer);
        clearCache(this.biologicalRoleSynchronizer);
        clearCache(this.interactionDetectionMethodSynchronizer);
        clearCache(this.interactionTypeSynchronizer);
        clearCache(this.participantDetectionMethodSynchronizer);
        clearCache(this.experimentalPreparationSynchronizer);
        clearCache(this.interactorTypeSynchronizer);
        clearCache(this.rangeStatusSynchronizer);
        clearCache(this.confidenceTypeSynchronizer);
        clearCache(this.parameterTypeSynchronizer);
        clearCache(this.cellTypeSynchronizer);
        clearCache(this.tissueSynchronizer);
        clearCache(this.featureDetectionMethodSynchronizer);
        clearCache(this.lifecycleStatusSynchronizer);
        clearCache(this.lifecycleEventSynchronizer);
        clearCache(this.sourceSynchronizer);
        clearCache(this.aliasSynchronizer);
        clearCache(this.annotationSynchronizer);
        clearCache(this.xrefSynchronizer);
        clearCache(this.cooperativeEffectSynchronizer);
        clearCache(this.preAssemblySynchronizer);
        clearCache(this.allosterySynchronizer);
        clearCache(this.interactorSynchronizer);
        clearCache(this.interactorBaseSynchronizer);
        clearCache(this.polymerSynchronizer);
        clearCache(this.complexSynchronizer);
        clearCache(this.interactorPoolSynchronizer);
        clearCache(this.causalRelationshipSynchronizer);
        clearCache(this.confidenceSynchronizer);
        clearCache(this.parameterSynchronizer);
        clearCache(this.organismSynchronizer);
        clearCache(this.rangeSynchronizer);
        clearCache(this.preferenceSynchronizer);
        clearCache(this.roleSynchronizer);
        clearCache(this.userSynchronizer);
        clearCache(this.publicationSynchronizer);
        clearCache(this.experimentSynchronizer);
        clearCache(this.interactionEvidenceSynchronizer);
        clearCache(this.cooperativityEvidenceSynchronizer);
        clearCache(this.lifecycleSynchronizer);
        clearCache(this.featureSynchronizer);
        clearCache(this.featureEvidenceSynchronizer);
        clearCache(this.modelledFeatureSynchronizer);
        clearCache(this.variableParameterSynchronizer);
        clearCache(this.variableParameterValueSynchronizer);
        clearCache(this.variableParameterValueSetSynchronizer);
        clearCache(this.participantSynchronizer);
        clearCache(this.modelledParticipantSynchronizer);
        clearCache(this.modelledParticipantPoolSynchronizer);
        clearCache(this.participantEvidenceSynchronizer);
        clearCache(this.ParticipantEvidencePoolSynchronizer);
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getGeneralCvSynchronizer() {
        if (this.generalCvSynchronizer == null){
            this.generalCvSynchronizer = new CvTermSynchronizer(this);
        }
        ((CvTermSynchronizer)this.generalCvSynchronizer).setObjClass(null);
        return this.generalCvSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getCvSynchronizer(String objclass) {
        if (this.generalCvSynchronizer == null){
            this.generalCvSynchronizer = new CvTermSynchronizer(this);
        }
        ((CvTermSynchronizer)this.generalCvSynchronizer).setObjClass(objclass);
        return this.generalCvSynchronizer;
    }

    private void initialiseAliasTemplateIfNotDone() {
        if (this.aliasSynchronizer == null){
            this.aliasSynchronizer = new AliasSynchronizerTemplate(this, AbstractIntactAlias.class);
        }
    }

    private void initialiseInteractorTemplateIfNotDone() {
        if (this.interactorBaseSynchronizer == null){
            this.interactorBaseSynchronizer = new InteractorSynchronizerTemplate(this, IntactInteractor.class);
        }
    }

    private void initialisePolymerTemplateIfNotDone() {
        if (this.polymerSynchronizer == null){
            this.polymerSynchronizer = new PolymerSynchronizerTemplate(this, IntactPolymer.class);
        }
    }

    private void initialiseAnnotationTemplateIfNotDone() {
        if (this.annotationSynchronizer == null){
            this.annotationSynchronizer = new AnnotationSynchronizerTemplate(this, AbstractIntactAnnotation.class);
        }
    }

    private void initialiseXrefTemplateIfNotDone() {
        if (this.xrefSynchronizer == null){
            this.xrefSynchronizer = new XrefSynchronizerTemplate(this, AbstractIntactXref.class);
        }
    }

    private void initialiseConfidenceTemplateIfNotDone() {
        if (this.confidenceSynchronizer == null){
            this.confidenceSynchronizer = new ConfidenceSynchronizerTemplate(this, AbstractIntactConfidence.class);
        }
    }

    private void initialiseParameterTemplateIfNotDone() {
        if (this.parameterSynchronizer == null){
            this.parameterSynchronizer = new ParameterSynchronizerTemplate(this, AbstractIntactParameter.class);
        }
    }

    private void initialiseLifecycleTemplateIfNotDone() {
        if (this.lifecycleSynchronizer == null){
            this.lifecycleSynchronizer = new LifeCycleSynchronizerTemplate(this, AbstractLifecycleEvent.class);
        }
    }

    private void initializeCausalRelationshipSynchronizerIfNotDone() {
        if (this.causalRelationshipSynchronizer == null){
            this.causalRelationshipSynchronizer = new CausalRelationchipSynchronizerTemplate(this, AbstractIntactCausalRelationship.class);
        }
    }

    private void clearCache(IntactDbSynchronizer delegate){
        if (delegate != null){
            delegate.clearCache();
        }
    }
}
