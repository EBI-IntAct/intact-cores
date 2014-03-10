package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactComplex;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ComplexDao extends InteractorDao<IntactComplex>{

    public Collection<IntactComplex> getByInteractionType(String typeName, String typeMI, int first, int max);

    public Collection<IntactComplex> getByLifecycleEvent(String evtName, int first, int max);

    public Collection<IntactComplex> getByStatus(String statusName, int first, int max);

    public Collection<IntactComplex> getByConfidence(String typeName, String typeMI, String value);
}
