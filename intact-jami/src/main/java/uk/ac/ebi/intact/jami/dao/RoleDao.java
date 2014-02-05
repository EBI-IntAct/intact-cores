/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.user.Role;

/**
 * DAO for user role
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface RoleDao extends IntactBaseDao<Role> {

    public Role getByName(String name);
}
