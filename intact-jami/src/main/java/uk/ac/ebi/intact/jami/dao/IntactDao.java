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

    public CvTermDao getCvTermDao();

    public SourceDao getSourceDao();

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

    public OrganismDao getOrganismDao();

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

    public <T extends AbstractIntactParticipant> ParticipantDao<T> getParticipantDao(Class<T> entityClass);

    public ModelledParticipantDao<IntactModelledParticipant> getModelledParticipantDao();

    public ParticipantEvidenceDao<IntactParticipantEvidence> getParticipantEvidenceDao();
}
