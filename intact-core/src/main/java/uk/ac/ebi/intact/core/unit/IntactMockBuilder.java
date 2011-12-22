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
package uk.ac.ebi.intact.core.unit;

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.*;
import uk.ac.ebi.intact.util.Crc64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * Simulates populated IntAct model objects.
 * <p/>
 * <b>Useful when testing !!</b>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @since 1.6.1
 */
public class IntactMockBuilder {

    private static int MIN_CHILDREN = 2;
    private static int MAX_CHILDREN = 10;

    int sequence = 0;

    private Institution institution;
    private CvObjectBuilder cvBuilder;

    public IntactMockBuilder() {
        institution = createInstitution(Institution.INTACT_REF, Institution.INTACT);
    }

    public IntactMockBuilder(Institution institution) {
        this.institution = institution;
        this.cvBuilder = new CvObjectBuilder();
    }

    /////////////////////
    // Institution

    public Institution getInstitution() {
        return institution;
    }

    public Institution createInstitution(String miRef, String shortLabel) {
        this.cvBuilder = new CvObjectBuilder();
        institution = new Institution(shortLabel);

        InstitutionXref xref = createIdentityXrefPsiMi(institution, miRef);
        institution.addXref(xref);

        return institution;
    }

    //////////////////////
    // CvObjects

    public CvDatabase getPsiMiDatabase() {
        return cvBuilder.createPsiMiCvDatabase(getInstitution());
    }

    public CvXrefQualifier getIdentityQualifier() {
        return cvBuilder.createIdentityCvXrefQualifier(getInstitution());
    }

    public <T extends CvObject> T createCvObject(Class<T> cvClass, String primaryId, String shortLabel) {
        return CvObjectUtils.createCvObject(getInstitution(), cvClass, primaryId, shortLabel);
    }

    /////////////////////
    // Xref

    public <X extends Xref> X createIdentityXrefPsiMiRandom(AnnotatedObject<X,?> parent) {
        return createIdentityXrefPsiMi(parent, nextString("primId"));
    }

    public <X extends Xref> X createIdentityXrefPsiMi(AnnotatedObject<X,?> parent, String primaryId) {
        return createIdentityXref(parent, primaryId, getPsiMiDatabase());
    }

    public <X extends Xref> X createIdentityXrefUniprot(AnnotatedObject<X,?> parent, String primaryId) {
        return XrefUtils.createIdentityXrefUniprot(parent, primaryId);
    }

    public <X extends Xref> X createIdentityXrefChebi(AnnotatedObject<X,?> parent, String chebiId) {
        return XrefUtils.createIdentityXrefChebi(parent, chebiId);
    }

    public <X extends Xref> X createIdentityXrefEmblGenbankDdbj(AnnotatedObject<X,?> parent, String emblGenbankDdbjId) {
        return XrefUtils.createIdentityXrefEmblGenbankDdbj(parent, emblGenbankDdbjId);
    }

    public <X extends Xref> X createIdentityXref(AnnotatedObject<X,?> parent, String primaryId, CvDatabase cvDatabase) {
        return XrefUtils.createIdentityXref(parent, primaryId, getIdentityQualifier(), cvDatabase);
    }

    public <X extends Xref> X createPrimaryReferenceXref(AnnotatedObject<X,?> parent, String primaryId) {
        CvXrefQualifier primaryReference = createCvObject(CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF, CvXrefQualifier.PRIMARY_REFERENCE);
        CvDatabase pubmedDb = createCvObject(CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED);

        return createXref(parent, primaryId, primaryReference, pubmedDb);
    }

    public <X extends Xref> X createXref(AnnotatedObject<X,?> parent, String primaryId, CvXrefQualifier cvXrefQualifer, CvDatabase cvDatabase) {
        X xref = (X) XrefUtils.newXrefInstanceFor(parent.getClass());
        xref.setOwner(parent.getOwner());
        xref.setParent(parent);
        xref.setPrimaryId(primaryId);
        xref.setCvXrefQualifier(cvXrefQualifer);
        xref.setCvDatabase(cvDatabase);

        return xref;
    }

    //////////////////
    // BioSource

    public BioSource createBioSourceRandom() {
        return createBioSource(nextId(), nextString("label"));
    }

