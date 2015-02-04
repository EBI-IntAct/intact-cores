package uk.ac.ebi.intact.jami.synchronizer.listener;

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

public class FeatureUpdates<F extends Feature> {
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<Alias> addedAliases = new ArrayList<Alias>();
    private List<Xref> addedIdentifiers = new ArrayList<Xref>();
    private List<F> addedLinkedFeatures = new ArrayList<F>();
    private List<Range> addedRanges = new ArrayList<Range>();

    private List<CvTerm> addedDetectionMethods = new ArrayList<CvTerm>();
    private List<Parameter> addedParameters = new ArrayList<Parameter>();

    public List<Xref> getAddedXrefs() {
        return addedXrefs;
    }

    public List<Annotation> getAddedAnnotations() {
        return addedAnnotations;
    }

    public List<Alias> getAddedAliases() {
        return addedAliases;
    }

    public List<Xref> getAddedIdentifiers() {
        return addedIdentifiers;
    }

    public List<F> getAddedLinkedFeatures() {
        return addedLinkedFeatures;
    }

    public List<Range> getAddedRanges() {
        return addedRanges;
    }

    public List<CvTerm> getAddedDetectionMethods() {
        return addedDetectionMethods;
    }

    public List<Parameter> getAddedParameters() {
        return addedParameters;
    }
}
