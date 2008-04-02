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
package uk.ac.ebi.intact.model.visitor;

import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DefaultTraverser implements IntactObjectTraverser {

    private int currentHierarchyLevel = -1;

    private RecursionChecker recursionChecker;

    public DefaultTraverser() {
        this.recursionChecker = new RecursionChecker();
    }

    public void traverse(IntactObject intactObject, IntactVisitor... visitors) {
        if (intactObject == null) return;

        if (visitors.length == 0) {
            throw new IllegalArgumentException("No visitors passed");
        }

        nextHierarchyLevel();

        for (IntactVisitor visitor : visitors) {
            visitor.visitIntactObject(intactObject);

            if (visitor instanceof HierarchyAware) {
                final HierarchyAware hierarchyAwareVisitor = (HierarchyAware) visitor;
                hierarchyAwareVisitor.setHierarchyLevel(getCurrentHierarchyLevel());

                hierarchyAwareVisitor.nextHierarchyLevel();
            }
        }

        if (intactObject instanceof AnnotatedObject) {
            traverseAnnotatedObject((AnnotatedObject)intactObject, visitors);
        } else if (intactObject instanceof Annotation) {
            traverseAnnotation((Annotation)intactObject, visitors);
        } else if (intactObject instanceof Alias) {
            traverseAlias((Alias)intactObject, visitors);
        } else if (intactObject instanceof Xref) {
            traverseXref((Xref)intactObject, visitors);
        } else if (intactObject instanceof Range) {
            traverseRange((Range)intactObject, visitors);
        } else if (intactObject instanceof Confidence){
            traverseConfidence((Confidence) intactObject, visitors);
        } else if (intactObject instanceof InteractionParameter){
            traverseParameter((Parameter) intactObject, visitors);
        }  else if (intactObject instanceof ComponentParameter){
            traverseParameter((Parameter) intactObject, visitors);
        } else {
            throw new IllegalArgumentException("Cannot traverse objects of type: "+intactObject.getClass().getName());
        }

        previousHierarchyLevel();

        for (IntactVisitor visitor : visitors) {
            if (visitor instanceof HierarchyAware) {
                final HierarchyAware hierarchyAwareVisitor = (HierarchyAware) visitor;
                hierarchyAwareVisitor.previousHierarchyLevel();
            }
        }
    }

    protected void traverseAnnotatedObject(AnnotatedObject annotatedObject, IntactVisitor... visitors) {
        if (annotatedObject == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitAnnotatedObject(annotatedObject);
        }

        if (annotatedObject instanceof Interaction) {
            traverseInteraction((Interaction) annotatedObject, visitors);
        } else if (annotatedObject instanceof Interactor) {
            traverseInteractor((Interactor) annotatedObject, visitors);
        } else if (annotatedObject instanceof CvObject) {
            traverseCvObject((CvObject) annotatedObject, visitors);
        } else if (annotatedObject instanceof Experiment) {
            traverseExperiment((Experiment) annotatedObject, visitors);
        } else if (annotatedObject instanceof Component) {
            traverseComponent((Component) annotatedObject, visitors);
        } else if (annotatedObject instanceof BioSource) {
            traverseBioSource((BioSource) annotatedObject, visitors);
        } else if (annotatedObject instanceof Feature) {
            traverseFeature((Feature) annotatedObject, visitors);
        } else if (annotatedObject instanceof Publication) {
            traversePublication((Publication) annotatedObject, visitors);
        } else if (annotatedObject instanceof Institution) {
            traverseInstitution((Institution) annotatedObject, visitors);
        } else {
            throw new IllegalArgumentException("Cannot process annotated object of type: " + annotatedObject.getClass().getName());
        }

        traverseAnnotatedObjectCommon(annotatedObject, visitors);
    }

    protected void traverseAnnotatedObjectCommon(AnnotatedObject ao, IntactVisitor ... visitors) {

        for (Annotation annotation : ao.getAnnotations()) {
            traverse(annotation, visitors);
        }
        for (Alias alias : (Collection<Alias>) ao.getAliases()) {
            traverse(alias, visitors);
        }
        for (Xref xref : (Collection<Xref>) ao.getXrefs()) {
            traverse(xref, visitors);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(ao)) {
            return;
        }

        traverse(ao.getOwner(), visitors);
    }

    ///////////////////////////////////////
    // IntactObject traversers

    protected void traverseAnnotation(Annotation annotation, IntactVisitor... visitors) {
        if (annotation == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitAnnotation(annotation);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(annotation)) {
            return;
        }

        traverse(annotation.getCvTopic(), visitors);
        traverse(annotation.getOwner(), visitors);
    }

    protected void traverseAlias(Alias alias, IntactVisitor... visitors) {
        if (alias == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitAlias(alias);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(alias)) {
            return;
        }

        traverse(alias.getCvAliasType(), visitors);
        traverse(alias.getOwner(), visitors);
    }

    protected void traverseXref(Xref xref, IntactVisitor... visitors) {
        if (xref == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitXref(xref);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(xref)) {
            return;
        }

        traverse(xref.getCvXrefQualifier(), visitors);
        traverse(xref.getCvDatabase(), visitors);
        traverse(xref.getOwner(), visitors);
    }

    protected void traverseRange(Range range, IntactVisitor... visitors) {
        if (range == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitRange(range);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(range)) {
            return;
        }

        traverse(range.getFromCvFuzzyType(), visitors);
        traverse(range.getToCvFuzzyType(), visitors);
    }

    protected void traverseConfidence(Confidence confidence, IntactVisitor... visitors) {
        if (confidence == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitConfidence(confidence);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(confidence)) {
            return;
        }

        traverse(confidence.getCvConfidenceType(), visitors);
    }
    
    protected void traverseParameter(Parameter parameter, IntactVisitor... visitors) {
        if (parameter == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitParameter(parameter);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(parameter)) {
            return;
        }

        traverse(parameter.getCvParameterType(), visitors);
        traverse(parameter.getCvParameterUnit(), visitors);
    }

    ///////////////////////////////////////
    // AnnotatedObject traversers

    protected void traverseExperiment(Experiment experiment, IntactVisitor ... visitors) {
        if (experiment == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitExperiment(experiment);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(experiment)) {
            return;
        }

        traverse(experiment.getCvIdentification(), visitors);
        traverse(experiment.getCvInteraction(), visitors);
        traverse(experiment.getBioSource(), visitors);
        traverse(experiment.getPublication(), visitors);

        for (Interaction interaction : experiment.getInteractions()) {
            traverse(interaction, visitors);
        }
    }

    protected void traverseFeature(Feature feature, IntactVisitor ... visitors) {
        if (feature == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitFeature(feature);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(feature)) {
            return;
        }

        traverse(feature.getCvFeatureType(), visitors);
        traverse(feature.getCvFeatureIdentification(), visitors);

        for (Range range : feature.getRanges()) {
            traverse(range, visitors);
        }
    }

    protected void traverseInstitution(Institution institution, IntactVisitor ... visitors) {
        if (institution == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitInstitution(institution);
        }
    }

    protected void traverseInteraction(Interaction interaction, IntactVisitor ... visitors) {
        if (interaction == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitInteraction(interaction);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(interaction)) {
            return;
        }

        traverse(interaction.getCvInteractionType(), visitors);

        for (Experiment experiment : interaction.getExperiments()) {
            traverse(experiment, visitors);
        }

        for (Component component : interaction.getComponents()) {
            traverse(component, visitors);
        }

        for (Confidence confidence : interaction.getConfidences()) {
            traverse(confidence, visitors);
        }
        
        for (InteractionParameter interactionParameter : interaction.getParameters()) {
            traverse(interactionParameter, visitors);
        }
    }

    protected void traverseInteractor(Interactor interactor, IntactVisitor ... visitors) {
        if (interactor == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitInteractor(interactor);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(interactor)) {
            return;
        }

        traverse(interactor.getCvInteractorType(), visitors);
        traverse(interactor.getBioSource(), visitors);
    }

    protected void traverseBioSource(BioSource bioSource, IntactVisitor ... visitors) {
        if (bioSource == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitBioSource(bioSource);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(bioSource)) {
            return;
        }

        traverse(bioSource.getCvCellType(), visitors);
        traverse(bioSource.getCvTissue(), visitors);
    }

    protected void traversePublication(Publication publication, IntactVisitor ... visitors) {
        if (publication == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitPublication(publication);
        }
    }

    protected void traverseComponent(Component component, IntactVisitor ... visitors) {
        if (component == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitComponent(component);
        }

        // check if this element has been traversed already, to avoid cyclic recursion
        if (recursionChecker.isAlreadyTraversed(component)) {
            return;
        }

        traverse(component.getInteraction(), visitors);
        traverse(component.getInteractor(), visitors);
        traverse(component.getCvExperimentalRole(), visitors);
        traverse(component.getCvBiologicalRole(), visitors);
        traverse(component.getExpressedIn(), visitors);

        for (Parameter parameter : component.getParameters())
        traverse(parameter, visitors);

        for (CvIdentification partDetMethod : component.getParticipantDetectionMethods()) {
            traverse(partDetMethod, visitors);
        }

        for (CvExperimentalPreparation experimentalPreparation : component.getExperimentalPreparations()) {
            traverse(experimentalPreparation, visitors);
        }

        for (Feature feature : component.getBindingDomains()) {
            traverse(feature, visitors);
        }
    }

    protected void traverseCvObject(CvObject cvObject, IntactVisitor ... visitors) {
        if (cvObject == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitCvObject(cvObject);
        }
    }

    protected void nextHierarchyLevel() {
        currentHierarchyLevel++;
    }

    protected void previousHierarchyLevel() {
        currentHierarchyLevel--;
    }

    public int getCurrentHierarchyLevel() {
        return currentHierarchyLevel;
    }

    protected class RecursionChecker {

        private List<IntactObject> intactObjects;

        private RecursionChecker() {
            this.intactObjects = new ArrayList<IntactObject>();
        }

        public boolean isAlreadyTraversed(IntactObject intactObject) {
             if (intactObjects.contains(intactObject)) {
                 return true;
             } else {
                 intactObjects.add(intactObject);
             }

            return false;
        }
    }
}
