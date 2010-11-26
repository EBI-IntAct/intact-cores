/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.users.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Factory for all the user management related DAOs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class UsersDaoFactory implements Serializable {

    @PersistenceContext( unitName = "intact-users-default" )
    private EntityManager currentEntityManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PreferenceDao preferenceDao;

    public UsersDaoFactory() {
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public RoleDao getRoleDao() {
        return roleDao;
    }

    public PreferenceDao getPreferenceDao() {
        return preferenceDao;
    }

    public EntityManager getEntityManager() {
        return currentEntityManager;
    }

    public void setEntityManager( EntityManager entityManager ) {
        currentEntityManager = entityManager;
    }
}
