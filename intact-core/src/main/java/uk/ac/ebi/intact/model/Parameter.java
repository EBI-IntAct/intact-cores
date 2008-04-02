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

@MappedSuperclass
public abstract class Parameter extends BasicObjectImpl {

    protected Integer base;
    protected Integer exponent;
    protected Double factor;
    protected Double uncertainty;
	protected CvParameterType cvParameterType;
    protected CvParameterUnit cvParameterUnit;
    protected Experiment experiment;

	public Parameter() {
		super();
		this.base = 10;
		this.exponent = 0;
	}

	public Parameter( Institution owner, CvParameterType cvParameterType, Double factor ) {
        super(owner);
        setFactor(factor);
        setCvParameterType(cvParameterType);
    }

	public Parameter( Institution owner, CvParameterType cvParameterType, CvParameterUnit cvParameterUnit, Double factor ) {
        super(owner);
        setFactor(factor);
        setCvParameterType(cvParameterType);
        setCvParameterUnit(cvParameterUnit);
    }

	public void setBase( Integer base ) {
        this.base = base;
    }

	public void setExponent( Integer exponent ) {
        this.exponent = exponent;
    }

	public void setFactor( Double factor ) {
        this.factor = factor;
    }

	public void setUncertainty( Double uncertainty ) {
        this.uncertainty = uncertainty;
    }

    public Integer getBase() {
        return this.base;
    }

	public Integer getExponent() {
        return this.exponent;
    }

	public Double getFactor() {
        return this.factor;
    }

	public Double getUncertainty() {
        return this.uncertainty;
    }

    @ManyToOne
    @JoinColumn( name = "experiment_ac" )
    public Experiment getExperiment() {
        return this.experiment;
    }

    public void setExperiment( Experiment experiment ) {
        this.experiment = experiment;
    }

    @ManyToOne
    @JoinColumn( name = "parametertype_ac" )
    public CvParameterType getCvParameterType() {
        return cvParameterType;
    }

	public void setCvParameterType( CvParameterType cvParameterType ) {
        this.cvParameterType = cvParameterType;
    }

    @ManyToOne
    @JoinColumn( name = "parameterunit_ac" )
    public CvParameterUnit getCvParameterUnit() {
        return cvParameterUnit;
    }

	public void setCvParameterUnit( CvParameterUnit cvParameterUnit ) {
        this.cvParameterUnit = cvParameterUnit;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result
				+ ((exponent == null) ? 0 : exponent.hashCode());
		result = prime * result + ((factor == null) ? 0 : factor.hashCode());
		result = prime * result
				+ ((uncertainty == null) ? 0 : uncertainty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Parameter other = (Parameter) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (exponent == null) {
			if (other.exponent != null)
				return false;
		} else if (!exponent.equals(other.exponent))
			return false;
		if (factor == null) {
			if (other.factor != null)
				return false;
		} else if (!factor.equals(other.factor))
			return false;
		if (uncertainty == null) {
			if (other.uncertainty != null)
				return false;
		} else if (!uncertainty.equals(other.uncertainty))
			return false;
		return true;
	}


}
