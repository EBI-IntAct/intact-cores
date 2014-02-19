package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntityPool;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntityPool;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ModelledEntityPoolDao extends ModelledEntityDao<IntactModelledEntityPool>{

    public Collection<IntactExperimentalEntityPool> getByType(String typeName, String typeMI);
}
