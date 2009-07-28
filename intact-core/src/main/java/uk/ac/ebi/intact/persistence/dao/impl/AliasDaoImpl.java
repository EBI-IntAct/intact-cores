/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.persistence.dao.AliasDao;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class AliasDaoImpl<T extends Alias> extends IntactObjectDaoImpl<T> implements AliasDao<T> {

    public AliasDaoImpl( Class<T> aliasClass, EntityManager entityManager, IntactSession intactSession ) {
        super( aliasClass, entityManager, intactSession );
    }

    public Collection<T> getByNameLike( String name ) {
        return getByPropertyNameLike( "name", name );
    }
}
