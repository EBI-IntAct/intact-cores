/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.model.util.CvObjectIdentifierGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * Represents a controlled vocabulary object. CvObject is derived from AnnotatedObject to allow to store annotation of
 * the term within the object itself, thus allowing to build an integrated dictionary.
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
@Entity
@Table(
        name = "ia_controlledvocab",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "objclass", "shortlabel" }) },
        indexes = { @Index(name = "cvobject_id_idx", columnList = "identifier") })
@DiscriminatorColumn(name = "objclass", discriminatorType = DiscriminatorType.STRING, length = 255)
public abstract class CvObject extends AnnotatedObjectImpl<CvObjectXref, CvObjectAlias> implements Searchable {

    private String objClass;

    /**
     * PSI-MI Identifier for this object, which is a de-normalization of the
     * value contained in the 'identity' xref
     */
    private String identifier;

    public CvObject() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that CvObjects cannot be created without at least a shortLabel and an
     * owner specified.
     *
     * @param shortLabel The memorable label to identify this CvObject
     * @throws NullPointerException thrown if either parameters are not specified
     */
    protected CvObject(String shortLabel) {
        super(shortLabel);
    }

    @Deprecated
    protected CvObject(Institution owner, String shortLabel) {
        super(shortLabel, owner);
    }

    /////////////////////////////
    // Entity callback methods

    @PrePersist
    public void preparePersist() {
        if (identifier == null) {
            CvObjectIdentifierGenerator idGenerator = new CvObjectIdentifierGenerator();
            idGenerator.populateIdentifier(this, true);
        }
    }

    @PostLoad
    @PreUpdate
    public void prepareUpdate() {
        if (identifier == null) {
            CvObjectIdentifierGenerator idGenerator = new CvObjectIdentifierGenerator();
            idGenerator.populateIdentifier(this, false);
        }
    }

    /////////////////////////////
    // Entity fields

    @Column(name = "objclass", insertable = false, updatable = false)
    public String getObjClass() {
        if (objClass == null) {
            objClass = CgLibUtil.removeCglibEnhanced(getClass()).getName();
        }
        return objClass;
    }

    public void setObjClass(String objClass) {
        this.objClass = objClass;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "ia_cvobject2annot",
            joinColumns = {@JoinColumn(name = "cvobject_ac")},
            inverseJoinColumns = {@JoinColumn(name = "annotation_ac")}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH,
            org.hibernate.annotations.CascadeType.DETACH})
    @Override
    public Collection<CvObjectXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH,
            org.hibernate.annotations.CascadeType.DETACH})
    @Override
    public Collection<CvObjectAlias> getAliases() {
        return super.getAliases();
    }

    @Column(name = "identifier", insertable = false, updatable = false)
    @Deprecated
    public String getMiIdentifier() {
        return identifier;
    }

    @Deprecated
    public void setMiIdentifier(String mi) {
        setIdentifier(mi);
    }

    /**
     * PSI-MI Identifier for this object, which is a de-normalization of the
     * value contained in the 'identity' xref from the 'psimi' database
     *
     * @return the MI Identifier for this CVObject
     * @since 1.9.x
     */
    @Column(name = "identifier", length = 30)
    @Size(max = 30)
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Equality for CvObject is currently based on equality for primary id of Xref having the qualifier of identity and
     * short label if there is xref of identity. We need to equals method to avoid circular references when invoking
     * equals methods
     *
     * @param obj The object to check
     * @return true if given object has an identity xref and its primary id matches to this' object's primary id or
     * short label if there is no identity xref.
     * @see Xref
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CvObject)) {
            return false;
        }

        final CvObject other = (CvObject) obj;
        if (!getObjClass().equals(other.getObjClass())) {
            return false;
        }


        if ((identifier != null && !identifier.equals(other.getIdentifier()))) {
            return false;
        }

        return super.equals(other);
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable, hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();

        //need check as we still have no-arg constructor...
        if (identifier != null) {
            result = 29 * result + identifier.hashCode();
        } else {
            result = 29 * result + ((getShortLabel() == null) ? 31 : getShortLabel().hashCode());
        }

        return result;
    }

    @Override
    public String toString() {
        return "Id=" + identifier + ", " + super.toString();
    }
} // end CvObject




