/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
@Entity
@Table( name = "ia_controlledvocab_xref",
        uniqueConstraints = {@UniqueConstraint(columnNames={"parent_ac", "qualifier_ac", "database_ac", "primaryid"})}) 
public class CvObjectXref extends Xref {

    private static final Log log = LogFactory.getLog( CvObjectXref.class );


    public CvObjectXref() {
    }

    @PostLoad
    public void postLoad() {
        CvObject parent = (CvObject) getParent();

        if (parent != null && parent.getMiIdentifier() != null) {
            prepareParentMi();
        }
    }

    @PrePersist
    @PreUpdate
    public void prepareParentMi() {
        CvObject parent = (CvObject) getParent();

        if (parent == null) {
            throw new IllegalStateException("Trying to persist or update an cv xref without parent: "+this);
        }

        if (CvDatabase.PSI_MI_MI_REF.equals(getCvDatabase().getMiIdentifier()) &&
            CvXrefQualifier.IDENTITY_MI_REF.equals(getCvXrefQualifier().getMiIdentifier())) {
            parent.setMiIdentifier(getPrimaryId());
        } 
    }



    public CvObjectXref( Institution anOwner, CvDatabase aDatabase, String aPrimaryId, String aSecondaryId, String aDatabaseRelease, CvXrefQualifier aCvXrefQualifier ) {
        super( anOwner, aDatabase, aPrimaryId, aSecondaryId, aDatabaseRelease, aCvXrefQualifier );
    }

    public CvObjectXref( Institution anOwner, CvDatabase aDatabase, String aPrimaryId, CvXrefQualifier aCvXrefQualifier ) {
        super( anOwner, aDatabase, aPrimaryId, aCvXrefQualifier );
    }

    @ManyToOne( targetEntity = CvObject.class )
    @JoinColumn( name = "parent_ac" )
    public AnnotatedObject getParent() {
        return super.getParent();
    }

    @Column( name = "parent_ac", insertable = false, updatable = false )
    public String getParentAc() {
        return super.getParentAc();
    }

}
