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
package uk.ac.ebi.intact.persistence.dao.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * A phrase represent a group of terms
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class QueryPhrase implements Serializable {

    private Collection<QueryTerm> terms;

    public QueryPhrase() {
        this.terms = new HashSet<QueryTerm>();
    }

    public QueryPhrase( Collection<QueryTerm> terms ) {
        this.terms = terms;
    }


    public Collection<QueryTerm> getTerms() {
        return terms;
    }

    public void setTerms( Collection<QueryTerm> terms ) {
        this.terms = terms;
    }

    public boolean isOnlyWildcard() {
        if ( terms != null && terms.size() == 1 ) {
            return terms.iterator().next().isOnlyWildcard();
        }
        return false;
    }
}
