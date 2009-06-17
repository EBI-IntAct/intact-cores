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
package uk.ac.ebi.intact.core.persistence.dao.query.impl;

import uk.ac.ebi.intact.core.persistence.dao.query.QueryModifier;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryPhraseException;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryTerm;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryTermConverter;

/**
 * Convert a string to a term and viceversa
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class StandardQueryTermConverter implements QueryTermConverter {

    public QueryTerm stringToTerm( String value ) throws QueryPhraseException {
        if ( value == null ) {
            throw new NullPointerException( "value cannot be null" );
        }

        QueryModifier[] modifiers = QueryModifier.identifyModifiers( value );

        for ( QueryModifier modifier : modifiers ) {
            value = standardiseModifierSymbol( value, modifier );
            value = removeModifierExceptWildcards( value, modifier );
        }

        QueryTerm term = new QueryTerm( value );
        term.setModifiers( modifiers );

        return term;
    }

    public String termToString( QueryTerm term ) throws QueryPhraseException {
        String value = term.getValue();

        if ( value.contains( " " ) || value.contains( "," ) ) {
            value = QueryModifier.PHRASE_DELIM.getSymbol() + value + QueryModifier.PHRASE_DELIM.getSymbol();
        }

        for ( QueryModifier modifier : term.getModifiers() ) {
            switch ( modifier.getPosition() ) {
                case BEFORE_TERM:
                    if ( !value.startsWith( modifier.getSymbol().toString() ) ) {
                        value = modifier.getSymbol() + value;
                    }
                    break;
                case AFTER_TERM:
                    if ( !value.endsWith( modifier.getSymbol().toString() ) ) {
                        value = value + modifier.getSymbol();
                    }
                    break;
                case BEFORE_AFTER_TERM:
                    if ( !value.startsWith( modifier.getSymbol().toString() ) &&
                         !value.endsWith( modifier.getSymbol().toString() ) ) {
                        value = modifier.getSymbol() + value + modifier.getSymbol();
                    }
                    break;
            }
        }

        return value;
    }


    private String removeModifierExceptWildcards( String value, QueryModifier modifier ) {
        String valueWithoutModifier = value;

        switch ( modifier ) {
            case EXCLUDE:
                valueWithoutModifier = value.substring( 1 );
                break;
            case INCLUDE:
                valueWithoutModifier = value.substring( 1 );
                break;
            case WILDCARD_START:
                valueWithoutModifier = value.substring( 1 );
                break;
            case WILDCARD_END:
                valueWithoutModifier = value.substring( 0, value.length() - 1 );
                break;
            case PHRASE_DELIM:
                valueWithoutModifier = value.substring( 1, value.length() - 1 );
                break;
        }

        return valueWithoutModifier.trim();
    }

    private String standardiseModifierSymbol( String value, QueryModifier modifier ) {
        String stdValue = value;

        switch ( modifier.getPosition() ) {
            case AFTER_TERM:
                stdValue = value.substring( 0, value.length() - 1 ) + modifier.getSymbol();
                break;
            case BEFORE_TERM:
                stdValue = modifier.getSymbol() + value.substring( 1 );
                break;
            case BEFORE_AFTER_TERM:
                stdValue = value.substring( 0, value.length() - 1 ) + modifier.getSymbol();
                stdValue = modifier.getSymbol() + stdValue.substring( 1 );
                break;
        }

        return stdValue;
    }
}
