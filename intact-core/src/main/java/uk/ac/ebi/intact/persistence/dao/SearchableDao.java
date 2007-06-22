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
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;

import java.util.List;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Mockable
public interface SearchableDao extends BaseDao<Session> {

    static final Class<? extends Searchable>[] STANDARD_SEARCHABLES = new Class[]{Experiment.class, InteractionImpl.class, ProteinImpl.class, NucleicAcidImpl.class, CvObject.class};

    Integer countByQuery( Class<? extends Searchable> searchableClass, SearchableQuery query );

    Map<Class<? extends Searchable>, Integer> countByQuery( SearchableQuery query );

    Map<Class<? extends Searchable>, Integer> countByQuery( Class<? extends Searchable>[] searchableClasses, SearchableQuery query );

    List<? extends Searchable> getByQuery( SearchableQuery query, Integer firstResults, Integer maxResults );

    List<? extends Searchable> getByQuery( Class<? extends Searchable>[] searchableClasses, SearchableQuery query, Integer firstResult, Integer maxResults );

    List<? extends Searchable> getByQuery( Class<? extends Searchable> searchableClass, SearchableQuery query, Integer firstResult, Integer maxResults );

    List<String> getAcsByQuery( Class<? extends Searchable> searchableClass, SearchableQuery query, Integer firstResult, Integer maxResults );
}