    public BioSource createDeterministicBioSource() {
        return createBioSource(9606, "human");
    }

    public BioSource createBioSource(int taxId, String shortLabel) {
        BioSource bioSource = new BioSource(getInstitution(), shortLabel, String.valueOf(taxId));

        CvDatabase newt = createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
        BioSourceXref newtXref = createIdentityXref(bioSource, String.valueOf(taxId), newt);
        bioSource.addXref(newtXref);
        
        return bioSource;
    }

    //////////////////
    // Alias

    public <A extends Alias> A createAliasGeneName(AnnotatedObject<?,A> parent, String name) {
        return AliasUtils.createAliasGeneName(parent, name);
    }

    public <A extends Alias> A createAlias(AnnotatedObject<?,A> parent, String name, CvAliasType type) {
        return AliasUtils.createAlias(parent, name, type );
    }

    public <A extends Alias> A createAlias(AnnotatedObject<?,A> parent, String name, String typeIdentity, String type) {
        return AliasUtils.createAlias(parent, name, createCvObject( CvAliasType.class, typeIdentity, type ) );
    }

    ///////////////////////
    // Nucleic Acid

    public NucleicAcid createNucleicAcidRandom() {
        return createNucleicAcid( nextString( ), createBioSourceRandom(), nextString( "NA-" ));
    }

    public NucleicAcid createNucleicAcid( String emblGenbankDdbjId, BioSource biosource, String shortlabel ) {
        CvInteractorType type = createCvObject(CvInteractorType.class,
                                               CvInteractorType.NUCLEIC_ACID_MI_REF,
                                               CvInteractorType.NUCLEIC_ACID);

        NucleicAcid na = new NucleicAcidImpl(getInstitution(), biosource, shortlabel, type);
        InteractorXref idXref = createIdentityXrefEmblGenbankDdbj(na, emblGenbankDdbjId);
        na.addXref(idXref);

        InteractorAlias alias = createAliasGeneName(na, shortlabel.toUpperCase());
        na.addAlias(alias);

        return na;
    }

    //////////////////////
    // Small Molecule

    public SmallMolecule createSmallMoleculeRandom() {
          return createSmallMolecule(nextString("chebi:"), nextString());
    }

    public SmallMolecule createSmallMolecule(String chebiId, String shortLabel) {
         CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.SMALL_MOLECULE_MI_REF,
                                                   CvInteractorType.SMALL_MOLECULE);

        SmallMolecule smallMolecule = new SmallMoleculeImpl(shortLabel, getInstitution(), intType);
        InteractorXref idXref = createIdentityXrefChebi(smallMolecule, chebiId);
        smallMolecule.addXref(idXref);

        InteractorAlias alias = createAliasGeneName(smallMolecule, shortLabel.toUpperCase());
        smallMolecule.addAlias(alias);

