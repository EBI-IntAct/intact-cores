/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.user.Preference;

import java.util.Collection;

/**
 * DAO for user preference
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface PreferenceDao extends IntactBaseDao<Preference> {

    public Collection<Preference> getByKey(String key, int first, int max);
    public Collection<Preference> getByUserAc(String parentAc);
}
