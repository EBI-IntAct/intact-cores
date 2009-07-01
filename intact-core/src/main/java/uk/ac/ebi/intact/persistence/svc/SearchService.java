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
package uk.ac.ebi.intact.persistence.svc;

import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;

import java.util.List;
import java.util.Map;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public interface SearchService {

    static final Class<? extends Searchable>[] STANDARD_SEARCHABLES = SearchableDao.STANDARD_SEARCHABLES;

    int count( Class<? extends Searchable> searchable, String query );

    int count( Class<? extends Searchable> searchable, SearchableQuery query );

    Map<Class<? extends Searchable>, Integer> count( Class<? extends Searchable>[] searchables, String query );

    Map<Class<? extends Searchable>, Integer> count( Class<? extends Searchable>[] searchables, SearchableQuery query );

    <S extends Searchable> List<S> search( Class<S> searchable, String query, Integer firstResult, Integer maxResults );

    <S extends Searchable> List<S> search( Class<S> searchable, SearchableQuery query, Integer firstResult, Integer maxResults );

    List<? extends Searchable> search( Class<? extends Searchable>[] searchables, String query, Integer firstResult, Integer maxResults );

    List<? extends Searchable> search( Class<? extends Searchable>[] searchables, SearchableQuery query, Integer firstResult, Integer maxResults );
}
