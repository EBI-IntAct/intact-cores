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

import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */

public enum QueryModifier {

    /**
     * Match anything before the term
     */
    WILDCARD_START( '%', new Character[]{'*'}, QueryModifierPosition.BEFORE_TERM ),

    /**
     * Match anything after the term
     */
    WILDCARD_END( '%', new Character[]{'*'}, QueryModifierPosition.AFTER_TERM ),

    /**
     * The value is just a wildcard
     */
    WILDCARD_VALUE( '%', new Character[]{'*'}, QueryModifierPosition.ALL_TERM ),

    /**
     * Include the term (AND conjunction)
     */
    INCLUDE( '+', QueryModifierPosition.BEFORE_TERM ),

    /**
     * Exclude the term (AND NOT)
     */
    EXCLUDE( '-', QueryModifierPosition.BEFORE_TERM ),

    /**
     * Phrase delimiter
     */
    PHRASE_DELIM( '"', new Character[]{'\''}, QueryModifierPosition.BEFORE_AFTER_TERM );

    private Character symbol;
    private Character[] synonyms;
    private QueryModifierPosition position;

    private QueryModifier( Character symbol, QueryModifierPosition position ) {
        this( symbol, new Character[0], position );
    }

    private QueryModifier( Character symbol, Character[] synonyms, QueryModifierPosition position ) {
        this.symbol = symbol;
        this.synonyms = synonyms;
        this.position = position;
    }

    public Character getSymbol() {
        return symbol;
    }

    public Character[] getSynonyms() {
        return synonyms;
    }

    public Character[] allPossibleSymbols() {
        Character[] allSymbols = new Character[synonyms.length + 1];

        allSymbols[0] = symbol;

        for ( int i = 0; i < synonyms.length; i++ ) {
            allSymbols[i + 1] = synonyms[i];
        }

        return allSymbols;
    }

    public QueryModifierPosition getPosition() {
        return position;
    }

    public static QueryModifier[] identifyModifiers( String value ) {
        Set<QueryModifier> modifiers = new HashSet<QueryModifier>();

        // first check for "all-value" wildcads (the value is just a wildcard)
        boolean valueIsJustAWildcard = false;
        if ( value.length() == 1 ) {
            for ( Character c : QueryModifier.WILDCARD_VALUE.allPossibleSymbols() ) {
                if ( value.equals( c.toString() ) ) {
                    valueIsJustAWildcard = true;
                    modifiers.add( QueryModifier.WILDCARD_VALUE );
                    break;
                }
            }
        }

        // if the value is not just a wildcard, identify the modifiers
        if ( !valueIsJustAWildcard ) {
            for ( QueryModifier modifier : QueryModifier.values() ) {
                switch ( modifier.getPosition() ) {
                    case ALL_TERM:
                        for ( Character c : modifier.allPossibleSymbols() ) {
                            if ( value.length() == 1 ) {
                                if ( value.equals( c.toString() ) ) {
                                    modifiers.add( modifier );
                                }
                            }
                        }
                        break;
                    case BEFORE_TERM:
                        for ( Character c : modifier.allPossibleSymbols() ) {
                            if ( value.startsWith( String.valueOf( c ) ) ) {
                                modifiers.add( modifier );
                            }
                        }
                        break;
                    case AFTER_TERM:
                        for ( Character c : modifier.allPossibleSymbols() ) {
                            if ( value.endsWith( String.valueOf( c ) ) ) {
                                modifiers.add( modifier );
                            }
                        }
                        break;
                }
            }
        }
        return modifiers.toArray( new QueryModifier[modifiers.size()] );
    }

    @Override
    public String toString() {
        return "mod{" + symbol + " - " + position + "}";
    }

}
