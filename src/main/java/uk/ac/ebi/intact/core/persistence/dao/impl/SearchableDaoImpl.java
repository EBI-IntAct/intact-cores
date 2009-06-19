/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.model.AnnotatedObjectImpl;
import uk.ac.ebi.intact.model.Searchable;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Searches
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Repository
@Transactional(readOnly = true)
public class SearchableDaoImpl extends HibernateBaseDaoImpl<AnnotatedObjectImpl> implements SearchableDao {

    public SearchableDaoImpl( ) {
        super( AnnotatedObjectImpl.class, null);
    }

    public SearchableDaoImpl( EntityManager entityManager ) {
        super( AnnotatedObjectImpl.class, entityManager);
    }

    public SearchableDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( AnnotatedObjectImpl.class, entityManager, intactSession);
    }

    public Integer countByQuery( Class<? extends Searchable> searchableClass, SearchableQuery query ) {
        Criteria criteria = new SearchableCriteriaBuilder( query )
                .createCriteria( searchableClass, getSession() );
        criteria.setProjection( Projections.countDistinct( "ac" ) );

        int count = 0;

        try {
            List<Integer> list = criteria.list();

            for ( Integer entityCount : list ) {
                count += entityCount;
            }
        } catch (HibernateException e) {
            throw new IntactException("Problem counting by query using class "+searchableClass.getName()+" and query: "+query, e);
        }

        return count;
    }

    public Map<Class<? extends Searchable>, Integer> countByQuery( SearchableQuery query ) {
        return countByQuery( STANDARD_SEARCHABLES, query );
    }

    public Map<Class<? extends Searchable>, Integer> countByQuery( Class<? extends Searchable>[] searchableClasses, SearchableQuery query ) {
        Map<Class<? extends Searchable>, Integer> counts = new HashMap<Class<? extends Searchable>, Integer>();

        for ( Class<? extends Searchable> searchable : searchableClasses ) {
            counts.put( searchable, countByQuery( searchable, query ) );
        }

        return counts;
    }

    public List<? extends Searchable> getByQuery( SearchableQuery query, Integer firstResult, Integer maxResults ) {
        return getByQuery( STANDARD_SEARCHABLES, query, firstResult, maxResults );
    }

    public List<? extends Searchable> getByQuery( Class<? extends Searchable>[] searchableClasses, SearchableQuery query, Integer firstResult, Integer maxResults ) {
        return getByQuery(searchableClasses, query, firstResult, maxResults, null, true);
    }

    public List<? extends Searchable> getByQuery(Class<? extends Searchable>[] searchableClasses, SearchableQuery query, Integer firstResult, Integer maxResults, String sortProperty, boolean sortAsc) {
        List<Searchable> results = new ArrayList<Searchable>();

        for ( Class searchable : searchableClasses ) {
            int resultsFoundSoFar = results.size();
            int resultsToFetch = maxResults - resultsFoundSoFar;

            results.addAll( getByQuery( searchable, query, firstResult, resultsToFetch, sortProperty, sortAsc ) );

            if ( results.size() == maxResults ) {
                break;
            }
        }

        return results;
    }

    public List<? extends Searchable> getByQuery( Class<? extends Searchable> searchableClass, SearchableQuery query, Integer firstResult, Integer maxResults ) {
         return getByQuery(searchableClass, query, firstResult, maxResults, null, true);
    }

    public List<? extends Searchable> getByQuery(Class<? extends Searchable> searchableClass, SearchableQuery query, Integer firstResult, Integer maxResults, String sortProperty, boolean sortAsc) {
        List<String> acs = getAcsByQuery( searchableClass, query, firstResult, maxResults );

        if ( acs.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }

        Criteria crit = getSession().createCriteria( searchableClass )
                .add( Restrictions.in( "ac", acs ) );

        if (sortProperty != null) {

            // if the property contains a colon (.), use the first token to create
            // an alias
            String alias = "";
            if (sortProperty.contains(".")) {
                String[] tokens = sortProperty.split("\\.");
                String prop = tokens[0];
                sortProperty = tokens[1];

                alias = "propalias"+System.currentTimeMillis();

                crit.createAlias(prop, alias);
                
                alias = alias+".";
            }

            if (sortAsc) {
                crit.addOrder(Order.asc(alias+sortProperty));
            } else {
                crit.addOrder(Order.desc(alias+sortProperty));
            }
        }

        return crit.list();
    }

    public List<String> getAcsByQuery( Class<? extends Searchable> searchableClass, SearchableQuery query, Integer firstResult, Integer maxResults ) {
        Criteria crit = new SearchableCriteriaBuilder( query )
                .createCriteria( searchableClass, getSession() )
                .setProjection( Projections.distinct( Property.forName( "ac" ) ) );

        if ( firstResult != null ) {
            crit.setFirstResult( firstResult );
        }

        if ( maxResults != null ) {
            crit.setMaxResults( maxResults );
        }

        return crit.list();
    }
}
