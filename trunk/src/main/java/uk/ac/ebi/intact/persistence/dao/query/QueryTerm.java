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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents a term in a query
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class QueryTerm implements Serializable {

    private String value;
    private List<QueryModifier> modifiers;

    public QueryTerm() {
    }

    public QueryTerm( String value ) {
        this.value = value;
        this.modifiers = new ArrayList<QueryModifier>();
    }

    public QueryTerm( String value, QueryModifier... modifiers ) {
        this( value, Arrays.asList( modifiers ) );
    }

    public QueryTerm( String value, Collection<QueryModifier> modifiers ) {
        this.value = value;
        this.modifiers = new ArrayList<QueryModifier>( modifiers );
        modifiers.addAll( modifiers );
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public QueryModifier[] getModifiers() {
        if ( modifiers == null ) {
            modifiers = new ArrayList<QueryModifier>();
        }
        return modifiers.toArray( new QueryModifier[modifiers.size()] );
    }

    public void setModifiers( QueryModifier[] modifiers ) {
        this.modifiers = new ArrayList<QueryModifier>( Arrays.asList( modifiers ) );
    }

    public boolean isOnlyWildcard() {
        if ( modifiers.size() == 1 ) {
            return ( modifiers.get( 0 ) == QueryModifier.WILDCARD_VALUE );
        }
        return false;
    }

    public boolean startsWith( String prefix ) {
        return value.startsWith( prefix );
    }

    public boolean endsWith( String suffix ) {
        return value.endsWith( suffix );
    }

    public boolean hasModifier( QueryModifier modifier ) {
        for ( QueryModifier mod : getModifiers() ) {
            if ( mod == modifier ) {
                return true;
            }
        }
        return false;
    }

    public boolean addModifier( QueryModifier modifier ) {
        return modifiers.add( modifier );
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        QueryTerm queryTerm = ( QueryTerm ) o;

        if ( modifiers != null ? !modifiers.equals( queryTerm.modifiers ) : queryTerm.modifiers != null ) return false;
        if ( value != null ? !value.equals( queryTerm.value ) : queryTerm.value != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( value != null ? value.hashCode() : 0 );
        result = 31 * result + ( modifiers != null ? modifiers.hashCode() : 0 );
        return result;
    }
}
