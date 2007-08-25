/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;

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
@DiscriminatorColumn( name = "objclass", length = 100 )
public abstract class InteractorImpl extends AnnotatedObjectImpl<InteractorXref, InteractorAlias> implements Interactor, Searchable {

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
     * TODO comments
     */
    private Collection<Product> products; // not implemented yet.


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
    protected InteractorImpl( String shortLabel, Institution owner, CvInteractorType type ) {
        super( shortLabel, owner );
        setCvInteractorType( type );
    }

    ///////////////////////////////////////
    //access methods for attributes
    @Column( name = "objclass", insertable = false, updatable = false )
    public String getObjClass() {
        return objClass;
    }

    public void setObjClass( String objClass ) {
        this.objClass = objClass;
    }

    @ManyToOne( fetch = FetchType.LAZY )
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

    @OneToMany( mappedBy = "interactor" )
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

    public void setProducts( Collection<Product> someProduct ) {
        this.products = someProduct;
        throw new UnsupportedOperationException();
    }

    @Transient
    public Collection<Product> getProducts() {
        // return products;
        throw new UnsupportedOperationException();
    }

    public void addProduct( Product product ) {
        if ( !this.products.contains( product ) ) {
            this.products.add( product );
            product.setInteractor( this );
        }

        throw new UnsupportedOperationException();
    }

    public void removeProduct( Product product ) {
        boolean removed = this.products.remove( product );
        if ( removed ) {
            product.setInteractor( null );
        }

        throw new UnsupportedOperationException();
    }

    public void setCvInteractorType( CvInteractorType type ) {
        interactorType = type;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "interactortype_ac" )
    public CvInteractorType getCvInteractorType() {
        return interactorType;
    }

    @ManyToMany( cascade = {CascadeType.PERSIST} )
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
        if ( !super.equals( o ) ) {
            return false;
        }
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final InteractorImpl that = ( InteractorImpl ) o;

        if ( activeInstances != null ? !activeInstances.equals( that.activeInstances ) : that.activeInstances != null ) {
            return false;
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




