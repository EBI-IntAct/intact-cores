/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;

import java.util.Collection;

/**
 * DAO for aliases
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface AliasDao<A extends AbstractIntactAlias> extends IntactBaseDao<A> {

    public Collection<A> getByName(String name);

    public Collection<A> getByNameLike(String name);

    public Collection<A> getByType(String typeName, String typeMI);

    public Collection<A> getByTypeAndName(String name, String typeName, String typeMI);

    public Collection<A> getByTypeAndNameLike(String name, String typeName, String typeMI);
}
