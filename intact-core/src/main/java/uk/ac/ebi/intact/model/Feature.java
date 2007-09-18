/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.*;
import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p/>
 * Represents a feature, a region with specific properties, on a sequence.
 * </p>
 *
 * @author Chris Lewington, hhe
 *         <p/>
 *         example - an InterPro domain
 *         example - an experimentally determined binding domain
 */
@Entity
@Table( name = "ia_feature" )
@EditorTopic
public class Feature extends AnnotatedObjectImpl<FeatureXref, FeatureAlias> implements Editable {

    //------------------- attributes -------------------------------

    /**
     * The Substrate a domain belongs to.
     */
    private Component component;

    /**
     * <p/>
     * A feature may bind to another feature, usually on a different
     * Interactor. This binding is reciprocal, the &quot;binds&quot; attribute should be
     * used on both Interactors.
     * </p>
     * <p/>
     * Deprecated special case: If a complex is assembled fromsubcomplexe, it
     * is not directly possible to represent the binding domains between the
     * subcomplexes. However, this is possible by defining domains on the
     * initial substrates, which are then used as binding domains between
     * Interactores which only interact in the second complex. As this method
     * creates ambiguities and difficult data structures, it is deprecated.
     * </p>
     */
    private Feature binds;

    /**
     * The List of ranges applicable to a Feature. The elements are
     * of type:
     *
     * @see Range
     */
    private Collection<Range> ranges = new ArrayList<Range>();

    /**
     * TODO comments
     */
    private CvFeatureIdentification cvFeatureIdentification;

    /**
     * TODO comments
     */
    private CvFeatureType cvFeatureType;


    //---------------------------- constructors -----------------------------------
    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public Feature() {
        super();
    }

    /**
     * This constructor currently assumes that a valid Feature instance must have
     * at least an owner, shortLabel and Component.
     *
     * @param owner      The owner of the Feature - must be non-null
     * @param shortLabel A shortLabel to reference the Feature - must be non-null
     * @param component  The Component to which this Feature is attached - must
     *                   be non-null.
     * @param type       the CvfeatureType of the Feature. Manadatory.
     */
    public Feature( Institution owner, String shortLabel,
                    Component component, CvFeatureType type
    ) {

        //super call sets up a valid AnnotatedObject
        super( shortLabel, owner );
        if ( type == null ) {
            throw new NullPointerException( "Must have a CvFeatureType to create a Feature!" );
        }
        if ( component == null ) {
            throw new NullPointerException( "Cannot create Feature without a Component!" );
        }
        this.component = component;
        this.cvFeatureType = type;
    }

    //----------------------- public methods ------------------------------

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    @JoinColumn( name = "featuretype_ac" )
    public CvFeatureType getCvFeatureType() {
        return cvFeatureType;
    }

    public void setCvFeatureType( CvFeatureType cvFeatureType ) {
        this.cvFeatureType = cvFeatureType;
    }

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    @JoinColumn( name = "component_ac" )
    public Component getComponent() {
        return component;
    }

    /**
     * This method adds a 'callback' to the Component to which a Feature applies.
     * It has the SIDE EFFECT of also adding the Feature object to the Component's
     * list of binding domians.
     *
     * @param component The component relevant to this Feature
     */
    public void setComponent( Component component ) {
        /*
        if (this.component != component) {
            if (this.component != null)
            {
                this.component.removeBindingDomain(this);
            }
            this.component = component;
            if (component != null)
            {
                component.addBindingDomain(this);
            }
        }
        */
        this.component = component;
    }

    /**
     * Provides access to the other Feature to which the current Feature object
     * binds to.
     *
     * @return The Feature that the current Feature binds, or null if no such
     *         Feature exists.
     */
    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @JoinColumn( name = "linkedfeature_ac", referencedColumnName = "ac" )
    public Feature getBoundDomain() {
        return binds;
    }

    public void setBoundDomain( Feature feature ) {
        binds = feature;
    }

    /**
     * Provides the List of Range objects related to  a Feature instance.
     *
     * @return A List of Ranges (expected to be non-empty)
     */
    @OneToMany( mappedBy = "feature", cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<Range> getRanges() {
        return ranges;
    }

    public void setRanges( Collection<Range> ranges ) {
        this.ranges = ranges;
    }

    /**
     * Adds a new Range object to a Feature instance. NOte that if the Range
     * is already present then it will not be added again.
     *
     * @param range A new Range instance to add.
     */
    public void addRange( Range range ) {
        if ( !this.ranges.contains( range ) ) {
            this.ranges.add( range );
            range.setFeature( this );
        }
    }

    public void removeRange( Range range ) {
        this.ranges.remove( range );
    }

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @JoinColumn( name = "identification_ac" )
    public CvFeatureIdentification getCvFeatureIdentification() {
        return cvFeatureIdentification;
    }

    public void setCvFeatureIdentification( CvFeatureIdentification cvFeatureIdentification ) {
        this.cvFeatureIdentification = cvFeatureIdentification;
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<FeatureXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<FeatureAlias> getAliases() {
        return super.getAliases();
    }

    @ManyToMany( cascade = CascadeType.PERSIST)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    @JoinTable(
            name = "ia_feature2annot",
            joinColumns = {@JoinColumn( name = "feature_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    /**
     * Equality for Features is currently based on owner, shortLabel, Component
     * and any non-null Ranges. NOTE: we cannot check equality for a related
     * Feature binding because this would be recursive!!
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Feature ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final Feature feature = ( Feature ) o;

        //NB Component should never be null, but check just in case!
        if ( component != null ) {
            if ( !component.equals( feature.getComponent() ) ) {
                return false;
            }
        } else {
            if ( feature.getComponent() != null ) {
                return false;
            }
        }

        //Now check the Ranges...
        return CollectionUtils.isEqualCollection( ranges, feature.getRanges() );
    }

    /**
     * Remember that hashCode and equals methods has to be develop in parallel
     * since : if a.equals(b) then a.hoshCode() == b.hashCode(). Currently the Feature
     * hashcode is based on its Component.
     * The other way round is NOT true.
     * Unless it could break consistancy when storing object in a hash-based
     * collection such as HashMap...
     */
    @Override
    public int hashCode() {

        int code = super.hashCode();
        if ( component != null ) {
            code = code * 29 + component.hashCode();
        }
        //Q: should we use any Ranges also in computing the hashcode?

        return code;
    }

    /**
     * Returns a cloned version of the current object.
     *
     * @return a cloned version of the current Feature with cloned ranges. The
     *         exceptions are:
     *         <ul>
     *         <li>The bound domain is not cloned (or else it will lead to recursive
     *         behaviour). It is shared with the existing Feature, to be replaced
     *         later with cloned copies</li>.
     *         <li>Component is set to null.</li>
     *         </ul>
     *
     * @throws CloneNotSupportedException for errors in cloning this object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Feature copy = ( Feature ) super.clone();

        // Unset the existing component and bind feature.
        copy.component = null;
        copy.binds = null;

        // binds is still pointing to the original feature.

        copy.ranges = new ArrayList<Range>( ranges.size() );
        // Make deep copies of range.
        for ( Range range : ranges ) {
            Range copyRange = ( Range ) range.clone();
            copyRange.setFeature( copy );
            copy.ranges.add( copyRange );
        }

        // Need to do more here.
        return copy;
    }

    /**
     * This is a package visible method specifically for the clone method
     * of Component class. The present setComponent method changes
     * the argument passed, thus causing changes to the source of the
     * clone.
     *
     * @param component the Component to set. This simply replaces
     *                  the existing component.
     */
    void setComponentForClone( Component component ) {
        this.component = component;
    }
}




