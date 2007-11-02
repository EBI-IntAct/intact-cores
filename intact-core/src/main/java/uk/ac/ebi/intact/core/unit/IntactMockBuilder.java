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

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.*;
import uk.ac.ebi.intact.util.Crc64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * Simulates populated IntAct model objects - useful when testing
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

    public CvDatabase getPsiMiDatabase() {
        return cvBuilder.createPsiMiCvDatabase(getInstitution());
    }

    public CvXrefQualifier getIdentityQualifier() {
        return cvBuilder.createIdentityCvXrefQualifier(getInstitution());
    }

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

    public BioSource createBioSourceRandom() {
        return createBioSource(nextId(), nextString("label"));
    }

    public BioSource createBioSource(int taxId, String shortLabel) {
        BioSource bioSource = new BioSource(getInstitution(), shortLabel, String.valueOf(taxId));

        CvDatabase newt = createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
        BioSourceXref newtXref = createIdentityXref(bioSource, String.valueOf(taxId), newt);
        bioSource.addXref(newtXref);
        
        return bioSource;
    }

    public <A extends Alias> A createAliasGeneName(AnnotatedObject<?,A> parent, String name) {
        return AliasUtils.createAliasGeneName(parent, name);
    }

    public <T extends CvObject> T createCvObject(Class<T> cvClass, String primaryId, String shortLabel) {
        return CvObjectUtils.createCvObject(getInstitution(), cvClass, primaryId, shortLabel);
    }

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

    public Protein createProteinRandom() {
        return createProtein(nextString("primId"), nextString(), createBioSourceRandom());
    }

    public Protein createProtein(String uniprotId, String shortLabel, BioSource bioSource) {
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);

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


    public Protein createProtein(String uniprotId, String shortLabel) {
        return createProtein(uniprotId, shortLabel, createBioSourceRandom());
    }

    public Component createComponent(Interaction interaction, Interactor interactor, CvExperimentalRole expRole, CvBiologicalRole bioRole) {
        Component component = new Component(getInstitution(), interaction, interactor, expRole, bioRole);

        CvIdentification cvParticipantDetMethod = createCvObject(CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED);
        component.getParticipantDetectionMethods().add(cvParticipantDetMethod);

        CvExperimentalPreparation cvExperimentalPreparation = createCvObject(CvExperimentalPreparation.class, CvExperimentalPreparation.PURIFIED_REF, CvExperimentalPreparation.PURIFIED);
        component.getExperimentalPreparations().add(cvExperimentalPreparation);

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

    public Component createComponentBait(Interactor interactor) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);
        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(createExperimentEmpty())), cvInteractionType, null, nextString("label"), getInstitution());
        return createComponentBait(interaction, interactor);
    }

    public Component createComponentBait(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return createComponent(interaction, interactor, expRole, bioRole);
    }

    public Component createComponentPrey(Interactor interactor) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);
        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(createExperimentEmpty())), cvInteractionType, null, nextString("label"), getInstitution());
        return createComponentPrey(interaction, interactor);
    }

    public Component createComponentRandom() {
        return createInteractionRandomBinary().getComponents().iterator().next();
    }

    public Component createComponentPrey(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return createComponent(interaction, interactor, expRole, bioRole);
    }

    public Interaction createInteraction(String shortLabel, Interactor bait, Interactor prey, Experiment experiment) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(experiment)),
                                                      cvInteractionType, null, shortLabel, getInstitution());

        interaction.addComponent(createComponentBait(interaction, bait));
        interaction.addComponent(createComponentPrey(interaction, prey));

        return interaction;
    }

    public Interaction createInteraction(Component ... components) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        Experiment experiment = createExperimentEmpty();

        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(experiment)), cvInteractionType, null, "temp", getInstitution());

        for (Component component : components) {
            interaction.addComponent(component);
        }

        String shortLabel = InteractionUtils.calculateShortLabel(interaction);
        interaction.setShortLabel(shortLabel);

        return interaction;
    }

    public Interaction createInteractionRandomBinary() {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(createExperimentEmpty())), cvInteractionType, null, nextString("label"), getInstitution());

        interaction.addComponent(createComponentBait(interaction, createProteinRandom()));
        interaction.addComponent(createComponentPrey(interaction, createProteinRandom()));

        String shortLabel = InteractionUtils.calculateShortLabel(interaction);
        interaction.setShortLabel(shortLabel);

        return interaction;
    }

    public Interaction createInteraction(String ... interactorShortLabels) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);
        Interaction interaction = new InteractionImpl(new ArrayList<Experiment>(Arrays.asList(createExperimentEmpty())), cvInteractionType, null, nextString("label"), getInstitution());

        Protein prot = null;
        for (String interactorShortLabel : interactorShortLabels) {
            prot = createProtein("uniprotId_" + interactorShortLabel, interactorShortLabel);
            interaction.addComponent(createComponentNeutral(interaction, prot));
        }

        if (interactorShortLabels.length == 1) {
            interaction.addComponent(createComponentNeutral(interaction, prot));
        }

        String shortLabel = InteractionUtils.calculateShortLabel(interaction);
        interaction.setShortLabel(shortLabel);

        return interaction;
    }

    public Interaction createInteractionFooBar() {
        Experiment experiment = createExperimentEmpty("exp-2006-1","12345");
        experiment.setBioSource(createBioSource(5, "lalaorg"));
        Interaction interaction = createInteraction("fooprey-barbait",
                                                    createProtein("A2", "barbait"),
                                                    createProtein("A1", "fooprey"),
                                                    experiment);
        interaction.getAnnotations().add(createAnnotation("This is an annotation", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT));

        CvFeatureType featureType = createCvObject(CvFeatureType.class, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, CvFeatureType.EXPERIMENTAL_FEATURE);
        Feature feature = createFeature("feature1", featureType);
        feature.setComponent(null);

        Range range = createRange(1, 1, 5, 5);
        feature.addRange(range);

        interaction.getComponents().iterator().next().addBindingDomain(feature);

        return interaction;
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
        publication.getExperiments().add(experiment);
        experiment.setPublication(publication);
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

    public Publication createPublicationRandom() {
        return createPublication(String.valueOf(nextInt()));
    }

    public Publication createPublication(String pmid) {
        Publication pub = new Publication(getInstitution(), pmid);
        return pub;
    }

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