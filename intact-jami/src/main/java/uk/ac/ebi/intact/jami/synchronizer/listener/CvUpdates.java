package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.OntologyTerm;
import psidev.psi.mi.jami.model.Xref;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in a cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class CvUpdates {
    private List<Xref> addedXrefs = new ArrayList<Xref>();
    private List<Annotation> addedAnnotations = new ArrayList<Annotation>();
    private List<Alias> addedAliases = new ArrayList<Alias>();
    private List<Xref> addedIdentifiers = new ArrayList<Xref>();

    private List<OntologyTerm> addedParents = new ArrayList<OntologyTerm>();

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

    public List<OntologyTerm> getAddedParents() {
        return addedParents;
    }
}
