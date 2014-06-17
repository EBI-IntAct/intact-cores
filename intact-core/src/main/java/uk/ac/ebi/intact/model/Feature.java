/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import uk.ac.ebi.intact.core.util.HashCodeUtils;
import uk.ac.ebi.intact.model.util.ComplexUtils;

import javax.persistence.*;
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
    /**
     * The category property has been created for compatibility with intact-jami
     */
    private String category;


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
     * @param shortLabel A shortLabel to reference the Feature - must be non-null
     * @param component  The Component to which this Feature is attached - must
     *                   be non-null.
     * @param type       the CvfeatureType of the Feature. Manadatory.
     */
    public Feature( String shortLabel,
                    Component component, CvFeatureType type ) {

        //super call sets up a valid AnnotatedObject
        super( shortLabel);
        if ( type == null ) {
            throw new NullPointerException( "Must have a CvFeatureType to create a Feature!" );
        }
        if ( component == null ) {
            throw new NullPointerException( "Cannot create Feature without a Component!" );
        }
        this.component = component;
        this.cvFeatureType = type;
    }

    @Deprecated
    public Feature( Institution owner, String shortLabel,
                    Component component, CvFeatureType type ) {
        this( shortLabel, component, type );
    }

    @PrePersist
    @PreUpdate
    protected void correctCategory() {
        if (this.component != null){
            if (this.component.getInteraction() != null){
                if (Hibernate.isInitialized(this.component.getInteraction().getAnnotations())){
                    if (ComplexUtils.isComplex(this.component.getInteraction())){
                        this.category = "modelled";
                    }
                    else {
                        this.category = "evidence";
                    }
                }
                else if (this.category == null){
                    this.category = "evidence";
                }
            }
            else if (this.category == null){
                this.category = "evidence";
            }
        }
        else if (this.category == null){
            this.category = "evidence";
        }
    }

    //----------------------- public methods ------------------------------

    @ManyToOne
    @JoinColumn( name = "featuretype_ac" )
    public CvFeatureType getCvFeatureType() {
        return cvFeatureType;
    }

    public void setCvFeatureType( CvFeatureType cvFeatureType ) {
        this.cvFeatureType = cvFeatureType;
    }

    @ManyToOne
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
    @ManyToOne
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
    @OneToMany( mappedBy = "feature", orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH} )
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
            if (range.getFeature() != null && !this.equals(range.getFeature())) {
               range.getFeature().removeRange(range);
            }
            this.ranges.add( range );
            range.setFeature( this );
        }
    }

    public void removeRange( Range range ) {
        this.ranges.remove( range );
    }

    @ManyToOne
    @JoinColumn( name = "identification_ac" )
    public CvFeatureIdentification getCvFeatureIdentification() {
        return cvFeatureIdentification;
    }

    public void setCvFeatureIdentification( CvFeatureIdentification cvFeatureIdentification ) {
        this.cvFeatureIdentification = cvFeatureIdentification;
    }

    @OneToMany( mappedBy = "parent", orphanRemoval = true )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
                org.hibernate.annotations.CascadeType.DELETE,
                org.hibernate.annotations.CascadeType.SAVE_UPDATE,
                org.hibernate.annotations.CascadeType.MERGE,
                org.hibernate.annotations.CascadeType.REFRESH,
                org.hibernate.annotations.CascadeType.DETACH} )
    @Override
    public Collection<FeatureXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent", orphanRemoval = true )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
                org.hibernate.annotations.CascadeType.DELETE,
                org.hibernate.annotations.CascadeType.SAVE_UPDATE,
                org.hibernate.annotations.CascadeType.MERGE,
                org.hibernate.annotations.CascadeType.REFRESH,
                org.hibernate.annotations.CascadeType.DETACH} )
    @Override
    public Collection<FeatureAlias> getAliases() {
        return super.getAliases();
    }

    @ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "ia_feature2annot",
            joinColumns = {@JoinColumn( name = "feature_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @Override
    public boolean equals( Object o ) {
        return equals( o, true, true );
    }

    public boolean equals( Object o, boolean includeLinkedFeature, boolean includeRanges ) {
        if ( this == o ) return true;
        if ( !( o instanceof Feature ) ) return false;
        if ( !super.equals( o ) ) return false;

        Feature feature = ( Feature ) o;

        if( includeRanges ) {
            if ( !CollectionUtils.isEqualCollection( ranges, feature.ranges ) )
                return false;
        }

        if ( cvFeatureIdentification != null ? !cvFeatureIdentification.equals( feature.cvFeatureIdentification ) : feature.cvFeatureIdentification != null ) {
            return false;
        }

        if ( cvFeatureType != null ? !cvFeatureType.equals( feature.cvFeatureType ) : feature.cvFeatureType != null ) {
            return false;
        }

        if (component == null && feature.getComponent() == null) {
        } else if ((component == null && feature.getComponent() != null) || (component != null && feature.getComponent() == null)) {
            return false;
        } else if (component.getAc() != null && feature.getComponent().getAc() != null) {
            if (!component.getAc().equals(feature.getComponent().getAc())) {
                return false;
            }
        } else if ( component != null ? !component.equals( feature.component, false ) : feature.component != null ) {
             return false;
         }

        // Make sure we don't end up in an infinite loop checking on linked features
        if( includeLinkedFeature ) {
            if ( binds != null ? !binds.equals( feature.binds, false, false ) : feature.binds != null )
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode( true, true );
    }

    public int hashCode( boolean includeBinds, boolean includeRanges ) {
        int result = super.hashCode();

        if( includeRanges ) {
            result = 31 * result + HashCodeUtils.collectionHashCode( ranges );
        }

        result = 31 * result + ( cvFeatureIdentification != null ? cvFeatureIdentification.hashCode() : 0 );
        result = 31 * result + ( cvFeatureType != null ? cvFeatureType.hashCode() : 0 );

        // hashcode component without including features
        result = 31 * result + ( component != null ? component.getAc() != null? component.getAc().hashCode() : component.hashCode( false ) : 0 );

        // make sure we don't end up in an infinite loop checking on linked features
        if( includeBinds ) {
            result = 31 * result + ( binds != null ? binds.hashCode( false, false ) : 0 );
        }

        return result;
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

    @Column(name = "objtype")
    private String getObjType() {
        return category;
    }

    private void setObjType(String category) {
        this.category = category;
    }
}




