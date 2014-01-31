package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;

import java.util.Collection;

/**
 * Interactor pool DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface InteractorPoolDao extends InteractorDao<IntactInteractorPool> {

    public Collection<IntactInteractorPool> getByInteractorAc(String ac);

    public Collection<IntactInteractorPool> getByInteractorShortName(String value);

}
