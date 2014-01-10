/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.model;

import org.hibernate.annotations.GenericGenerator;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract Implementation for IntAct primary objects
 *
 */
@MappedSuperclass
public abstract class AbstractIntactPrimaryObject extends AbstractAuditable implements IntactPrimaryObject {
    private String ac;

    public AbstractIntactPrimaryObject() {
    }

    ///////////////////////////////////////
    //access methods for attributes

    @Id
    @GeneratedValue( generator = "intact-id-generator" )
    @GenericGenerator( name = "intact-id-generator", strategy = "uk.ac.ebi.intact.jami.model.IntactAcGenerator")
    @Column( length = 30 )
    public String getAc() {
        return ac;
    }

    ///////////////////////////////////////
    // instance methods
    @Override
    public String toString() {
        return this.ac;
    }

    /**
     * This method should not be used by applications, as the AC is a primary key which is auto-generated. If we move to
     * an application server it may then be needed.
     *
     * This method is only used by hibernate using reflexion
     *
     * @param ac
     */
    public void setAc( String ac ) {
        this.ac = ac;
    }
}

