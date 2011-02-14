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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents a specific confidence value of an interaction.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.4.0
 */
@Entity
@Table( name = "ia_component_confidence" )
public class ComponentConfidence extends AbstractConfidence {
    private Component component;

    public ComponentConfidence() {
        super();
    }

    public ComponentConfidence(String value) {
        super(value);
    }

    public ComponentConfidence(CvConfidenceType cvType, String value) {
        super(cvType, value);
    }


    @ManyToOne ( targetEntity = Component.class )
    @JoinColumn (name = "component_ac")
     public Component getComponent() {
        return component;
    }

    public void setComponent( Component component ) {
        this.component = component;
    }


    @Override
    public boolean equals( Object o ) {
         if ( this == o ) return true;
        if ( !( o instanceof AbstractConfidence ) ) return false;

        if (!super.equals(o)) {
            return false;
        }

        ComponentConfidence that = (ComponentConfidence) o;

        if ( component != null ? !component.equals( that.component, false ) : that.component != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ( component != null ? component.hashCode() : 0 );
        return result;
    }
}