        return smallMolecule;
    }

    //////////////////////
    // Protein

    public Protein createProteinRandom() {
        return createProtein(nextString("primId"), nextString(), createBioSourceRandom());
    }

    public Protein createPeptideRandom() {
        return createPeptide(nextString("primId"), nextString(), createBioSourceRandom());
    }

    public Protein createProtein(String uniprotId, String shortLabel, BioSource bioSource) {
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);

        return createProtein(uniprotId, shortLabel, bioSource, intType);
    }

    private Protein createProtein(String uniprotId, String shortLabel, BioSource bioSource, CvInteractorType intType) {
        Protein protein = new ProteinImpl(getInstitution(), bioSource, shortLabel, intType);
        InteractorXref idXref = createIdentityXrefUniprot(protein, uniprotId);
        protein.addXref(idXref);

        InteractorAlias alias = createAliasGeneName(protein, shortLabel.toUpperCase());
        protein.addAlias(alias);

        String sequence = randomPeptideSequence();
        String crc64 = Crc64.getCrc64(sequence);
        protein.setSequence(sequence);
        protein.setCrc64(crc64);
        return protein;
    }

    public Protein createPeptide(String uniprotId, String shortLabel, BioSource bioSource) {
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.PEPTIDE_MI_REF, "peptide");

        return createProtein(uniprotId, shortLabel, bioSource, intType);
    }

    public Protein createProtein(String uniprotId, String shortLabel) {
        return createProtein(uniprotId, shortLabel, createBioSourceRandom());
    }

    public Protein createProteinSpliceVariant(Protein masterProt, String uniprotId, String shortLabel) {
        Protein spliceVariant = createProtein(uniprotId, shortLabel);

        if (masterProt.getAc() == null) {
            throw new IllegalArgumentException("Cannot create an splice variant if the master protein does not have an AC: "+masterProt.getShortLabel());
        }

        CvXrefQualifier isoformParent = createCvObject(CvXrefQualifier.class, CvXrefQualifier.ISOFORM_PARENT_MI_REF, CvXrefQualifier.ISOFORM_PARENT);
        CvDatabase uniprotKb = createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        InteractorXref isoformXref = createXref(spliceVariant, masterProt.getAc(), isoformParent, uniprotKb);
        spliceVariant.addXref(isoformXref);

        return spliceVariant;
    }

    public Protein createProteinChain(Protein masterProt, String uniprotId, String shortLabel) {
        Protein chain = createProtein(uniprotId, shortLabel);

        if (masterProt.getAc() == null) {
            throw new IllegalArgumentException("Cannot create a chain if the master protein does not have an AC: "+masterProt.getShortLabel());
        }

        CvXrefQualifier chainParent = createCvObject(CvXrefQualifier.class, CvXrefQualifier.CHAIN_PARENT_MI_REF, CvXrefQualifier.CHAIN_PARENT);
        CvDatabase uniprotKb = createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        InteractorXref isoformXref = createXref(chain, masterProt.getAc(), chainParent, uniprotKb);
        chain.addXref(isoformXref);

        return chain;
    }

    public Protein createDeterministicProtein(String uniprotId, String shortLabel) {
        return createProtein(uniprotId, shortLabel, createDeterministicBioSource());
    }

    //////////////////////
    // Component

    public Component createComponent(Interaction interaction, Interactor interactor, CvExperimentalRole expRole, CvBiologicalRole bioRole) {
        Component component = new Component(getInstitution(), interaction, interactor, expRole, bioRole);

        CvIdentification cvParticipantDetMethod = createCvObject(CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED);
        component.getParticipantDetectionMethods().add(cvParticipantDetMethod);

        CvExperimentalPreparation cvExperimentalPreparation = createCvObject(CvExperimentalPreparation.class, CvExperimentalPreparation.PURIFIED_REF, CvExperimentalPreparation.PURIFIED);
        component.getExperimentalPreparations().add(cvExperimentalPreparation);
        
        ComponentParameter componentParameter = createDeterministicComponentParameter();
        component.addParameter(componentParameter);
        
        interactor.addActiveInstance( component );
        interaction.addComponent( component );

        return component;
    }

    public Component createComponentNeutral(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        Component component = createComponent(interaction, interactor, expRole, bioRole);

        for (int i=0; i<childRandom(0,2); i++) {
            component.addBindingDomain(createFeatureRandom());
        }
        
        return component;
    }

    public Component createComponentNeutral( Interactor interactor ) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION );
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(createExperimentEmpty())),
                                                      cvInteractionType, intType, nextString("label"), getInstitution());

        Component component = createComponent(interaction, interactor, expRole, bioRole);

        for (int i=0; i<childRandom(0,2); i++) {
            component.addBindingDomain(createFeatureRandom());
        }

        return component;
    }

    public Component createComponentBait(Interactor interactor) {
        Interaction interaction = createInteractionDirect();
        return createComponentBait(interaction, interactor);
    }

    public Component createComponentBait(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return createComponent(interaction, interactor, expRole, bioRole);
    }

    public Component createComponentPrey(Interactor interactor) {
        Interaction interaction = createInteractionDirect();
        return createComponentPrey(interaction, interactor);
    }

    private Interaction createInteractionDirect() {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION );
        return new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(createExperimentEmpty())),
                                                      cvInteractionType, intType, nextString("label"), getInstitution());
    }

    public Component createComponentRandom() {
        return createInteractionRandomBinary().getComponents().iterator().next();
    }

    public Component createComponentPrey(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return createComponent(interaction, interactor, expRole, bioRole);
    }

    //////////////////////
    // Interaction

    public Interaction createInteraction(String shortLabel, Interactor bait, Interactor prey, Experiment experiment) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION );
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION );
        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(experiment)),
                                                      cvInteractionType, intType, shortLabel, getInstitution());
        createComponentBait(interaction, bait);
        createComponentPrey(interaction, prey);

        return interaction;
    }

    public Interaction createInteraction(Component ... components) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        Experiment experiment = createExperimentEmpty();

        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION );
        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(experiment)),
                                                      cvInteractionType, intType, "temp", getInstitution());

        for (Component component : components) {
            interaction.addComponent(component);
        }

        try {
            String shortLabel = InteractionUtils.calculateShortLabel(interaction);
            interaction.setShortLabel(shortLabel);
        } catch (Exception e) {
            interaction.setShortLabel("unk-unk");
        }

        return interaction;
    }

     public Interaction createInteraction(Interactor ... interactors) {
         CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);
         Experiment experiment = createExperimentEmpty();
         CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION );
         Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(experiment)),
                                                       cvInteractionType, intType, "temp", getInstitution());

         Component[] components = new Component[interactors.length];

         for (int i=0; i<interactors.length; i++) {
             components[i] = createComponentNeutral(interaction, interactors[i]);
             components[i].setInteraction(null);
         }

         return createInteraction(components);
    }

    public Interaction createInteractionRandomBinary() {
        return createInteractionRandomBinary(null);
    }

    public Interaction createInteractionRandomBinary(String imexId) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION );
        Experiment experimentEmpty = createExperimentEmpty();
        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(experimentEmpty)),
                                                      cvInteractionType, intType, nextString("label"), getInstitution());
        experimentEmpty.getInteractions().add(interaction);

        createComponentBait(interaction, createProteinRandom());
        createComponentPrey(interaction, createProteinRandom());

        String shortLabel = InteractionUtils.calculateShortLabel(interaction);
        interaction.setShortLabel(shortLabel);

        if (imexId != null) {
            CvObjectBuilder cvBuilder = new CvObjectBuilder();
            CvXrefQualifier idQual = cvBuilder.createIdentityCvXrefQualifier(getInstitution());
            CvDatabase imexDb = createCvObject(CvDatabase.class, CvDatabase.IMEX_MI_REF, CvDatabase.IMEX);
            interaction.addXref(new InteractorXref(getInstitution(), imexDb, imexId, idQual));
        }

        return interaction;
    }
    
    public Interaction createInteraction(String ... interactorShortLabels) {
        Interaction interaction = createInteractionDirect();

        Protein prot = null;
        for (String interactorShortLabel : interactorShortLabels) {
            prot = createDeterministicProtein(interactorShortLabel, interactorShortLabel);
            createComponentNeutral(interaction, prot);
        }

        if (interactorShortLabels.length == 1) {
            createComponentNeutral(interaction, prot);
        }

        String shortLabel = InteractionUtils.calculateShortLabel(interaction);
        interaction.setShortLabel(shortLabel);

        return interaction;
    }

    /**
     * This creates a stable (non-random) interaction
     */
    public Interaction createDeterministicInteraction() {
        Interaction interaction = createInteraction("fooprey-barbait",
                                                    createDeterministicProtein("A2", "barbait"),
                                                    createDeterministicProtein("A1", "fooprey"),
                                                    createDeterministicExperiment());
        interaction.getAnnotations().add(createAnnotation("This is an annotation", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT));

        CvFeatureType featureType = createCvObject(CvFeatureType.class, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, CvFeatureType.EXPERIMENTAL_FEATURE);
        Feature feature = createFeature("feature1", featureType);
        feature.setComponent(null);

        Range range = createRange(1, 1, 5, 5);
        feature.addRange(range);

        interaction.getComponents().iterator().next().addBindingDomain(feature);
 //       interaction.getComponents().iterator().next().addComponentParameter(createDeterministicComponentParameter());
 //       interaction.addXref(createPrimaryReferenceXref( interaction , "testreference"));
        interaction.addConfidence( createDeterministicConfidence());
        interaction.addParameter( createDeterministicInteractionParameter() );

        return interaction;
    }

    ////////////////////
    // Experiment

    /**
     * This creates a stable (non-random) experiment
     */
    public Experiment createDeterministicExperiment() {
        Experiment experiment = createExperimentEmpty("foobar-2006-1","12345");
        experiment.setBioSource(createBioSource(5, "lalaorg"));

        return experiment;
    }

    public Experiment createExperimentEmpty() {
         return createExperimentEmpty(randomExperimentLabel());
     }

    public Experiment createExperimentEmpty(String shortLabel) {
         Experiment experiment = new Experiment(getInstitution(), shortLabel, createBioSourceRandom());

        experiment.setCvInteraction(createCvObject(CvInteraction.class, CvInteraction.COSEDIMENTATION_MI_REF, CvInteraction.COSEDIMENTATION));
        experiment.setCvIdentification(createCvObject(CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED));

        final Publication publication = createPublicationRandom();
        experiment.setPublication(publication);
        publication.addExperiment(experiment);
        experiment.addXref(createPrimaryReferenceXref(experiment, experiment.getPublication().getShortLabel()));

        return experiment;
    }

    public Experiment createExperimentEmpty(String shortLabel, String pubId) {
         Experiment experiment = new Experiment(getInstitution(), shortLabel, createBioSourceRandom());

        experiment.setCvInteraction(createCvObject(CvInteraction.class, CvInteraction.COSEDIMENTATION_MI_REF, CvInteraction.COSEDIMENTATION));
        experiment.setCvIdentification(createCvObject(CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED));

        Publication publication = createPublication(pubId);
        publication.addExperiment(experiment);
        experiment.addXref(createPrimaryReferenceXref(experiment, pubId));

        return experiment;
    }

    public Experiment createExperimentRandom(int interactionNumber) {
        Experiment exp = createExperimentEmpty(randomExperimentLabel());

        for (int i=0; i<interactionNumber; i++) {
            Interaction interaction = createInteractionRandomBinary();
            interaction.setExperiments(new ArrayList<Experiment>(Arrays.asList(exp)));
            exp.addInteraction(interaction);
        }

        return exp;
    }

    public Experiment createExperimentRandom(String shortLabel, int interactionNumber) {
        Experiment exp = createExperimentEmpty(randomExperimentLabel());
        exp.setShortLabel(shortLabel);

        for (int i=0; i<interactionNumber; i++) {
            Interaction interaction = createInteractionRandomBinary();
            interaction.setExperiments(new ArrayList<Experiment>(Arrays.asList(exp)));
            exp.addInteraction(interaction);
        }

        return exp;
    }

    ////////////////////
    // Publication

    public Publication createPublicationRandom() {
        return createPublication(String.valueOf(nextInt()));
    }

    public Publication createPublication(String pmid) {
        Publication pub = new Publication(getInstitution(), pmid);

        IntactContext.getCurrentInstance().getLifecycleManager().getStartStatus().create(pub, "Mock builder");

        return pub;
    }

    ////////////////////
    // Lyfecycle Event

    public LifecycleEvent createLifecycleEvent( CvLifecycleEvent event, User user, String note ) {
        return new LifecycleEvent( event, user, note );
    }

    public LifecycleEvent createLifecycleEvent( CvLifecycleEvent event, User user ) {
        return createLifecycleEvent( event, user, null );
    }

    //////////////
    // User

    public User createUser( String login, String firstName, String lastName, String email ) {
        return new User( login, firstName, lastName, email );
    }

    public User createUserSandra() {
        final User sandra = createReviewer( "sandra", "sandra", "-", "sandra@example.com" );
        sandra.addPreference( "notes", "-" );
        sandra.addPreference( "curation.depth", "imex curation" );
        return sandra;
    }

    public User createUserJyoti() {
        final User jyoti = createReviewer( "jyoti", "jyoti", "-", "jyoti@example.com" );
        jyoti.addPreference( "notes", "some large scale references" );
        jyoti.addPreference( "curation.depth", "imex curation" );
        return jyoti;
    }

    public User createAdmin(String login, String firstName, String lastName, String email) {
        User user = createUser(login, firstName, lastName, email);
        user.addRole(new Role(Role.ROLE_ADMIN));
        return user;
    }

    public User createReviewer(String login, String firstName, String lastName, String email) {
         User user = createUser(login, firstName, lastName, email);
         user.addRole(new Role(Role.ROLE_REVIEWER));
         user.addRole(new Role(Role.ROLE_CURATOR));
         return user;
     }

    public User createCurator(String login, String firstName, String lastName, String email) {
        User user = createUser(login, firstName, lastName, email);
        user.addRole(new Role(Role.ROLE_CURATOR));
        return user;
    }

    /////////////////////
    // Intact Entry

    public IntactEntry createIntactEntryRandom() {
       return createIntactEntryRandom(childRandom(), 1, childRandom(1, MAX_CHILDREN));
    }

    public IntactEntry createIntactEntryRandom(int experimentNumber, int minInteractionsPerExperiment, int maxInteractionsPerExpeciment) {
        Collection<Interaction> interactions = new ArrayList<Interaction>();

        for (int i=0; i<experimentNumber; i++) {
            Experiment exp = createExperimentRandom(childRandom(minInteractionsPerExperiment, maxInteractionsPerExpeciment));
            interactions.addAll(exp.getInteractions());
        }

        return new IntactEntry(interactions);
    }

    /////////////////////
    // Annotation

    public Annotation createAnnotation(String annotationText, CvTopic cvTopic) {
        Annotation annotation = new Annotation(institution, cvTopic);
        annotation.setAnnotationText(annotationText);

        return annotation;
    }

    public Annotation createAnnotation(String annotationText, String cvTopicPrimaryId, String cvTopicShortLabel) {
        CvTopic cvTopic = createCvObject(CvTopic.class, cvTopicPrimaryId, cvTopicShortLabel);
        return createAnnotation(annotationText, cvTopic);
    }

    public Annotation createAnnotationRandom() {
        return createAnnotation(nextString("annottext"), CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
    }

    ////////////////////
    // Feature

    public Feature createFeature(String shortLabel, CvFeatureType featureType) {
        Interaction interaction = createInteractionRandomBinary();
        Component component = interaction.getComponents().iterator().next();
        Feature feature = new Feature(getInstitution(), shortLabel, component, featureType);

        return feature;
    }

    public Feature createFeatureRandom() {
        CvFeatureType cvFeatureType = createCvObject(CvFeatureType.class, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, CvFeatureType.EXPERIMENTAL_FEATURE);
        return createFeature(nextString("feat"), cvFeatureType);
    }

    //////////////////////
    // Range

    public Range createRangeUndetermined() {
        Range range = new Range(institution, 0, 0, null);

        CvFuzzyType fuzzyType = createCvObject(CvFuzzyType.class, CvFuzzyType.UNDETERMINED_MI_REF, CvFuzzyType.UNDETERMINED);
        range.setFromCvFuzzyType(fuzzyType);
        range.setToCvFuzzyType(fuzzyType);

        return range;
    }

    public Range createRange(int beginFrom, int endFrom, int beginTo, int endTo) {

        if( beginFrom == 0 && endFrom == 0 && beginTo == 0 && endTo == 0 ) {
            return createRangeUndetermined();
        }

        Range range = new Range(institution, beginFrom, endFrom, beginTo, endTo, null);

        final CvFuzzyType fuzzyType = createCvObject(CvFuzzyType.class, CvFuzzyType.RANGE_MI_REF, CvFuzzyType.RANGE);
        range.setFromCvFuzzyType(fuzzyType);
        range.setToCvFuzzyType(fuzzyType);

        return range;
    }

    public Range createRangeCTerminal() {

        Range range = new Range(institution, 0, 0, null);

        final CvFuzzyType fuzzyType = createCvObject(CvFuzzyType.class, CvFuzzyType.C_TERMINAL_MI_REF, CvFuzzyType.C_TERMINAL);
        range.setFromCvFuzzyType(fuzzyType);
        range.setToCvFuzzyType(fuzzyType);

        return range;
    }

    public Range createRangeCTerminal(int beginFrom, int endFrom, int beginTo, int endTo) {

        Range range = new Range(institution, beginFrom, endFrom, beginTo, endTo, null);

        final CvFuzzyType fuzzyType = createCvObject(CvFuzzyType.class, CvFuzzyType.C_TERMINAL_MI_REF, CvFuzzyType.C_TERMINAL);
        range.setFromCvFuzzyType(fuzzyType);
        range.setToCvFuzzyType(fuzzyType);

        return range;
    }

    public Range createRangeRandom() {
        int from = new Random().nextInt(5);
        int to = new Random().nextInt(10)+from;

        return createRange(from, from, to, to);
    }

     //////////////////////
    // Confidence
     public Confidence createDeterministicConfidence() {
        CvConfidenceType cvConfidenceType = createCvObject( CvConfidenceType.class, "IA:9974", "intact confidence");
        Confidence conf = createConfidence( cvConfidenceType, "0.8");
        return conf;
    }

    public Confidence createConfidence(CvConfidenceType type, String value) {
        Confidence conf = new Confidence(getInstitution(), type, value);
        return conf;
    }

     public Confidence createConfidenceRandom() {
        CvConfidenceType cvConfidenceType = createCvObject( CvConfidenceType.class, "IA:"+ new Random().nextInt(1000), nextString( "cv"));
        Confidence conf = createConfidence(cvConfidenceType, Double.toString( new Random().nextDouble()));
        return conf;
    }
     
     //////////////////////
     // Interaction Parameter
      public InteractionParameter createDeterministicInteractionParameter() {
         CvParameterType cvParameterType = createCvObject( CvParameterType.class, "MI:0836", "temperature");
         CvParameterUnit cvParameterUnit = createCvObject( CvParameterUnit.class, "MI:0838", "kelvin");
         InteractionParameter param = createInteractionParameter( cvParameterType, cvParameterUnit, new Double(302));
         param.setBase(10);
         param.setExponent(0);
         param.setUncertainty(0.8);
         return param;
     }

     public InteractionParameter createInteractionParameter(CvParameterType type, CvParameterUnit unit, Double factor) {
         InteractionParameter param = new InteractionParameter(getInstitution(), type, unit, factor);
         return param;
     }
     
     //////////////////////
     // Component Parameter
      public ComponentParameter createDeterministicComponentParameter() {
         CvParameterType cvParameterType = createCvObject( CvParameterType.class, "MI:0836", "temperature");
         CvParameterUnit cvParameterUnit = createCvObject( CvParameterUnit.class, "MI:0838", "kelvin");
         ComponentParameter param = createComponentParameter( cvParameterType, cvParameterUnit, new Double(302));
         return param;
     }

     public ComponentParameter createComponentParameter(CvParameterType type, CvParameterUnit unit, Double factor) {
         ComponentParameter param = new ComponentParameter(getInstitution(), type, unit, factor);
         return param;
     }

    ///////////////////
    // Utilities

    protected String nextString() {
        return randomString();
    }

    private String nextString(String prefix) {
        return prefix + "_" + randomString();
    }

    protected int nextInt() {
        return new Random().nextInt(10000);
    }

    protected int nextId() {
        sequence++;
        return sequence;
    }

    private int childRandom() {
        return childRandom(MIN_CHILDREN, MAX_CHILDREN);
    }

    private int childRandom(int min, int max) {
        if (min == max) return max;

        return new Random().nextInt(max - min) + min;
    }

    public String randomString() {
        return randomString(childRandom(4,10));
    }

    public String randomString(int returnLength) {
        String vowels = "aeiou";
        String consonants = "qwrtypsdfghjklzxcvbnm";

        StringBuilder random = new StringBuilder( returnLength );
        for (int j = 0; j < returnLength; j++)
        {
            boolean nextIsVowel = new Random().nextBoolean();

            if (nextIsVowel) {
                random.append(vowels.charAt((int) (Math.random() * vowels.length())));
            } else {
                random.append(consonants.charAt((int) (Math.random() * consonants.length())));
            }
        }
        return random.toString();
    }

    protected String randomExperimentLabel() {
        int year = 2000 + new Random().nextInt(8);
        return randomString()+"-"+year+"-"+(new Random().nextInt(7)+1);
    }

    public String randomPeptideSequence() {
        String aminoacids = "ACDEFGHIKLMNPQRSTVWY";

        StringBuilder sb = new StringBuilder();
        sb.append("M");

        for (int i=0; i<new Random().nextInt(500); i++) {
            sb.append(aminoacids.charAt((int) (Math.random() * aminoacids.length())));
        }

        return sb.toString();
    }


}
