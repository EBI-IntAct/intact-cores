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

import uk.ac.ebi.intact.core.persistence.dao.query.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard builder of phrases from Strings
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.2
 */
public class StandardQueryPhraseConverter implements QueryPhraseConverter<String> {

    private static final String REPLACED_SPACE = "&nbsp;";
    private static final String REPLACED_COMMA = "&comma;";


    public QueryPhrase objectToPhrase( String value ) throws QueryPhraseException {
        if ( value == null ) {
            throw new NullPointerException( "Value cannot be null" );
        }

        List<QueryTerm> terms = new ArrayList<QueryTerm>();

        // if the value contains quotes, transform any space or comma inside the quotes
        // so they won't be used to separate the different tokens for the value
        if ( value.contains( "\"" ) ) {
            value = replaceSymbolsInPhrases( value );
        }

        value = value.trim();

        // split the value in tokens by space or comma
        String[] strTerms = value.split( "\\s|," );

        StandardQueryTermConverter termConverter = new StandardQueryTermConverter();

        for ( String strTerm : strTerms ) {
            strTerm = replacedToValue( strTerm );
            strTerm = removeQuotesIfNecessary( strTerm );

            QueryTerm term = termConverter.stringToTerm( strTerm );
            terms.add( term );
        }

        return new QueryPhrase( terms );
    }

    public String phraseToObject( QueryPhrase phrase ) throws QueryPhraseException {
        if ( phrase == null ) return null;

        StringBuffer sb = new StringBuffer( phrase.getTerms().size() * 8 );

        StandardQueryTermConverter termConverter = new StandardQueryTermConverter();

        int i = 0;
        for ( QueryTerm term : phrase.getTerms() ) {
            if ( i > 0 ) {
                sb.append( "," );
            }
            sb.append( termConverter.termToString( term ) );
            i++;
        }

        return sb.toString();
    }

    private static String replaceSymbolsInPhrases( String value ) {
        boolean isInsidePhrase = false;

        StringBuffer replacedValue = new StringBuffer();
        StringBuffer currentPhrase = new StringBuffer();

        char[] valueChars = value.toCharArray();

        for ( char c : valueChars ) {
            for ( Character phraseDelim : QueryModifier.PHRASE_DELIM.allPossibleSymbols() ) {
                if ( c == phraseDelim ) {
                    if ( isInsidePhrase ) {
                        isInsidePhrase = false;

                        String replacedPhrase = valueToReplaced( currentPhrase.toString() );
                        replacedValue.append( replacedPhrase );
                    } else {
                        isInsidePhrase = true;
                    }
                }
            }

            if ( isInsidePhrase ) {
                currentPhrase.append( c );
            } else {
                replacedValue.append( c );
            }
        }

        return replacedValue.toString();
    }

    private static String valueToReplaced( String value ) {
        String replaced = value.replaceAll( "\\s", REPLACED_SPACE );
        replaced = replaced.replaceAll( ",", REPLACED_COMMA );

        return replaced;
    }

    private static String replacedToValue( String replaced ) {
        String value = replaced.replaceAll( REPLACED_SPACE, " " );
        value = value.replaceAll( REPLACED_COMMA, "," );

        return value;
    }

    private static String removeQuotesIfNecessary( String value ) {
        if ( !value.contains( "\"" ) ) {
            return value;
        }

        boolean initialPercent = value.startsWith( "%" );
        boolean endPercent = value.endsWith( "%" );

        if ( initialPercent ) {
            value = value.substring( 1 );
        }

        if ( endPercent ) {
            value = value.substring( 0, value.length() - 1 );
        }

        if ( value.startsWith( "\"" ) ) {
            value = value.substring( 1 );
        }

        if ( value.endsWith( "\"" ) ) {
            value = value.substring( 0, value.length() - 1 );
        }

        if ( initialPercent ) value = "%" + value;
        if ( endPercent ) value = value + "%";

        return value;
    }

}
