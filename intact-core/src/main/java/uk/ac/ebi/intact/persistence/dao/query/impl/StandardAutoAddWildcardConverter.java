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
package uk.ac.ebi.intact.persistence.dao.query.impl;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.query.QueryModifier;
import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.persistence.dao.query.QueryTerm;

/**
 * Standard implementation of the AutoAddWildCardConverter
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class StandardAutoAddWildcardConverter implements AutoAddWildcardConverter {

    public SearchableQuery autoAddWildCards( SearchableQuery query ) {
        query.setAc( addAutomaticWildcardsToPhrase( query.getAc() ) );
        query.setShortLabel( addAutomaticWildcardsToPhrase( query.getShortLabel() ) );
        query.setDescription( addAutomaticWildcardsToPhrase( query.getDescription() ) );
        query.setAnnotationText( addAutomaticWildcardsToPhrase( query.getAnnotationText() ) );
        query.setFullText( addAutomaticWildcardsToPhrase( query.getFullText() ) );
        query.setXref( addAutomaticWildcardsToPhrase( query.getXref() ) );

        return query;
    }

    private QueryPhrase addAutomaticWildcardsToPhrase( QueryPhrase originalPhrase ) {
        if ( originalPhrase == null ) return null;

        QueryPhrase phrase = new QueryPhrase();

        for ( QueryTerm term : originalPhrase.getTerms() ) {
            addStartAndEndPercentIfNecessary( term );
            phrase.getTerms().add( term );
        }

        return phrase;
    }

    private static void addStartAndEndPercentIfNecessary( QueryTerm term ) {
        if ( term.hasModifier( QueryModifier.PHRASE_DELIM ) ) {
            return;
        }

        String acPrefix = IntactContext.getCurrentInstance().getConfig().getAcPrefix();

        if ( !term.startsWith( acPrefix ) && !term.hasModifier( QueryModifier.WILDCARD_START ) ) {
            term.addModifier( QueryModifier.WILDCARD_START );
        }

        if ( !term.startsWith( acPrefix ) && !term.hasModifier( QueryModifier.WILDCARD_END ) ) {
            term.addModifier( QueryModifier.WILDCARD_END );
        }
    }

}
