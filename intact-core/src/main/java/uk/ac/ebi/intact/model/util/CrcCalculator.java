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
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.Crc64;

import java.util.*;

/**
 * Calculates a unique CRC for an object, based on IMEx standards
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CrcCalculator {

    public CrcCalculator() {
    }

    public String crc64(Interaction interaction) {
        UniquenessStringBuilder sb = createUniquenessString(interaction);
        return Crc64.getCrc64(sb.toString().toLowerCase());
    }

    //////////////////////////////////
    // Methods to create Strings to determine the uniqueness

    protected UniquenessStringBuilder createUniquenessString(Interaction interaction) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (interaction == null) return sb;

        // components
        List<Component> components = new ArrayList<Component>(interaction.getComponents());
        Collections.sort(components, new ComponentComparator());

        for (Component component : components) {
            sb.append(createUniquenessString(component));
        }

        // experiments
        Set<Experiment> experiments = new TreeSet<Experiment>(new ExperimentComparator());
        experiments.addAll(interaction.getExperiments());

        for (Experiment experiment : experiments) {
            sb.append(createUniquenessString(experiment));
        }

        // interaction type
        sb.append(createUniquenessString(interaction.getCvInteractionType()));

        // annotations
        Set<Annotation> annotations = new TreeSet<Annotation>(new AnnotationComparator());
        annotations.addAll(interaction.getAnnotations());

        for (Annotation annotation : annotations) {
            sb.append(createUniquenessString(annotation));
        }

        // special identity xrefs that make the interaction unique
        InteractorXref idPdbXref = XrefUtils.getIdentityXref(interaction, CvDatabase.RCSB_PDB_MI_REF);
        InteractorXref idMsdXref = XrefUtils.getIdentityXref(interaction, CvDatabase.MSD_PDB_MI_REF);
        InteractorXref idWwXref = XrefUtils.getIdentityXref(interaction, CvDatabase.WWPDB_MI_REF);
        if (idPdbXref != null) sb.append(idPdbXref.getPrimaryId());
        if (idMsdXref != null) sb.append(idMsdXref.getPrimaryId());
        if (idWwXref != null) sb.append(idWwXref.getPrimaryId());

        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(Experiment experiment) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (experiment == null) return sb;

        // short label
        sb.append(experiment.getShortLabel());

        // participant detection method
        sb.append(createUniquenessString(experiment.getCvIdentification()));

        // interaction type
        sb.append(createUniquenessString(experiment.getCvInteraction()));

        // organism
        sb.append(createUniquenessString(experiment.getBioSource()));

        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(BioSource bioSource) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (bioSource == null) return sb;

        // tax id
        sb.append(bioSource.getTaxId());
        // tissue
        sb.append(createUniquenessString(bioSource.getCvTissue()));
        // cell type
        sb.append(createUniquenessString(bioSource.getCvCellType()));

        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(Component component) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (component == null) return sb;

        // interactor
        sb.append(createUniquenessString(component.getInteractor()));

        // stoichiometry
        sb.append(component.getStoichiometry());

        // biological role
        sb.append(createUniquenessString(component.getCvBiologicalRole()));

        // experimental role
        sb.append(createUniquenessString(component.getCvExperimentalRole()));

        // features
        List<Feature> features = new ArrayList<Feature>(component.getBindingDomains());
        Collections.sort(features, new FeatureComparator());

        for (Feature feature : features) {
            sb.append(createUniquenessString(feature));
        }

        // participant detection methods
        List<CvIdentification> participantDetMethods = new ArrayList<CvIdentification>(component.getParticipantDetectionMethods());
        Collections.sort(participantDetMethods, new CvObjectComparator());

        for (CvIdentification partDetMethod : participantDetMethods) {
            sb.append(createUniquenessString(partDetMethod));
        }

        // experimental preparations
        List<CvExperimentalPreparation> experimentalPreparations = new ArrayList<CvExperimentalPreparation>(component.getExperimentalPreparations());
        Collections.sort(experimentalPreparations, new CvObjectComparator());

        for (CvExperimentalPreparation experimentalPreparation : experimentalPreparations) {
            sb.append(createUniquenessString(experimentalPreparation));
        }

        // annotations
        Set<Annotation> annotations = new TreeSet<Annotation>(new AnnotationComparator());
        annotations.addAll(component.getAnnotations());

        for (Annotation annotation : annotations) {
            sb.append(createUniquenessString(annotation));
        }

        // host organism
        sb.append(createUniquenessString(component.getExpressedIn()));

        return sb;
    }


    protected UniquenessStringBuilder createUniquenessString(Interactor interactor) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (interactor == null) return sb;

        Collection<InteractorXref> idXrefs = XrefUtils.getIdentityXrefs(interactor);

        for (InteractorXref idXref : idXrefs) {
            sb.append(idXref.getPrimaryId().toLowerCase());
        }

        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(Feature feature) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (feature == null) return sb;

        // feature type
        sb.append(createUniquenessString(feature.getCvFeatureType()));

        // feature identification
        sb.append(createUniquenessString(feature.getCvFeatureIdentification()));

        // ranges
        List<Range> ranges = new ArrayList<Range>(feature.getRanges());
        Collections.sort(ranges, new RangeComparator());

        for (Range range : ranges) {
            sb.append(createUniquenessString(range));
        }

        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(Range range) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (range == null) return sb;

        // type from
        sb.append(createUniquenessString(range.getFromCvFuzzyType()));

        // interval from
        sb.append(range.getFromIntervalStart()+"-"+range.getFromIntervalEnd());

        // type to
        sb.append(createUniquenessString(range.getToCvFuzzyType()));

        // interval end
        sb.append(range.getToIntervalStart()+"-"+range.getToIntervalEnd());


        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(CvObject cvObject) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (cvObject == null) return sb;

        // psi-mi
        CvObjectXref idXref = CvObjectUtils.getPsiMiIdentityXref(cvObject);

        if (idXref != null) {
            sb.append(idXref.getPrimaryId());
        } else {
            sb.append(cvObject.getShortLabel());
        }

        return sb;
    }

    protected UniquenessStringBuilder createUniquenessString(Annotation annotation) {
        UniquenessStringBuilder sb = new UniquenessStringBuilder();

        if (annotation == null) return sb;

        sb.append(createUniquenessString(annotation.getCvTopic()));
        sb.append(annotation.getAnnotationText());

        return sb;
    }

    /////////////////////////////////
    // StringBuilder decorator
    protected class UniquenessStringBuilder {

        private static final char SEPARATOR = '|';

        private StringBuilder sb;

        public UniquenessStringBuilder() {
            sb = new StringBuilder();
        }

        public StringBuilder append(UniquenessStringBuilder usb) {
            return sb.append(usb.toString());
        }

        public StringBuilder append(String str) {
            return sb.append(str).append(SEPARATOR);
        }

        public StringBuilder append(int i) {
            return sb.append(i).append(SEPARATOR);
        }

        public StringBuilder append(float f) {
            return sb.append(f);
        }

        public StringBuilder getSringBuilder() {
            return sb;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    /////////////////////////////////
    // Comparators

    protected class ComponentComparator implements Comparator<Component> {

        public int compare(Component o1, Component o2) {
            return createUniquenessString(o1).toString()
                    .compareTo(createUniquenessString(o2).toString());
        }
    }

    protected class ExperimentComparator implements Comparator<Experiment> {

        public int compare(Experiment o1, Experiment o2) {
            return createUniquenessString(o1).toString()
                    .compareTo(createUniquenessString(o2).toString());
        }
    }

    protected class FeatureComparator implements Comparator<Feature> {

        public int compare(Feature o1, Feature o2) {
            return createUniquenessString(o1).toString()
                    .compareTo(createUniquenessString(o2).toString());
        }
    }

    protected class RangeComparator implements Comparator<Range> {

        public int compare(Range o1, Range o2) {
            return createUniquenessString(o1).toString()
                    .compareTo(createUniquenessString(o2).toString());
        }
    }

    protected class AnnotationComparator implements Comparator<Annotation> {

        public int compare(Annotation o1, Annotation o2) {
            return createUniquenessString(o1).toString()
                    .compareTo(createUniquenessString(o2).toString());
        }
    }

    protected class CvObjectComparator implements Comparator<CvObject> {

        public int compare(CvObject o1, CvObject o2) {
            return createUniquenessString(o1).toString()
                    .compareTo(createUniquenessString(o2).toString());
        }
    }
}