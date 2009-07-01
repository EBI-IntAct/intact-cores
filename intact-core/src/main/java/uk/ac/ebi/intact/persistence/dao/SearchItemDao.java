/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.SearchItem;

import java.util.List;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface SearchItemDao extends BaseDao<SearchItem> {

    Map<String, Integer> countGroupsByValuesLike( String[] values, String[] objClasses, String type );

    List<String> getDistinctAc( String[] values, String[] objClasses, String type, int firstResult, int maxResults );

    Map<String, String> getDistinctAcGroupingByObjClass( String[] values, String[] objClasses, String type, int firstResult, int maxResults );

    List<SearchItem> getByAc( String ac );

    void persist( SearchItem searchItem );

    void delete( SearchItem searchItem );

    int deleteByAc( String ac );
}
