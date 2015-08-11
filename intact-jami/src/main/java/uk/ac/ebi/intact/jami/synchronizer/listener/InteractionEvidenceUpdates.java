package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in an interactor, interactor pool or complex
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class InteractionEvidenceUpdates {
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<Annotation> addedNegative = new ArrayList<Annotation>();
    private List<Xref> addedIdentifiers = new ArrayList<Xref>();

    private List<ParticipantEvidence> addedParticipants = new ArrayList<ParticipantEvidence>();
    private List<Confidence> addedConfidences = new ArrayList<Confidence>();
    private List<Parameter> addedParameters = new ArrayList<Parameter>();
    private List<VariableParameterValueSet> addedVariableParameterSets = new ArrayList<VariableParameterValueSet>();

    public List<Xref> getAddedXrefs() {
        return addedXrefs;
    }

    public List<Annotation> getAddedAnnotations() {
        return addedAnnotations;
    }

    public List<Xref> getAddedIdentifiers() {
        return addedIdentifiers;
    }

    public List<ParticipantEvidence> getAddedParticipants() {
        return addedParticipants;
    }

    public List<Confidence> getAddedConfidences() {
        return addedConfidences;
    }

    public List<Parameter> getAddedParameters() {
        return addedParameters;
    }

    public List<VariableParameterValueSet> getAddedVariableParameterSets() {
        return addedVariableParameterSets;
    }

    public List<Annotation> getAddedNegative() {
        return addedNegative;
    }
}
