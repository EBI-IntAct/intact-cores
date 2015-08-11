package uk.ac.ebi.intact.jami.synchronizer.listener.updates;

import psidev.psi.mi.jami.model.Alias;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in a cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class OrganismUpdates {
    private List<Alias> addedAliases = new ArrayList<Alias>();

    public List<Alias> getAddedAliases() {
        return addedAliases;
    }
}
