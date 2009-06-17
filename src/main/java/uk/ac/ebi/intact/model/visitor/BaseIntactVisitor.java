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

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class BaseIntactVisitor implements IntactVisitor, HierarchyAware {

    int hierarchyLevel = 0;

    //////////////////////////////////////////
    // IntactObjects

    public void visitIntactObject(IntactObject intactObject) {
        // nothing
    }

    public void visitAnnotatedObject(AnnotatedObject annotatedObject) {
        // nothing
    }

    public void visitAnnotation(Annotation annotation) {
        // nothing
    }

    public void visitAlias(Alias alias) {
        // nothing
    }

    public void visitXref(Xref xref) {
        // nothing
    }

    public void visitRange(Range range) {
        // nothing
    }

     public void visitConfidence(Confidence confidence) {
        // nothing
    }

     public void visitParameter(Parameter parameter) {
         // nothing
     }

    //////////////////////////////////////////
    // AnnotatedObjects

    public void visitCvObject(CvObject cvObject) {
        // nothing
    }

    public void visitInstitution(Institution institution) {
        // nothing
    }

    public void visitExperiment(Experiment experiment) {
        // nothing
    }

    public void visitFeature(Feature feature) {
        // nothing
    }

    public void visitComponent(Component component) {
        // nothing
    }

    public void visitInteraction(Interaction interaction) {
        // nothing
    }

    public void visitInteractor(Interactor interactor) {
        // nothing
    }

    public void visitBioSource(BioSource bioSource) {
        // nothing
    }

    public void visitPublication(Publication publication) {
        // nothing
    }

    //////////////////////////////////////
    // Hierarchy aware

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public void nextHierarchyLevel() {
        // nothing
    }

    public void previousHierarchyLevel() {
        // nothing
    }
}
