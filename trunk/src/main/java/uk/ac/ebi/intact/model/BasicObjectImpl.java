package uk.ac.ebi.intact.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * New class which factors out owner and evidences from IntactObject.
 * This allows objects which do not have an owner (eg: Institution)
 * to inherit from IntactObject.
 *
 * @author intact team
 * @version $Id$
 */
@MappedSuperclass
public abstract class BasicObjectImpl extends IntactObjectImpl implements BasicObject {

    // TODO: synchron? this should moved somewhere else.
    private String ownerAc;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments + plural name
     */
    private Collection<Evidence> evidences = new ArrayList<Evidence>();

    /**
     * TODO comments
     */
    private Institution owner;


    /**
     * Protected constructor for use by subclasses
     */
    public BasicObjectImpl() {
        super();
    }

    protected BasicObjectImpl( Institution owner ) {

        this();
        setOwner( owner );
    }

    ///////////////////////////////////////
    // access methods for associations

    public void setEvidences( Collection<Evidence> someEvidence ) {
        this.evidences = someEvidence;
    }

    @Transient
    public Collection<Evidence> getEvidences() {
        return evidences;
    }

    public void addEvidence( Evidence evidence ) {
        if ( !this.evidences.contains( evidence ) ) this.evidences.add( evidence );
    }

    public void removeEvidence( Evidence evidence ) {
        this.evidences.remove( evidence );
    }

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "owner_ac", nullable = false )
    public Institution getOwner() {
        return owner;
    }

    public void setOwner( Institution institution ) {
        /*
        if( institution == null ) {
            throw new NullPointerException( "valid " + getClass().getName() + " must have an owner (Institution) !" );
        }
        */
        this.owner = institution;
        // TODO: synchron ?
        //this.ownerAc = institution.getAc();
    }

    ///////////////////////////////////////
    // access methods for associations

    @Column( name = "owner_ac", insertable = false, updatable = false, nullable = false )
    public String getOwnerAc() {
        return ownerAc;
    }

    public void setOwnerAc( String ac ) {
        this.ownerAc = ac;
    }


    ///////////////////////////////////////
    // instance methods
    @Override
    public String toString() {
        return super.toString() + "; owner=" + owner.getAc();
    }
}