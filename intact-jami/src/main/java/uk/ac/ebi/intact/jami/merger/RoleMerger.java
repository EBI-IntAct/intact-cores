package uk.ac.ebi.intact.jami.merger;

import uk.ac.ebi.intact.jami.model.user.Role;

/**
 * User merger that will update properties of an existing user.
 * It will override all properties of user
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class RoleMerger extends IntactDbMergerEnrichOnly<Role, Role> {

    public RoleMerger(){
        super(Role.class);
    }

    @Override
    public Role merge(Role obj1, Role obj2) {
        // obj2 is mergedRole
        Role mergedRole = super.merge(obj1, obj2);

        // merge name
        if ((mergedRole.getName() == null && obj1.getName() != null) ||
                (mergedRole.getName() != null && !mergedRole.getName().equals(obj1.getName()))){
            mergedRole.setName(obj1.getName());
        }
        return mergedRole;
    }
}
