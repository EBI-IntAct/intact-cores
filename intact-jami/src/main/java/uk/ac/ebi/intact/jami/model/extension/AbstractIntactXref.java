package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import psidev.psi.mi.jami.utils.comparator.xref.UnambiguousXrefComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Abstract IntAct implementation of Xref
 * Note: this implementation was chosen because xrefs do not make sense without their parents and are not shared by different entities
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/12/13</pre>
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class AbstractIntactXref extends AbstractIntactPrimaryObject implements Xref{

    ///////////////////////////////////////
    // Constant

    /**
     * Primary identifier of the database referred to.
     */
    private String id;

    /**
     * Secondary identifier of the database. This will usually be
     * a meaningful name, for example a domain name.
     */
    private String secondaryId;

    /**
     * The release number of the external database from which the object
     * has been updated.
     */
    private String version;

    ///////////////////////////////////////
    // associations

    /**
     * Qualifier
     */
    private CvTerm qualifier;

    /**
     * Database
     */
    private CvTerm database;

    protected AbstractIntactXref() {
        //super call sets creation time data
        super();
    }


    public AbstractIntactXref(CvTerm database, String id, CvTerm qualifier){
        this(database, id);
        this.qualifier = qualifier;
    }

    public AbstractIntactXref(CvTerm database, String id, String version, CvTerm qualifier){
        this(database, id, version);
        this.qualifier = qualifier;
    }

    public AbstractIntactXref(CvTerm database, String id, String version){
        this(database, id);
        this.version = version;
    }

    public AbstractIntactXref(CvTerm database, String id){
        if (database == null){
            throw new IllegalArgumentException("The database is required and cannot be null");
        }
        this.database = database;

        if (id == null || (id != null && id.length() == 0)){
            throw new IllegalArgumentException("The id is required and cannot be null or empty");
        }
        this.id = id;
    }

    @PrePersist
    @PreUpdate
    public void prePersist(){
        if (!(this.database instanceof IntactCvTerm)){
            IntactCvTerm clone = new IntactCvTerm(this.database.getShortName());
            clone.setObjClass(IntactUtils.DATABASE_OBJCLASS);
            CvTermCloner.copyAndOverrideCvTermProperties(this.database, clone);
            this.database = clone;
        }
        if (this.qualifier != null && !(this.qualifier instanceof IntactCvTerm)){
            IntactCvTerm clone = new IntactCvTerm(this.qualifier.getShortName());
            clone.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
            CvTermCloner.copyAndOverrideCvTermProperties(this.qualifier, clone);
            this.qualifier = clone;
        }
    }

    ///////////////////////////////////////
    //access methods for attributes

    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String aPrimaryId) {
        if (id == null || (id != null && id.length() == 0)){
            throw new IllegalArgumentException("The id is required and cannot be null or empty");
        }
        this.id = aPrimaryId;
    }

    @Size(max = IntactUtils.MAX_ID_LEN)
    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String aSecondaryId) {
        this.secondaryId = aSecondaryId;
    }

    @Column(name = "dbrelease")
    @Size(max = IntactUtils.MAX_DB_RELEASE_LEN)
    public String getVersion() {
        return version;
    }

    public void setVersion(String aDbRelease) {
        this.version = aDbRelease;
    }

    ///////////////////////////////////////
    // access methods for associations
    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn(name = "qualifier_ac")
    @Target(IntactCvTerm.class)
    public CvTerm getQualifier() {
        return this.qualifier;
    }

    public void setQualifier(CvTerm cvXrefQualifier) {
        this.qualifier = cvXrefQualifier;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn(name = "database_ac")
    @Target(IntactCvTerm.class)
    @NotNull
    public CvTerm getDatabase() {
        return this.database;
    }

    public void setDatabase(CvTerm cvDatabase) {
        this.database = cvDatabase;
    }

    ///////////////////////////////////////
    // instance methods
    @Override
    public boolean equals(Object o) {

        if (this == o){
            return true;
        }

        // Xrefs are different and it has to be ExternalIdentifier
        if (!(o instanceof Xref)){
            return false;
        }

        return UnambiguousXrefComparator.areEquals(this, (Xref) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousXrefComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return database.toString() + ":" + id.toString() + (qualifier != null ? " (" + qualifier.toString() + ")" : "");
    }
}
