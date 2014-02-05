/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.user.User;

import java.util.Collection;

/**
 * DAO for users
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface UserDao extends IntactBaseDao<User> {

    public User getByLogin(String login);
    public Collection<User> getByFirstName(String name);
    public Collection<User> getByLastName(String name);
    public User getByEMail(String mail);
    public Collection<User> getByDisabled(boolean disabled);
    public Collection<User> getByRole(String role);
    public Collection<User> getByPreference(String key, String value);
}
