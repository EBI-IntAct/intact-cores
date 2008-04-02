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

import javax.persistence.*;

/**
 * Represents a specific parameter value of an interaction.
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since 1.9.0
 */
@Entity
@Table( name = "ia_interaction_parameter" )
public class InteractionParameter extends Parameter {
	
	protected InteractionImpl interaction;
    
	public InteractionParameter() {
		super();
	}
	
	public InteractionParameter( Institution owner, CvParameterType cvParameterType, Double factor ) {
        super(owner, cvParameterType, factor);
    }
	
	public InteractionParameter( Institution owner, CvParameterType cvParameterType, CvParameterUnit cvParameterUnit, Double factor ) {
        super(owner, cvParameterType, cvParameterUnit, factor);
    }
	
    @ManyToOne ( targetEntity = InteractionImpl.class )
    @JoinColumn (name = "interaction_ac")
     public InteractionImpl getInteraction() {
        return this.interaction;
    }
    
    public void setInteraction( InteractionImpl interaction ) {
        this.interaction = interaction;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((interaction == null) ? 0 : interaction.hashCode());
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
		final InteractionParameter other = (InteractionParameter) obj;
		if (interaction == null) {
			if (other.interaction != null)
				return false;
		} else if (!interaction.equals(other.interaction, false))
			return false;
		return true;
	}    
}
