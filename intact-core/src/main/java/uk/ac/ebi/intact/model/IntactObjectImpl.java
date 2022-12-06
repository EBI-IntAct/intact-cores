/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * This is the top level class for all intact model object.
 *
 * @author intact team
 * @version $Id$
 */
@MappedSuperclass
public abstract class IntactObjectImpl extends AbstractAuditable implements IntactObject, Cloneable {

    protected static final String NEW_LINE = System.getProperty("line.separator");

    protected String ac;

    private boolean deprecated;

    public IntactObjectImpl() {
    }

    ///////////////////////////////////////
    //access methods for attributes

    @Id
    @GeneratedValue(generator = "intact-id-generator", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "intact-id-generator", strategy = "uk.ac.ebi.intact.model.IntactIdGenerator")
    @Column(length = 30)
    public String getAc() {
        return ac;
    }

    /**
     * This method should not be used by applications, as the AC is a primary key which is auto-generated. If we move to
     * an application server it may then be needed.
     *
     * @param ac
     */
    public void setAc(String ac) {
        this.ac = ac;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }


    /**
     * Makes a clone of this intact object.
     *
     * @return a cloned version of the current instance.
     * @throws CloneNotSupportedException to indicate that an instance cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        IntactObjectImpl copy = (IntactObjectImpl) super.clone();
        // Reset the AC.
        copy.setAc(null);

        // Sets the dates to the current date.
        copy.setCreated(null);
        copy.setUpdated(null);

        return copy;
    }

    ///////////////////////////////////////
    // instance methods
    @Override
    public String toString() {
        return this.ac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntactObjectImpl that = (IntactObjectImpl) o;

        if (ac != null && that.getAc() != null) {
            return ac.equals(that.getAc());
        }

        return true;
    }

    @Override
    public int hashCode() {
        return ac != null ? ac.hashCode() : 0;
    }
} // end IntactObject

