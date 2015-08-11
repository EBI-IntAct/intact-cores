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
package uk.ac.ebi.intact.core.persistence.svc.impl;

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.core.persistence.dao.DaoUtils;
import uk.ac.ebi.intact.core.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.StandardQueryPhraseConverter;
import uk.ac.ebi.intact.core.persistence.svc.SearchService;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Search behaviour: first search using the exact query provided. If no results are returned
 * encapsulate each of the query terms with wildcards so potentially more results can be returned.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Service
public class SimpleSearchService implements SearchService {

    @Autowired
    private SearchableDao searchableDao;

    private String sortProperty;
    private boolean sortAsc;

    public SimpleSearchService() {
    }

    public SimpleSearchService(String sortProperty, boolean sortAsc) {
        this.sortProperty = sortProperty;
        this.sortAsc = sortAsc;
    }

    public int count( Class<? extends Searchable> searchable, String query ) {
        return count( searchable, createSimpleQuery( query ) );
    }

    public int count( Class<? extends Searchable> searchable, SearchableQuery query ) {
        int count = getDao().countByQuery( searchable, query );

        if ( count > 0 ) {
            return count;
        }

        return getDao().countByQuery( searchable, createSimpleQueryWithWildcards( query ) );
    }

    public Map<Class<? extends Searchable>, Integer> count( Class<? extends Searchable>[] searchables, String query ) {
        return getDao().countByQuery( searchables, createSimpleQuery( query ) );
    }

    public Map<Class<? extends Searchable>, Integer> count( Class<? extends Searchable>[] searchables, SearchableQuery query ) {
        Map<Class<? extends Searchable>, Integer> count = getDao().countByQuery( searchables, query );

        int total = 0;

        for ( int c : count.values() ) {
            total += c;
        }

        if ( total > 0 ) {
            return count;
        }

        return getDao().countByQuery( searchables, createSimpleQueryWithWildcards( query ) );
    }

    public <S extends Searchable> List<S> search( Class<S> searchable, String query, Integer firstResult, Integer maxResults ) {
        return search( searchable, createSimpleQuery( query ), firstResult, maxResults );
    }

    public <S extends Searchable> List<S> search( Class<S> searchable, SearchableQuery query, Integer firstResult, Integer maxResults ) {
        return ( List<S> ) search( new Class[]{searchable}, query, firstResult, maxResults );
    }

    public List<? extends Searchable> search( Class<? extends Searchable>[] searchables, String query, Integer firstResult, Integer maxResults ) {
        return search( searchables, createSimpleQuery( query ), firstResult, maxResults );
    }

    public List<? extends Searchable> search( Class<? extends Searchable>[] searchables, SearchableQuery query, Integer firstResult, Integer maxResults ) {
        if ( firstResult == null ) firstResult = 0;
        if ( maxResults == null ) maxResults = Integer.MAX_VALUE;

        List<? extends Searchable> results = getDao().getByQuery( searchables, query, firstResult, maxResults, sortProperty, sortAsc );

        if ( results.size() > 0 ) {
            return results;
        }

        return getDao().getByQuery( searchables, createSimpleQueryWithWildcards( query ), firstResult, maxResults, sortProperty, sortAsc );
    }

    private SearchableQuery createSimpleQuery( String query ) {
        QueryPhrase phrase = new StandardQueryPhraseConverter().objectToPhrase( query );

        SearchableQuery sq = new SearchableQuery();

        if ( !phrase.isOnlyWildcard() ) {
            sq.setAc( phrase );
            sq.setShortLabel( phrase );
            sq.setDescription( phrase );
            sq.setAnnotationText( phrase );
            sq.setXref( phrase );
            sq.setDisjunction( true );
        }

        return sq;
    }

    private SearchableQuery createSimpleQueryWithWildcards( SearchableQuery query ) {
        return DaoUtils.autoAddWildcards( query );
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public SearchableDao getDao() {
        if (searchableDao == null) {
            searchableDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getSearchableDao();
        }
        return searchableDao;
    }

    public void setDao(SearchableDao searchableDao) {
        this.searchableDao = searchableDao;
    }
}
