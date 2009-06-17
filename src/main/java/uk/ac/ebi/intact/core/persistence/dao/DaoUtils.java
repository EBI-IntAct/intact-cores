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
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.core.persistence.dao.query.impl.AutoAddWildcardConverter;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.StandardAutoAddWildcardConverter;

/**
 * General DAO utilities
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Oct-2006</pre>
 */
public class DaoUtils {

    private DaoUtils() {
    }

    /**
     * Removes the wildcards (*) from the beginning and end of a value,
     * so it can be used in a like query
     *
     * @param value the value to use
     *
     * @return the value with replaced wildcards
     */
    public static String replaceWildcardsByPercent( String value ) {
        if ( value.startsWith( "*" ) ) {
            value = value.replaceFirst( "\\*", "%" );
        }

        if ( value.endsWith( "*" ) ) {
            value = value.substring( 0, value.length() - 1 ) + "%";
        }

        value = value.replaceAll( "\\* ", "% " );
        value = value.replaceAll( " \\*", " %" );
        value = value.replaceAll( "\\*,", "%," );
        value = value.replaceAll( ",\\*", "%" );
        value = value.replaceAll( "\\+\\*", "+%" );
        value = value.replaceAll( "\\-\\*", "-%" );

        return value;
    }

    public static boolean isValueForLike( String value ) {
        String replacedValue = replaceWildcardsByPercent( value );

        return ( replacedValue.startsWith( "%" ) || replacedValue.endsWith( "%" ) );
    }

    /**
     * Adds wildcards to a searchable query using the standard AutoAddWildcardConverter
     *
     * @param query the query to convert
     *
     * @return a query with wildcards where necessary
     */
    public static SearchableQuery autoAddWildcards( SearchableQuery query ) {
        return autoAddWildcards( query, new StandardAutoAddWildcardConverter() );
    }

    /**
     * Adds wildcards to a searchable query using the provided AutoAddWildcardConverter
     *
     * @param query     the query to convert
     * @param converter the converter to use
     *
     * @return a query with wildcards where necessary
     */
    public static SearchableQuery autoAddWildcards( SearchableQuery query, AutoAddWildcardConverter converter ) {
        return converter.autoAddWildCards( query );
    }
}
