package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactAllostery;

import java.util.Collection;

/**
 * Allostery DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface AllosteryDao extends CooperativeEffectDao<IntactAllostery> {

    public Collection<IntactAllostery> getByAllostericMoleculeAc(String ac);

    public Collection<IntactAllostery> getByMechanism(String name, String mi);

    public Collection<IntactAllostery> getByAllosteryType(String name, String mi);

    public Collection<IntactAllostery> getByMoleculeEffectorAc(String ac);

    public Collection<IntactAllostery> getByFeatureModificationEffectorAc(String ac);
}
