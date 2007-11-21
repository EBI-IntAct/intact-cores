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
public abstract class IntactVisitor {

    public void visitIntactObject(IntactObject intactObject) {
        if (intactObject instanceof AnnotatedObject) {
            visitAnnotatedObject((AnnotatedObject)intactObject);
        } else if (intactObject instanceof Annotation) {
            visitAnnotation((Annotation)intactObject);
        } else if (intactObject instanceof Alias) {
            visitAlias((Alias)intactObject);
        } else if (intactObject instanceof Xref) {
            visitXref((Xref)intactObject);
        } else if (intactObject instanceof Range) {
            visitRange((Range)intactObject);
        } else {
            throw new IllegalArgumentException("Cannot visit objects of type: "+intactObject.getClass().getName());
        }
    }

    public void visitAnnotatedObject(AnnotatedObject annotatedObject) {
        if (annotatedObject instanceof Institution) {
            visitInstitution((Institution)annotatedObject);
        } else if (annotatedObject instanceof CvObject) {
            visitCvObject((CvObject)annotatedObject);
        } else if (annotatedObject instanceof Interaction) {
            visitInteraction((Interaction)annotatedObject);
        } else if (annotatedObject instanceof Interactor) {
            visitInteractor((Interactor)annotatedObject);
        } else if (annotatedObject instanceof Experiment) {
            visitExperiment((Experiment)annotatedObject);
        } else if (annotatedObject instanceof Component) {
            visitComponent((Component)annotatedObject);
        } else if (annotatedObject instanceof Feature) {
            visitFeature((Feature)annotatedObject);
        } else if (annotatedObject instanceof BioSource) {
            visitBioSource((BioSource)annotatedObject);
        } else if (annotatedObject instanceof Publication) {
            visitPublication((Publication)annotatedObject);
        } else {
            throw new IllegalArgumentException("Cannot visit annotated object of type: "+annotatedObject.getClass().getName());
        }
    }

    /////////////////////////////////////////////////////
    // IntactObject visitors

    public abstract void visitAnnotation(Annotation annotation);

    public abstract void visitAlias(Alias alias);

    public abstract void visitXref(Xref xref);

    public abstract void visitRange(Range range);


    /////////////////////////////////////////////////////
    // AnnotatedObjects

    public abstract void visitInstitution(Institution institution);

    public abstract void visitCvObject(CvObject cvObject);

    public abstract void visitExperiment(Experiment experiment);

    public abstract void visitFeature(Feature feature);

    public abstract void visitComponent(Component component);

    public abstract void visitInteraction(Interaction interaction);

    public abstract void visitInteractor(Interactor interactor);

    public abstract void visitBioSource(BioSource bioSource);

    public abstract void visitPublication(Publication publication);
}