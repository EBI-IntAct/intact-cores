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
 * Represents a parameter value.
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.0
 */

@MappedSuperclass
public abstract class Parameter extends BasicObjectImpl {

    public static final Integer DEFAULT_BASE = 10;

    public static final Integer DEFAULT_EXPONENT = 0;

    /**
     * Base of the parameter value.
     */
    protected Integer base = DEFAULT_BASE;

    /**
     * Exponent of the parameter value.
     */
    protected Integer exponent = DEFAULT_EXPONENT;

    /**
     * The "main" value of the parameter.
     */
    protected Double factor;

    /**
     * Uncertainty of the parameter value.
     */
    protected Double uncertainty;

    /**
     * The kind of parameter, e.g. "dissociation constant".
     */
    protected CvParameterType cvParameterType;

    /**
     * The unit of the term, e.g. "kiloDalton".
     */
    protected CvParameterUnit cvParameterUnit;

    protected Experiment experiment;

	public Parameter() {
		super();
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
        if( factor == null ) {
            throw new IllegalArgumentException( "You must set a non null factor." );
        }
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
        if( cvParameterType == null ) {
            throw new IllegalArgumentException( "You must give a non null CvParameterType." );
        }
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
		result = prime * result + ((exponent == null) ? 0 : exponent.hashCode());
		result = prime * result + ((factor == null) ? 0 : factor.hashCode());
		result = prime * result + ((uncertainty == null) ? 0 : uncertainty.hashCode());
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Parameter{" );
        sb.append( ( cvParameterType != null ? cvParameterType.getShortLabel(): "" )).append( ": " );
        sb.append( factor ).append( '.' );
        sb.append( base );
        sb.append("e").append( exponent );
        if( uncertainty != null ) {
        sb.append( " +/- " ).append( uncertainty );
        }
        sb.append( " " ).append( ( cvParameterUnit != null ? cvParameterUnit.getShortLabel(): "" ) );
        sb.append( '}' );
        return sb.toString();
    }
}