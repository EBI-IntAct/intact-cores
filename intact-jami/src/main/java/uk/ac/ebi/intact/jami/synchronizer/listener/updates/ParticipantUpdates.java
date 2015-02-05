package uk.ac.ebi.intact.jami.synchronizer.listener.updates;

import psidev.psi.mi.jami.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in a feature
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class ParticipantUpdates<F extends Feature> {
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<Alias> addedAliases = new ArrayList<Alias>();
    private List<F> addedFeatures = new ArrayList<F>();
    private List<CausalRelationship> addedCausalRelationships = new ArrayList<CausalRelationship>();

    private List<CvTerm> addedIdentificationMethods = new ArrayList<CvTerm>();
    private List<Parameter> addedParameters = new ArrayList<Parameter>();
    private List<CvTerm> addedExperimentalPreparations = new ArrayList<CvTerm>();
    private List<Confidence> addedConfidences = new ArrayList<Confidence>();

    public List<Xref> getAddedXrefs() {
        return addedXrefs;
    }

    public List<Annotation> getAddedAnnotations() {
        return addedAnnotations;
    }

    public List<Alias> getAddedAliases() {
        return addedAliases;
    }

    public List<F> getAddedFeatures() {
        return addedFeatures;
    }

    public List<CvTerm> getAddedIdentificationMethods() {
        return addedIdentificationMethods;
    }

    public List<Parameter> getAddedParameters() {
        return addedParameters;
    }

    public List<CausalRelationship> getAddedCausalRelationships() {
        return addedCausalRelationships;
    }

    public List<CvTerm> getAddedExperimentalPreparations() {
        return addedExperimentalPreparations;
    }

    public List<Confidence> getAddedConfidences() {
        return addedConfidences;
    }
}
