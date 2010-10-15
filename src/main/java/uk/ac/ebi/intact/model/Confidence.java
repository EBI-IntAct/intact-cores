/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model;

import javax.validation.constraints.Size;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents a specific confidence value of an interaction.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
@Entity
@Table( name = "ia_confidence" )
public class Confidence extends BasicObjectImpl {
    private String value;
    private CvConfidenceType cvConfidenceType;
    private InteractionImpl interaction;

    public Confidence() {
        super();
    }

    public Confidence( String value ) {
        super();
        this.value = value;
    }

    public Confidence(CvConfidenceType cvType, String value ) {
        this(value);
        this.cvConfidenceType = cvType;
    }
    
    @Deprecated
    public Confidence( Institution owner, String value ) {
        this(value);
        setOwner(owner);
    }

    @Deprecated
    public Confidence(Institution owner, CvConfidenceType cvType, String value ) {
        this(cvType, value);
        setOwner(owner);
    }

    @Size(max = 4000)
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @ManyToOne ( targetEntity = InteractionImpl.class )
    @JoinColumn (name = "interaction_ac")
     public InteractionImpl getInteraction() {
        return interaction;
    }

    public void setInteraction( InteractionImpl interaction ) {
        this.interaction = interaction;
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
        if ( !( o instanceof Confidence ) ) return false;

        Confidence that = ( Confidence ) o;

        if ( cvConfidenceType != null ? !cvConfidenceType.equals( that.cvConfidenceType ) : that.cvConfidenceType != null )
            return false;
        if ( interaction != null ? !interaction.equals( that.interaction, false ) : that.interaction != null ) return false;
        if ( value != null ? !value.equals( that.value ) : that.value != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( value != null ? value.hashCode() : 0 );
        result = 31 * result + ( cvConfidenceType != null ? cvConfidenceType.hashCode() : 0 );
        result = 31 * result + ( interaction != null ? interaction.hashCode() : 0 );
        return result;
    }
}
