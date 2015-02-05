package uk.ac.ebi.intact.jami.synchronizer.listener.updates;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in a cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class PublicationUpdates {
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<Annotation> addedOtherDbAnnotations = new ArrayList<Annotation>();
    private List<Xref> addedIdentifiers = new ArrayList<Xref>();

    private List<Experiment> addedExperiments = new ArrayList<Experiment>();
    private List<LifeCycleEvent> addedLifeCycleEvents = new ArrayList<LifeCycleEvent>();

    public List<Xref> getAddedXrefs() {
        return addedXrefs;
    }

    public List<Annotation> getAddedAnnotations() {
        return addedAnnotations;
    }

    public List<Xref> getAddedIdentifiers() {
        return addedIdentifiers;
    }

    public List<Experiment> getAddedExperiments() {
        return addedExperiments;
    }

    public List<LifeCycleEvent> getAddedLifeCycleEvents() {
        return addedLifeCycleEvents;
    }

    public List<Annotation> getAddedOtherDbAnnotations() {
        return addedOtherDbAnnotations;
    }
}
