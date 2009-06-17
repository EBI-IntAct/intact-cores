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
package uk.ac.ebi.intact.core.persistence.dao.query;

/**
 * Converts an object to phrase and viceversa
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.2
 */
public interface QueryPhraseConverter<T> {

    /**
     * Converts an <code>Object</code> to a <code>QueryPhrase</code>
     */
    QueryPhrase objectToPhrase( T value ) throws QueryPhraseException;

    /**
     * Converts an  <code>QueryPhrase</code> to an <code>Object</code>
     */
    T phraseToObject( QueryPhrase phrase ) throws QueryPhraseException;
}
