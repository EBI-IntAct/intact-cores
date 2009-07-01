/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.IntactObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface IntactObjectDao<T extends IntactObject> extends BaseDao<T>{

    T getByAc( String ac );

    Collection<T> getByAcLike( String ac );

    Collection<T> getByAcLike( String ac, boolean ignoreCase );

    List<T> getByAc( String[] acs );

    List<T> getByAc( Collection<String> acs );

    public Iterator<T> iterator();

    public Iterator<T> iterator( int batchSize );

    Collection<T> getColByPropertyName( String propertyName, String value );

    int deleteByAc( String ac );

    boolean exists( T objToRefresh );
}
