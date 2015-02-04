package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.Xref;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in an experiment
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class ExperimentUpdates {
    private List<InteractionEvidence> addedInteractions = new ArrayList<InteractionEvidence>();
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<VariableParameter> addedVariableParameters = new ArrayList<VariableParameter>();

    public List<InteractionEvidence> getAddedInteractions() {
        return addedInteractions;
    }

    public List<Xref> getAddedXrefs() {
        return addedXrefs;
    }

    public List<Annotation> getAddedAnnotations() {
        return addedAnnotations;
    }

    public List<VariableParameter> getAddedVariableParameters() {
        return addedVariableParameters;
    }
}
