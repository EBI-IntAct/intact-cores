/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DiscriminatorFormula;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Defines a generic interacting object.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@Table( name = "ia_interactor" )
//@DiscriminatorColumn( name = "objclass", length = 100 )
@DiscriminatorFormula ("objclass")
@DiscriminatorValue( "uk.ac.ebi.intact.model.InteractorImpl" )
public class InteractorImpl extends AnnotatedObjectImpl<InteractorXref, InteractorAlias> implements Interactor, Searchable {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String bioSourceAc;

    private String objClass;

    /**
     * The biological source of the Interactor.
     */
    private BioSource bioSource;

    /**
     * For OJB access
     */
    private String cvInteractorTypeAc;

    /**
     * The interactor type.
     */
    private CvInteractorType interactorType;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Collection<Component> activeInstances = new ArrayList<Component>();


    /**
     * no-arg constructor provided for compatibility with subclasses that have no-arg constructors.
     */
    public InteractorImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that Interactors cannot be created without at least a shortLabel and
     * an owner specified. NOTE: It is assumed that subclasses of Interactor will supply a valid BioSource; this is
     * initially set to null but <b>other classes may expect it to be non-null</b>.
     *
     * @param shortLabel The memorable label to identify this Interactor
     * @param owner      The Institution which owns this Interactor
     *
     * @throws NullPointerException thrown if either parameters are not specified
     * @deprecated Use {@link #InteractorImpl(String,Institution,CvInteractorType)} instead
     */
    @Deprecated
    protected InteractorImpl( String shortLabel, Institution owner ) {
        this( shortLabel, owner, null );
    }


    /**
     * Constructor for subclass use only. Ensures that Interactors cannot be created without at least a shortLabel, an
     * owner and type specified. NOTE: It is assumed that subclasses of Interactor will supply a valid BioSource; this
     * is initially set to null but <b>other classes may expect it to be non-null</b>.
     *
     * @param shortLabel The memorable label to identify this Interactor
     * @param owner      The Institution which owns this Interactor
     * @param type       The Interactor type
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public InteractorImpl( String shortLabel, Institution owner, CvInteractorType type ) {
        super( shortLabel, owner );
        setCvInteractorType( type );
    }

    ////////////////////////////////////////
    // entity callback methods

    @PrePersist
    @PreUpdate
    protected void correctObjClass() {
        if (interactorType == null) {
            throw new IllegalStateException("Interactor without interactor type: " + this);
        }

        if (CvObjectUtils.isProteinType(interactorType)) {
            setObjClass(ProteinImpl.class.getName());
        } else if (CvObjectUtils.isInteractionType(interactorType)) {
            setObjClass(InteractionImpl.class.getName());
        } else if (CvObjectUtils.isSmallMoleculeType(interactorType)) {
            setObjClass(SmallMoleculeImpl.class.getName());
        } else if (CvObjectUtils.isNucleicAcidType(interactorType)) {
            setObjClass(NucleicAcidImpl.class.getName());
        } else if (this instanceof Polymer) {
            setObjClass(PolymerImpl.class.getName());
        } else {
            setObjClass(InteractorImpl.class.getName());
        }
    }

    ///////////////////////////////////////
    //access methods for attributes
    @Column( name = "objclass" )
    public String getObjClass() {
        if( objClass == null ) {
            objClass = getClass().getName();
        }
        return objClass;
    }

    public void setObjClass( String objClass ) {
        this.objClass = objClass;
    }

    @ManyToOne

    @JoinColumn( name = "biosource_ac" )
    public BioSource getBioSource() {
        return bioSource;
    }

    public void setBioSource( BioSource bioSource ) {
//        if( bioSource == null ) {
//            throw new NullPointerException( "valid Interactor must have a BioSource!" );
//        }
        this.bioSource = bioSource;
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setActiveInstances( Collection<Component> someActiveInstance ) {
        if ( someActiveInstance == null ) {
            throw new IllegalArgumentException( "Active instances cannot be null." );
        }
        this.activeInstances = someActiveInstance;
    }


    @OneToMany( mappedBy = "interactor")
    public Collection<Component> getActiveInstances() {
        return activeInstances;
    }

    public void addActiveInstance( Component component ) {
        if ( !this.activeInstances.contains( component ) ) {
            this.activeInstances.add( component );
            component.setInteractor( this );
        }
    }

    public void removeActiveInstance( Component component ) {
        boolean removed = this.activeInstances.remove( component );
        if ( removed ) {
            component.setInteractor( null );
        }
    }

    public void setCvInteractorType( CvInteractorType type ) {
        interactorType = type;
    }

    @ManyToOne
    @JoinColumn( name = "interactortype_ac" )
    public CvInteractorType getCvInteractorType() {
        return interactorType;
    }

    @ManyToMany( cascade = CascadeType.PERSIST)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "ia_int2annot",
            joinColumns = {@JoinColumn( name = "interactor_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }


    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<InteractorXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<InteractorAlias> getAliases() {
        return super.getAliases();
    }

    ///////////////////////////////////////
    // instance methods
    @Override
    public String toString() {
        String result;
        Iterator i;

        result = "AC: " + this.getAc() + " Owner: " + this.getOwner().getShortLabel()
                 + " Label: " + this.getShortLabel() + "[";
        if ( null != this.getXrefs() ) {
            i = this.getXrefs().iterator();
            while ( i.hasNext() ) {
                result = result + i.next();
            }
        }

        return result + "]";
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }

        if( !( o instanceof Interactor ) ) {
            return false;
        }

        final Interactor other = ( Interactor ) o;
        if (!getObjClass().equals(other.getObjClass())) {
            return false;
        }

//        if ( !super.equals( o ) ) {
//            return false;
//        }

        return equals(o, true);
    }

    protected boolean equals( Object o, boolean checkOnActiveInstances) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        if ( !super.equals( o ) ) {
            return false;
        }

        final InteractorImpl that = ( InteractorImpl ) o;

        if (checkOnActiveInstances) {
            if ( activeInstances != null ? !activeInstances.equals( that.activeInstances ) : that.activeInstances != null ) {
                return false;
            }
        }
        if ( bioSource != null ? !bioSource.equals( that.bioSource ) : that.bioSource != null ) {
            return false;
        }
        if ( interactorType != null ? !interactorType.equals( that.interactorType ) : that.interactorType != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + ( bioSource != null ? bioSource.hashCode() : 0 );
        result = 29 * result + ( interactorType != null ? interactorType.hashCode() : 0 );

        // next line is commented because is making hibernate (cglib) fail from time to time
        //result = 29 * result + ( activeInstances != null ? activeInstances.size() : 0 );
        return result;
    }

} // end Interactor




