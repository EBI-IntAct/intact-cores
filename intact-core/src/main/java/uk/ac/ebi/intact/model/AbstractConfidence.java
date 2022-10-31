/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.4.0
 */
@MappedSuperclass
public abstract class AbstractConfidence extends BasicObjectImpl {

    private String value;
    private CvConfidenceType cvConfidenceType;

    public AbstractConfidence() {
        super();
    }

    public AbstractConfidence( String value ) {
        super();
        this.value = value;
    }

    public AbstractConfidence(CvConfidenceType cvType, String value ) {
        this(value);
        this.cvConfidenceType = cvType;
    }

    @Size(max = 4000)
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @ManyToOne
    @JoinColumn( name = "confidencetype_ac" )
    public CvConfidenceType getCvConfidenceType() {
        return cvConfidenceType;
    }

    public void setCvConfidenceType( CvConfidenceType cvConfidenceType ) {
        this.cvConfidenceType = cvConfidenceType;
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof AbstractConfidence ) ) return false;

        if (!super.equals(o)) {
            return false;
        }

        AbstractConfidence that = ( AbstractConfidence ) o;

        if ( cvConfidenceType != null ? !cvConfidenceType.equals( that.cvConfidenceType ) : that.cvConfidenceType != null )
            return false;
        if ( value != null ? !value.equals( that.value ) : that.value != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        result = 31 * result + ( cvConfidenceType != null ? cvConfidenceType.hashCode() : 0 );
        return result;
    }

}
