package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in an interactor, interactor pool or complex
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class InteractorUpdates {
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<Alias> addedAliases = new ArrayList<Alias>();
    private List<Checksum> addedChecksums = new ArrayList<Checksum>();
    private List<Xref> addedIdentifiers = new ArrayList<Xref>();

    private List<Interactor> addedInteractors = new ArrayList<Interactor>();

    private List<ModelledParticipant> addedParticipants = new ArrayList<ModelledParticipant>();
    private List<ModelledConfidence> addedConfidences = new ArrayList<ModelledConfidence>();
    private List<ModelledParameter> addedParameters = new ArrayList<ModelledParameter>();
    private List<LifeCycleEvent> addedLyfeCycleEvents = new ArrayList<LifeCycleEvent>();

    public List<Xref> getAddedXrefs() {
        return addedXrefs;
    }

    public List<Annotation> getAddedAnnotations() {
        return addedAnnotations;
    }

    public List<Alias> getAddedAliases() {
        return addedAliases;
    }

    public List<Checksum> getAddedChecksums() {
        return addedChecksums;
    }

    public List<Xref> getAddedIdentifiers() {
        return addedIdentifiers;
    }

    public List<Interactor> getAddedInteractors() {
        return addedInteractors;
    }

    public List<ModelledParticipant> getAddedParticipants() {
        return addedParticipants;
    }

    public List<ModelledConfidence> getAddedConfidences() {
        return addedConfidences;
    }

    public List<ModelledParameter> getAddedParameters() {
        return addedParameters;
    }

    public List<LifeCycleEvent> getAddedLyfeCycleEvents() {
        return addedLyfeCycleEvents;
    }
}
