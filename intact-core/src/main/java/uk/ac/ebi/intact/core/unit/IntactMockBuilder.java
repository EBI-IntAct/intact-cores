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
import uk.ac.ebi.intact.model.util.AliasUtils;
import uk.ac.ebi.intact.model.util.CvObjectBuilder;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;

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
        this.cvBuilder = new CvObjectBuilder();
    }

    public IntactMockBuilder(Institution institution) {
        this.institution = institution;
        this.cvBuilder = new CvObjectBuilder();
    }

    public Institution getInstitution() {
        if (institution == null) {
            institution = new Institution("myInstitution");
        }
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
        return bioSource;
    }

    public <A extends Alias> A createAliasGeneName(AnnotatedObject<?,A> parent, String name) {
        return AliasUtils.createAliasGeneName(parent, name);
    }

    public <T extends CvObject> T createCvObject(Class<T> cvClass, String primaryId, String shortLabel) {
        return CvObjectUtils.createCvObject(getInstitution(), cvClass, primaryId, shortLabel);
    }

    public Protein createProteinRandom() {
        return createProtein(nextString("primId"), nextString("prot"), createBioSourceRandom());
    }

    public Protein createProtein(String uniprotId, String shortLabel, BioSource bioSource) {
        CvInteractorType intType = createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);

        Protein protein = new ProteinImpl(getInstitution(), bioSource, shortLabel, intType);
        InteractorXref idXref = createIdentityXrefUniprot(protein, uniprotId);
        protein.addXref(idXref);

        InteractorAlias alias = createAliasGeneName(protein, nextString("gene"));
        protein.addAlias(alias);

        return protein;
    }

    public Component createComponent(Interaction interaction, Interactor interactor, CvExperimentalRole expRole, CvBiologicalRole bioRole) {
        return new Component(getInstitution(), interaction, interactor, expRole, bioRole);
    }

    public Component createComponentNeutral(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return new Component(getInstitution(), interaction, interactor, expRole, bioRole);
    }

    public Component createComponentBait(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return new Component(getInstitution(), interaction, interactor, expRole, bioRole);
    }

    public Component createComponentPrey(Interaction interaction, Interactor interactor) {
        CvExperimentalRole expRole = createCvObject(CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY);
        CvBiologicalRole bioRole = createCvObject(CvBiologicalRole.class, CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);

        return new Component(getInstitution(), interaction, interactor, expRole, bioRole);
    }

    public Interaction createInteraction(String shortLabel, Interactor bait, Interactor prey, Experiment experiment) {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        Interaction interaction = new InteractionImpl(Arrays.asList(experiment), cvInteractionType, null, shortLabel, getInstitution());

        interaction.addComponent(createComponentBait(interaction, bait));
        interaction.addComponent(createComponentPrey(interaction, prey));

        return interaction;
    }

    public Interaction createInteractionRandomBinary() {
        CvInteractionType cvInteractionType = createCvObject(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF, CvInteractionType.DIRECT_INTERACTION);

        Interaction interaction = new InteractionImpl(Arrays.asList(createExperimentEmpty(nextString("exp"))), cvInteractionType, null, nextString("label"), getInstitution());

        interaction.addComponent(createComponentBait(interaction, createProteinRandom()));
        interaction.addComponent(createComponentPrey(interaction, createProteinRandom()));

        return interaction;
    }

    public Experiment createExperimentEmpty(String shortLabel) {
         Experiment experiment = new Experiment(getInstitution(), shortLabel, createBioSourceRandom());

        experiment.setCvInteraction(createCvObject(CvInteraction.class, CvInteraction.COSEDIMENTATION_MI_REF, CvInteraction.COSEDIMENTATION));
        experiment.setCvIdentification(createCvObject(CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED));

        experiment.setPublication(createPublicationRandom());

        return experiment;
    }

    public Experiment createExperimentRandom(int interactionNumber) {
        Experiment exp = createExperimentEmpty(nextString("exp"));

        for (int i=0; i<interactionNumber; i++) {
            Interaction interaction = createInteractionRandomBinary();
            interaction.setExperiments(Arrays.asList(exp));
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

    protected String nextString() {
        return nextString("str");
    }

    private String nextString(String prefix) {
        return prefix + "_" + nextInt();
    }

    protected int nextInt() {
        return new Random().nextInt(10000);
    }

    protected int nextId() {
        sequence++;
        return sequence;
    }

    protected int childRandom() {
        return childRandom(MIN_CHILDREN, MAX_CHILDREN);
    }

    private int childRandom(int min, int max) {
        if (min == max) return max;

        return new Random().nextInt(max - min) + min;
    }
}