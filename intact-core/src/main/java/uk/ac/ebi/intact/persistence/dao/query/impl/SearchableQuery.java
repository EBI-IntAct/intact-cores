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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Class to be used as query value for searches. It is used
 * in the {@link uk.ac.ebi.intact.persistence.dao.SearchItemDao}
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class SearchableQuery implements Serializable {

    private QueryPhrase ac;
    private QueryPhrase acOrId;
    private QueryPhrase shortLabel;
    private QueryPhrase description;
    private QueryPhrase fullText;
    private QueryPhrase xref;
    private QueryPhrase cvDatabaseLabel;
    private QueryPhrase annotationText;
    private QueryPhrase cvTopicLabel;
    private QueryPhrase cvIdentificationLabel;
    private QueryPhrase cvInteractionLabel;
    private QueryPhrase cvInteractionTypeLabel;
    private boolean includeCvInteractionChildren;
    private boolean includeCvIdentificationChildren;
    private boolean includeCvInteractionTypeChildren;
    private boolean disjunction;
    private boolean ignoreCase = true;

    public SearchableQuery() {
    }

    public QueryPhrase getAc() {
        return ac;
    }

    public void setAc( QueryPhrase ac ) {
        this.ac = ac;
    }

    public QueryPhrase getAcOrId() {
        return acOrId;
    }

    public void setAcOrId( QueryPhrase acOrId ) {
        this.acOrId = acOrId;
    }

    public QueryPhrase getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel( QueryPhrase shortLabel ) {
        this.shortLabel = shortLabel;
    }

    public QueryPhrase getDescription() {
        return description;
    }

    public void setDescription( QueryPhrase description ) {
        this.description = description;
    }

    public QueryPhrase getFullText() {
        return fullText;
    }

    public void setFullText( QueryPhrase fullText ) {
        this.fullText = fullText;
    }

    public QueryPhrase getXref() {
        return xref;
    }

    public void setXref( QueryPhrase xref ) {
        this.xref = xref;
    }

    public QueryPhrase getCvDatabaseLabel() {
        return cvDatabaseLabel;
    }

    public void setCvDatabaseLabel( QueryPhrase cvDatabaseLabel ) {
        this.cvDatabaseLabel = cvDatabaseLabel;
    }

    public QueryPhrase getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText( QueryPhrase annotationText ) {
        this.annotationText = annotationText;
    }

    public QueryPhrase getCvTopicLabel() {
        return cvTopicLabel;
    }

    public void setCvTopicLabel( QueryPhrase cvTopicLabel ) {
        this.cvTopicLabel = cvTopicLabel;
    }

    public QueryPhrase getCvIdentificationLabel() {
        return cvIdentificationLabel;
    }

    public void setCvIdentificationLabel( QueryPhrase cvIdentificationLabel ) {
        this.cvIdentificationLabel = cvIdentificationLabel;
    }

    public QueryPhrase getCvInteractionLabel() {
        return cvInteractionLabel;
    }

    public void setCvInteractionLabel( QueryPhrase cvInteractionLabel ) {
        this.cvInteractionLabel = cvInteractionLabel;
    }

    public QueryPhrase getCvInteractionTypeLabel() {
        return cvInteractionTypeLabel;
    }

    public void setCvInteractionTypeLabel( QueryPhrase cvInteractionTypeLabel ) {
        this.cvInteractionTypeLabel = cvInteractionTypeLabel;
    }

    public boolean isIncludeCvInteractionChildren() {
        return includeCvInteractionChildren;
    }

    public void setIncludeCvInteractionChildren( boolean includeCvInteractionChildren ) {
        this.includeCvInteractionChildren = includeCvInteractionChildren;
    }

    public boolean isIncludeCvIdentificationChildren() {
        return includeCvIdentificationChildren;
    }

    public void setIncludeCvIdentificationChildren( boolean includeCvIdentificationChildren ) {
        this.includeCvIdentificationChildren = includeCvIdentificationChildren;
    }

    public boolean isIncludeCvInteractionTypeChildren() {
        return includeCvInteractionTypeChildren;
    }

    public void setIncludeCvInteractionTypeChildren( boolean includeCvInteractionTypeChildren ) {
        this.includeCvInteractionTypeChildren = includeCvInteractionTypeChildren;
    }

    public boolean isDisjunction() {
        return disjunction;
    }

    public void setDisjunction( boolean disjunction ) {
        this.disjunction = disjunction;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( 256 );
        sb.append( "{" );

        StandardQueryPhraseConverter converter = new StandardQueryPhraseConverter();

        appendValueToStringBuffer( sb, "ac", converter.phraseToObject( ac ) );
        appendValueToStringBuffer( sb, "acOrId", converter.phraseToObject( acOrId ) );
        appendValueToStringBuffer( sb, "shortLabel", converter.phraseToObject( shortLabel ) );
        appendValueToStringBuffer( sb, "description", converter.phraseToObject( description ) );
        appendValueToStringBuffer( sb, "fullText", converter.phraseToObject( fullText ) );
        appendValueToStringBuffer( sb, "xref", converter.phraseToObject( xref ) );
        appendValueToStringBuffer( sb, "cvDatabaseLabel", converter.phraseToObject( cvDatabaseLabel ) );
        appendValueToStringBuffer( sb, "annotationText", converter.phraseToObject( annotationText ) );
        appendValueToStringBuffer( sb, "cvTopicLabel", converter.phraseToObject( cvTopicLabel ) );
        appendValueToStringBuffer( sb, "cvIdentificationLabel", converter.phraseToObject( cvIdentificationLabel ) );
        appendValueToStringBuffer( sb, "cvInteractionLabel", converter.phraseToObject( cvInteractionLabel ) );
        appendValueToStringBuffer( sb, "cvInteractionTypeLabel", converter.phraseToObject( cvInteractionTypeLabel ) );

        if ( includeCvInteractionChildren )
            appendValueToStringBuffer( sb, "includeCvInteractionChildren", includeCvInteractionChildren );

        if ( includeCvIdentificationChildren )
            appendValueToStringBuffer( sb, "includeCvIdentificationChildren", includeCvIdentificationChildren );

        if ( includeCvInteractionTypeChildren )
            appendValueToStringBuffer( sb, "includeCvInteractionTypeChildren", includeCvInteractionTypeChildren );

        if ( disjunction )
            appendValueToStringBuffer( sb, "disjunction", disjunction );

        sb.deleteCharAt( sb.length() - 1 );

        sb.append( '}' );

        return sb.toString();
    }

    private static void appendValueToStringBuffer( StringBuffer sb, String prop, Object value ) {
        if ( value != null && value.toString().length() > 0 ) {
            sb.append( prop + "='" ).append( value ).append( '\'' ).append( ";" );
        }
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SearchableQuery that = ( SearchableQuery ) o;

        if ( disjunction != that.disjunction ) return false;
        if ( includeCvIdentificationChildren != that.includeCvIdentificationChildren ) return false;
        if ( includeCvInteractionChildren != that.includeCvInteractionChildren ) return false;
        if ( includeCvInteractionTypeChildren != that.includeCvInteractionTypeChildren ) return false;
        if ( ac != null ? !ac.equals( that.ac ) : that.ac != null ) return false;
        if ( annotationText != null ? !annotationText.equals( that.annotationText ) : that.annotationText != null )
            return false;
        if ( cvDatabaseLabel != null ? !cvDatabaseLabel.equals( that.cvDatabaseLabel ) : that.cvDatabaseLabel != null )
            return false;
        if ( cvIdentificationLabel != null ? !cvIdentificationLabel.equals( that.cvIdentificationLabel ) : that.cvIdentificationLabel != null )
            return false;
        if ( cvInteractionLabel != null ? !cvInteractionLabel.equals( that.cvInteractionLabel ) : that.cvInteractionLabel != null )
            return false;
        if ( cvInteractionTypeLabel != null ? !cvInteractionTypeLabel.equals( that.cvInteractionTypeLabel ) : that.cvInteractionTypeLabel != null )
            return false;
        if ( cvTopicLabel != null ? !cvTopicLabel.equals( that.cvTopicLabel ) : that.cvTopicLabel != null )
            return false;
        if ( description != null ? !description.equals( that.description ) : that.description != null ) return false;
        if ( fullText != null ? !fullText.equals( that.fullText ) : that.fullText != null ) return false;
        if ( shortLabel != null ? !shortLabel.equals( that.shortLabel ) : that.shortLabel != null ) return false;
        if ( acOrId != null ? !acOrId.equals( that.acOrId ) : that.acOrId != null ) return false;
        if ( xref != null ? !xref.equals( that.xref ) : that.xref != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( ac != null ? ac.hashCode() : 0 );
        result = 31 * result + ( acOrId != null ? acOrId.hashCode() : 0 );
        result = 31 * result + ( shortLabel != null ? shortLabel.hashCode() : 0 );
        result = 31 * result + ( description != null ? description.hashCode() : 0 );
        result = 31 * result + ( fullText != null ? fullText.hashCode() : 0 );
        result = 31 * result + ( xref != null ? xref.hashCode() : 0 );
        result = 31 * result + ( cvDatabaseLabel != null ? cvDatabaseLabel.hashCode() : 0 );
        result = 31 * result + ( annotationText != null ? annotationText.hashCode() : 0 );
        result = 31 * result + ( cvTopicLabel != null ? cvTopicLabel.hashCode() : 0 );
        result = 31 * result + ( cvIdentificationLabel != null ? cvIdentificationLabel.hashCode() : 0 );
        result = 31 * result + ( cvInteractionLabel != null ? cvInteractionLabel.hashCode() : 0 );
        result = 31 * result + ( cvInteractionTypeLabel != null ? cvInteractionTypeLabel.hashCode() : 0 );
        result = 31 * result + ( includeCvInteractionChildren ? 1 : 0 );
        result = 31 * result + ( includeCvIdentificationChildren ? 1 : 0 );
        result = 31 * result + ( includeCvInteractionTypeChildren ? 1 : 0 );
        result = 31 * result + ( disjunction ? 1 : 0 );
        return result;
    }

    /**
     * Create a <code>SearchableQuery</code> from a String. Using a regex pattern, gets the properties
     * and values from the expression and creates the query object using reflection
     */
    public static SearchableQuery parseSearchableQuery( String searchableQueryStr ) {
        if ( !isSearchableQuery( searchableQueryStr ) ) {
            throw new IntactException( "Not a parseable SearchableQuery: " + searchableQueryStr );
        }

        SearchableQuery query = new SearchableQuery();

        searchableQueryStr = searchableQueryStr.substring( 1, searchableQueryStr.length() - 1 );

        String[] tokens = searchableQueryStr.split( ";" );

        for ( String token : tokens ) {
            String[] propAndValue = token.split( "=" );

            String propName = propAndValue[0];

            String propValue = propAndValue[1];

            // remove quotes if needed
            if ( propValue.startsWith( "'" ) && propValue.endsWith( "'" ) ) {
                propValue = propValue.substring( 1, propValue.length() - 1 );
            }

            try {
                addPropertyWithReflection( query, propName, propValue );
            }
            catch ( Exception e ) {
                throw new IntactException( "Exception parsing " + propName + "=" + propValue + " in SearchQuery from String: " + searchableQueryStr, e );
            }
        }

        return query;
    }

    private static void addPropertyWithReflection( SearchableQuery query, String propName, String propValue )
            throws Exception {
        Method getter = PropertyUtils.getReadMethod( new PropertyDescriptor( propName, query.getClass() ) );
        Class returnType = getter.getReturnType();

        Object objPhrase;

        if ( returnType.equals( QueryPhrase.class ) ) {
            objPhrase = new StandardQueryPhraseConverter().objectToPhrase( propValue );
        } else if ( returnType.getName().equals( "boolean" ) ) {
            objPhrase = Boolean.valueOf( propValue ).booleanValue();
        } else {
            throw new IntactException( "Unexpected type " + returnType.getName() + " for property name: " + propName );
        }

        BeanUtils.setProperty( query, propName, objPhrase );
    }

    public static boolean isSearchableQuery( String searchableQueryStr ) {
        if ( searchableQueryStr == null ) {
            return false;
        }

        searchableQueryStr = searchableQueryStr.trim();

        return searchableQueryStr.startsWith( "{" ) && searchableQueryStr.endsWith( "}" );
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}
