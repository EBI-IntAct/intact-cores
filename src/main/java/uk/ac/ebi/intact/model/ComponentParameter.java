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
 * Represents a specific parameter value of an interaction.
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since 1.8.0
 */
@Entity
@Table( name = "ia_component_parameter" )
public class ComponentParameter extends Parameter {
	
	protected Component component;
    
	public ComponentParameter() {
		super();
	}

    public ComponentParameter( CvParameterType cvParameterType, Double factor ) {
        super(cvParameterType, factor);
    }

	public ComponentParameter( CvParameterType cvParameterType, CvParameterUnit cvParameterUnit, Double factor ) {
        super(cvParameterType, cvParameterUnit, factor);
    }

    @Deprecated
	public ComponentParameter( Institution owner, CvParameterType cvParameterType, Double factor ) {
        super(owner, cvParameterType, factor);
    }

    @Deprecated
	public ComponentParameter( Institution owner, CvParameterType cvParameterType, CvParameterUnit cvParameterUnit, Double factor ) {
        super(owner, cvParameterType, cvParameterUnit, factor);
    }
	
    @ManyToOne ( targetEntity = Component.class )
    @JoinColumn (name = "component_ac")
     public Component getComponent() {
        return this.component;
    }
    
    public void setComponent( Component component ) {
        this.component = component;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((component == null) ? 0 : component.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ComponentParameter other = (ComponentParameter) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		return true;
	}

	
}
