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

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DefaultTraverser implements IntactObjectTraverser {

    public void traverse(IntactObject intactObject, IntactVisitor ... visitors) {
        if (intactObject instanceof AnnotatedObject) {
            traverseAnnotatedObject((AnnotatedObject)intactObject, visitors);
        } else if (intactObject instanceof Annotation) {
            traverseAnnotation((Annotation)intactObject, visitors);
        } else if (intactObject instanceof Alias) {
            traverseAlias((Alias)intactObject, visitors);
        } else if (intactObject instanceof Xref) {
            traverseXref((Xref)intactObject, visitors);
        } else {
            throw new IllegalArgumentException("Cannot traverse objects of type: "+intactObject.getClass().getName());
        }
    }

    protected void traverseAnnotatedObject(AnnotatedObject annotatedObject, IntactVisitor... visitors) {
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
    }

    ///////////////////////////////////////
    // IntactObject traversers

    protected void traverseAnnotation(Annotation annotation, IntactVisitor ... visitors) {
        if (annotation == null) return;

        traverseCvObject(annotation.getCvTopic(), visitors);
        traverseInstitution(annotation.getOwner());
    }

    protected void traverseAlias(Alias alias, IntactVisitor ... visitors) {
        if (alias == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitAlias(alias);
        }

        traverseCvObject(alias.getCvAliasType());
        traverseInstitution(alias.getOwner());
    }

    protected void traverseXref(Xref xref, IntactVisitor ... visitors) {
        if (xref == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitXref(xref);
        }

        traverseCvObject(xref.getCvXrefQualifier());
        traverseCvObject(xref.getCvDatabase());
        traverseInstitution(xref.getOwner());
    }

    protected void traverseRange(Range range, IntactVisitor ... visitors) {
        if (range == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitRange(range);
        }

        traverseCvObject(range.getFromCvFuzzyType(), visitors);
        traverseCvObject(range.getToCvFuzzyType(), visitors);
    }

    ///////////////////////////////////////
    // AnnotatedObject traversers

    protected void traverseExperiment(Experiment experiment, IntactVisitor ... visitors) {
        if (experiment == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitExperiment(experiment);
        }

        traverseCvObject(experiment.getCvIdentification());
        traverseCvObject(experiment.getCvInteraction());
        traverseBioSource(experiment.getBioSource(), visitors);
        traversePublication(experiment.getPublication());

        // TODO check if they have been processed to avoid infinite loops
        for (Interaction interaction : experiment.getInteractions()) {
            traverseInteraction(interaction);
        }

        traverseAnnotatedObjectCommon(experiment, visitors);
    }

    protected void traverseFeature(Feature feature, IntactVisitor ... visitors) {
        if (feature == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitFeature(feature);
        }

        traverseCvObject(feature.getCvFeatureType());
        traverseCvObject(feature.getCvFeatureIdentification());

        for (Range range : feature.getRanges()) {
            traverseRange(range, visitors);
        }

        traverseAnnotatedObjectCommon(feature, visitors);

        throw new UnsupportedOperationException();
    }

    protected void traverseInstitution(Institution institution, IntactVisitor ... visitors) {
        if (institution == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitInstitution(institution);
        }

        traverseAnnotatedObjectCommon(institution, visitors);
    }

    protected void traverseInteraction(Interaction interaction, IntactVisitor ... visitors) {
        if (interaction == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitInteraction(interaction);
        }

        traverseCvObject(interaction.getCvInteractionType());

        // TODO check if they have been processed to avoid infinite loops
        for (Experiment experiment : interaction.getExperiments()) {
            traverseExperiment(experiment);
        }

        for (Component component : interaction.getComponents()) {
            traverseComponent(component);
        }

        traverseAnnotatedObjectCommon(interaction, visitors);
    }

    protected void traverseInteractor(Interactor interactor, IntactVisitor ... visitors) {
        if (interactor == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitInteractor(interactor);
        }

        traverseCvObject(interactor.getCvInteractorType(), visitors);
        traverseBioSource(interactor.getBioSource(), visitors);

        traverseAnnotatedObjectCommon(interactor, visitors);
    }

    protected void traverseBioSource(BioSource bioSource, IntactVisitor ... visitors) {
        if (bioSource == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitBioSource(bioSource);
        }

        traverseCvObject(bioSource.getCvCellType());
        traverseCvObject(bioSource.getCvTissue());

        traverseAnnotatedObjectCommon(bioSource, visitors);
    }

    protected void traversePublication(Publication publication, IntactVisitor ... visitors) {
        if (publication == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitPublication(publication);
        }

        traverseAnnotatedObjectCommon(publication, visitors);
    }

    protected void traverseComponent(Component component, IntactVisitor ... visitors) {
        if (component == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitComponent(component);
        }

        //traverseInteraction(component.getInteraction());
        traverseInteractor(component.getInteractor());
        traverseCvObject(component.getCvExperimentalRole());
        traverseCvObject(component.getCvBiologicalRole());
        traverseBioSource(component.getExpressedIn());

        for (CvIdentification partDetMethod : component.getParticipantDetectionMethods()) {
            traverseCvObject(partDetMethod);
        }

        for (CvExperimentalPreparation experimentalPreparation : component.getExperimentalPreparations()) {
            traverseCvObject(experimentalPreparation);
        }

        for (Feature feature : component.getBindingDomains()) {
            traverseFeature(feature);
        }

        traverseAnnotatedObjectCommon(component, visitors);
    }

    protected void traverseCvObject(CvObject cvObject, IntactVisitor ... visitors) {
        if (cvObject == null) return;

        for (IntactVisitor visitor : visitors) {
            visitor.visitCvObject(cvObject);
        }

        traverseAnnotatedObjectCommon(cvObject, visitors);
    }

    protected void traverseAnnotatedObjectCommon(AnnotatedObject ao, IntactVisitor ... visitors) {
        for (IntactVisitor visitor : visitors) {
            for (Annotation annotation : ao.getAnnotations()) {
                visitor.visitAnnotation(annotation);
            }
            for (Alias alias : (Collection<Alias>) ao.getAliases()) {
                visitor.visitAlias(alias);
            }
            for (Xref xref : (Collection<Xref>) ao.getXrefs()) {
                visitor.visitXref(xref);
            }
            visitor.visitInstitution(ao.getOwner());
        }
    }
}