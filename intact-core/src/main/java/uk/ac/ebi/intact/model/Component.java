/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;
import uk.ac.ebi.intact.core.util.HashCodeUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The specific instance of an interactor which participates in an interaction.
 * <p/>
 * The same interactor may participate more than once, for example to describe different roles of the Interactors.
 * However, simple multimers should be expressed by the relativeQuantity attribute.
 *
 * @author hhe
 * @version $Id: Component.java 8310 2007-04-30 15:50:46Z skerrien $
 */
@Entity
@Table( name = "ia_component" )
public class Component extends AnnotatedObjectImpl<ComponentXref, ComponentAlias> implements Parameterizable<ComponentParameter>, ConfidenceHolder<ComponentConfidence> {

    private static final Log log = LogFactory.getLog( Component.class );

    public static final float STOICHIOMETRY_NOT_DEFINED = 0;

    public static final String NON_APPLICABLE = "N/A";

    ///////////////////////////////////////
    //attributes

    private String interactorAc;
    private String interactionAc;
    private String expressedInAc;


    /**
     * Represents the relative quantitity of the interactor participating in the interaction. Default is one. To
     * describe for example a homodimer, an interaction might have only one substrate, but the relative quantity would
     * be 2.
     */
    private float stoichiometry = STOICHIOMETRY_NOT_DEFINED;

    /**
     * The species the interactor was expressed into for the purpose of this interaction.
     */
    private BioSource expressedIn;

    ///////////////////////////////////////
    // associations

    /**
     * the interactor involved in this component's interaction.
     */
    private Interactor interactor;

    /**
     * the interaction this component in involved into.
     */
    private Interaction interaction;

    /**
     * The domain a Substrate binds by.
     */
    private Collection<Feature> bindingDomains = new ArrayList<Feature>();

    /**
     * @Deprecated was replaced by experimental and biological role.
     */
    private CvComponentRole componentRole;


    /**
     * Experimental roles for this component
     */
    private Collection<CvExperimentalRole> experimentalRoles;

    /**
     * Biological role of this component (eg. enzyme, target...).
     */
    private CvBiologicalRole biologicalRole;

    /**
     * Participant identification method that can override the one defined in the experiment.
     * If not specified, the experiment's is to be considered.
     */
    private CvIdentification participantIdentification;

    /**
     * Participant identifications method that can override the one defined in the experiment.
     * If not specified, the experiment's is to be considered.
     */
    private Collection<CvIdentification> participantDetectionMethods;

    /**
     * Experimental preparations for this component. The allowed terms can be found in this URL http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0346&termName=experimental%20preparation
     */
    private Collection<CvExperimentalPreparation> experimentalPreparations;

    /**
     * Parameters for this component. The allowed terms can be found in this URL http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0640&termName=parameter%20type
     */
    private Collection<ComponentParameter> componentParameters;

    /**
     * Confidences for this component.
     */
    private Collection<ComponentConfidence> confidences;

    ///////////////////////
    // Constructor

    /**
     * Necessary for hibernate, yet set to private as it should not be used for any other purpose.
     */
    public Component() {
        //super call sets creation time data
        super();
        this.componentParameters = new ArrayList<ComponentParameter>();
        this.confidences = new ArrayList<ComponentConfidence>();
    }

    public Component( Institution owner, Interaction interaction, Interactor interactor,
                      CvExperimentalRole experimentRole, CvBiologicalRole biologicalRole ) {

        this( owner, NON_APPLICABLE, interaction, interactor, experimentRole, biologicalRole );
        this.componentParameters = new ArrayList<ComponentParameter>();
        this.confidences = new ArrayList<ComponentConfidence>();
    }

    /**
     * Creates a valid Component instance. To be valid, a Component must have at least: <ul> <li>An onwer
     * (Institution)</li> <li>a biological source that the interaaction was expressed in</li> <li>an Interaction that
     * this instance is a Component of</li> <li>an Interactor which defines the entity (eg Protein) which takes part in
     * the Interaction and is therefore the 'core' of this Component</li> <li>the biological experimentalRole that this Component
     * plays in the Interaction (eg bait/prey etc)</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time.
     *
     * @param shortLabel       Label for this component
     * @param interaction      The Interaction this Component is a part of (non-null)
     * @param interactor       The 'wrapped active entity' (eg a Protein) that this Component represents in the Interaction
     *                         (non-null)
     * @param experimentalRole The experimental role played by this Component in the Interaction experiment (eg
     *                         bait/prey). This is a controlled vocabulary term (non-null)
     * @param biologicalRole   The biological role played by this Component in the Interaction experiment (eg
     *                         enzyme/target). This is a controlled vocabulary term (non-null)
     * @throws NullPointerException thrown if any of the parameters are not specified.
     */
    public Component( String shortLabel, Interaction interaction, Interactor interactor,
                      CvExperimentalRole experimentalRole, CvBiologicalRole biologicalRole ) {
        //super call sets creation time data
        super( shortLabel );

        setShortLabel(NON_APPLICABLE);

        if ( interaction == null ) {
            throw new NullPointerException( "Valid Component must have an Interaction set!" );
        }
        if ( interactor == null ) {
            throw new NullPointerException( "Valid Component must have an Interactor (eg Protein) set!" );
        }
        if ( experimentalRole == null ) {
            throw new NullPointerException( "Valid Component must have a non null experimentalRole." );
        }

        if ( biologicalRole == null ) {
            throw new NullPointerException( "Valid Component must have a non null biologicalRole." );
        }

        this.interaction = interaction;
        this.interactor = interactor;

        this.experimentalRoles = new ArrayList<CvExperimentalRole>();
        this.experimentalRoles.add( experimentalRole);

        this.biologicalRole = biologicalRole;

        this.componentParameters = new ArrayList<ComponentParameter>();
        this.confidences = new ArrayList<ComponentConfidence>();
    }

    @Deprecated
    public Component( Institution owner, String shortLabel, Interaction interaction, Interactor interactor,
                      CvExperimentalRole experimentalRole, CvBiologicalRole biologicalRole ) {
        this(shortLabel, interaction, interactor, experimentalRole, biologicalRole);
    }

    ///////////////////////////////////////
    // getters and setters
    @ManyToMany
    @JoinTable(
            name = "ia_component2exprole",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experimentalrole_ac" )}
    )
    public Collection<CvExperimentalRole> getExperimentalRoles() {

        if ( experimentalRoles == null ) {
            experimentalRoles = new ArrayList<CvExperimentalRole>();
        }

        return experimentalRoles;
    }

    public void setExperimentalRoles( Collection<CvExperimentalRole> experimentalRoles ) {
        this.experimentalRoles = experimentalRoles;
    }


    /**
     * Getter for property 'biologicalRole'.
     *
     * @return Value for property 'biologicalRole'.
     */
    @ManyToOne
    @JoinColumn( name = "biologicalrole_ac" )
    public CvBiologicalRole getCvBiologicalRole() {
        return biologicalRole;
    }

    /**
     * Setter for property 'biologicalRole'.
     *
     * @param biologicalRole Value to set for property 'biologicalRole'.
     */
    public void setCvBiologicalRole( CvBiologicalRole biologicalRole ) {
        this.biologicalRole = biologicalRole;
    }

    /**
     * Getter for property 'experimentalRole'.
     *
     * @return Value for property 'experimentalRole'.
     */
    @Deprecated
    @Transient
    public CvExperimentalRole getCvExperimentalRole() {
        if ( experimentalRoles == null ) {
            return null;
        }

        if ( experimentalRoles.isEmpty() ) {
            return null;
        }

        return experimentalRoles.iterator().next();
    }

    /**
     * Setter for property 'experimentalRole'.
     *
     * @param experimentalRole Value to set for property 'experimentalRole'.
     */
    @Deprecated
    public void setCvExperimentalRole( CvExperimentalRole experimentalRole ) {
        getExperimentalRoles().clear();
        getExperimentalRoles().add( experimentalRole );
    }

    /**
     * Getter for property 'particiantIdentification'.
     *
     * @return Value for property 'particiantIdentification'.
     */
    @Deprecated
    @Transient
    public CvIdentification getParticipantIdentification() {
        if ( participantDetectionMethods == null ) {
            return null;
        }

        if ( participantDetectionMethods.isEmpty() ) {
            return null;
        }

        return participantDetectionMethods.iterator().next();
    }

    /**
     * Setter for property 'particiantIdentification'.
     *
     * @param particiantIdentification Value to set for property 'particiantIdentification'.
     */
    @Deprecated
    public void setParticipantIdentification( CvIdentification particiantIdentification ) {
        getParticipantDetectionMethods().add( particiantIdentification );
    }

    @ManyToMany
    @JoinTable(
            name = "ia_component2part_detect",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    public Collection<CvIdentification> getParticipantDetectionMethods() {
        if ( participantDetectionMethods == null ) {
            participantDetectionMethods = new ArrayList<CvIdentification>();
        }
        return participantDetectionMethods;
    }

    public void setParticipantDetectionMethods( Collection<CvIdentification> participantDetectionMethods ) {
        this.participantDetectionMethods = participantDetectionMethods;
    }

    /**
     * Answers the question: "is the stoichiometry of the component defined ?".
     *
     * @return true if the stoichiometry is defined, false otherwise.
     */
    public boolean hasStoichiometry() {
        return ( stoichiometry != STOICHIOMETRY_NOT_DEFINED );
    }

    /**
     * Getter for property 'stoichiometry'.
     *
     * @return Value for property 'stoichiometry'.
     */
    public float getStoichiometry() {
        return stoichiometry;
    }

    /**
     * Setter for property 'stoichiometry'.
     *
     * @param stoichiometry Value to set for property 'stoichiometry'.
     */
    public void setStoichiometry( float stoichiometry ) {
        this.stoichiometry = stoichiometry;
    }

    /**
     * Getter for property 'componentParameters'.
     *
     * @return list of items for property 'componentParameters'.
     */
    @OneToMany( mappedBy = "component", orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<ComponentParameter> getParameters() {
        return this.componentParameters;
    }

    /**
     * Setter for property 'componentParameter'.
     *
     * @param componentParameters collection to set for property 'componentParameters'.
     */
    public void setParameters( Collection<ComponentParameter> componentParameters ) {
        if ( componentParameters == null ) {
            throw new IllegalArgumentException( "You must give a non null collection of parameters." );
        }
        this.componentParameters = componentParameters;
    }

    public void addParameter( ComponentParameter componentParameter ) {
        if ( !this.componentParameters.contains( componentParameter ) ) {
            this.componentParameters.add( componentParameter );
            componentParameter.setComponent( this );
        }
    }

    public void removeParameter( ComponentParameter componentParameter ) {
        this.componentParameters.remove( componentParameter );
    }

    @OneToMany( mappedBy = "component", orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<ComponentConfidence> getConfidences() {
        return confidences;
    }

    public void setConfidences( Collection<ComponentConfidence> someConfidences ) {
        this.confidences = someConfidences;
    }

    public void addConfidence( ComponentConfidence confidence ) {
        if ( !this.confidences.contains( confidence ) ) {
            this.confidences.add( confidence );
            confidence.setComponent( this);
        }
    }

    public void removeConfidence( ComponentConfidence confidence ) {
        this.confidences.remove( confidence);
    }

    /**
     * Getter for property 'expressedIn'.
     *
     * @return Value for property 'expressedIn'.
     */
    @ManyToOne

    @JoinColumn( name = "expressedin_ac" )
    public BioSource getExpressedIn() {
        return expressedIn;
    }

    /**
     * Setter for property 'expressedIn'.
     *
     * @param expressedIn Value to set for property 'expressedIn'.
     */
    public void setExpressedIn( BioSource expressedIn ) {
        this.expressedIn = expressedIn;
    }

    ///////////////////////////////////////
    // access methods for associations

    /**
     * Getter for property 'interactor'.
     *
     * @return Value for property 'interactor'.
     */
    @ManyToOne( targetEntity = InteractorImpl.class )
    @JoinColumn( name = "interactor_ac" )
    public Interactor getInteractor() {
        return interactor;
    }

    /**
     * @param interactor
     */
    public void setInteractor( Interactor interactor ) {
        this.interactor = interactor;
    }

    /**
     * Getter for property 'interaction'.
     *
     * @return Value for property 'interaction'.
     */
    @ManyToOne( targetEntity = InteractionImpl.class )
    @JoinColumn( name = "interaction_ac" )
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Setter for property 'interaction'.
     *
     * @param interaction Value to set for property 'interaction'.
     */
    public void setInteraction( Interaction interaction ) {
        this.interaction = interaction;
    }

    /**
     * Getter for property 'bindingDomains'.
     *
     * @return Value for property 'bindingDomains'.
     */
    @OneToMany( mappedBy = "component", orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.REFRESH} )
    public Collection<Feature> getFeatures() {
        return bindingDomains;
    }

    /**
     * Setter for property 'bindingDomains'.
     *
     * @param someBindingDomain Value to set for property 'bindingDomains'.
     */
    public void setFeatures( Collection<Feature> someBindingDomain ) {
        if ( someBindingDomain == null ) {
            throw new IllegalArgumentException( "features cannot be null." );
        }
        this.bindingDomains = someBindingDomain;
    }

    public void addFeature( Feature feature ) {
        // we accept duplicated features in a participant if we want to link a binding site to different features
        //if ( !this.bindingDomains.contains( feature ) ) {
            this.bindingDomains.add( feature );
            feature.setComponent( this );
        //}
    }

    public void removeFeature( Feature feature ) {
        boolean removed = this.bindingDomains.remove( feature );
        if ( removed ) {
            feature.setComponent( null );
        }
    }

    @Deprecated
    @Transient
    public Collection<Feature> getBindingDomains() {
        return getFeatures();
    }

    /**
     * Setter for property 'bindingDomains'.
     *
     * @param someBindingDomain Value to set for property 'bindingDomains'.
     */
    @Deprecated
    public void setBindingDomains( Collection<Feature> someBindingDomain ) {
        setFeatures(someBindingDomain);
    }

    @Deprecated
    public void addBindingDomain( Feature feature ) {
        addFeature(feature);
    }

    @Deprecated
    public void removeBindingDomain( Feature feature ) {
        removeFeature(feature);
    }

    /**
     * Getter for property 'cvComponentRole'.
     *
     * @return Value for property 'cvComponentRole'.
     */
    @Deprecated
    @Transient
    public CvComponentRole getCvComponentRole() {
        return componentRole;
    }

    /**
     * Setter for property 'cvComponentRole'.
     *
     * @param cvComponentRole Value to set for property 'cvComponentRole'.
     */
    @Deprecated
    public void setCvComponentRole( CvComponentRole cvComponentRole ) {
        componentRole = cvComponentRole;
    }

    /**
     * {@inheritDoc}
     */
    @ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH} )

    @JoinTable(
            name = "ia_component2annot",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }


    /**
     * {@inheritDoc}
     */
    @OneToMany( mappedBy = "parent", orphanRemoval = true )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH,
            org.hibernate.annotations.CascadeType.DETACH} )
    @Override
    public Collection<ComponentXref> getXrefs() {
        return super.getXrefs();
    }

    /**
     * {@inheritDoc}
     */
    @OneToMany( mappedBy = "parent", orphanRemoval = true )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH,
            org.hibernate.annotations.CascadeType.DETACH} )
    @Override
    public Collection<ComponentAlias> getAliases() {
        return super.getAliases();
    }

    // instance methods


    /**
     * Getter for property 'interactorAc'.
     *
     * @return Value for property 'interactorAc'.
     */ //attributes used for mapping BasicObjects
    @Column( name = "interactor_ac", insertable = false, updatable = false )
    public String getInteractorAc() {
        return this.interactorAc;
    }

    /**
     * Setter for property 'interactorAc'.
     *
     * @param ac Value to set for property 'interactorAc'.
     */
    public void setInteractorAc( String ac ) {
        this.interactorAc = ac;
    }

    /**
     * Getter for property 'interactionAc'.
     *
     * @return Value for property 'interactionAc'.
     */
    @Column( name = "interaction_ac", insertable = false, updatable = false )
    public String getInteractionAc() {
        return this.interactionAc;
    }

    /**
     * Setter for property 'interactionAc'.
     *
     * @param ac Value to set for property 'interactionAc'.
     */
    public void setInteractionAc( String ac ) {
        this.interactionAc = ac;
    }

    /**
     * Getter for property 'expressedInAc'.
     *
     * @return Value for property 'expressedInAc'.
     */
    @Column( name = "expressedin_ac", insertable = false, updatable = false )
    public String getExpressedInAc() {
        return this.expressedInAc;
    }

    /**
     * Setter for property 'expressedInAc'.
     *
     * @param ac Value to set for property 'expressedInAc'.
     */
    public void setExpressedInAc( String ac ) {
        this.expressedInAc = ac;
    }

    @ManyToMany
    @JoinTable(
            name = "ia_component2exp_preps",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    public Collection<CvExperimentalPreparation> getExperimentalPreparations() {
        if ( experimentalPreparations == null ) {
            experimentalPreparations = new ArrayList<CvExperimentalPreparation>();
        }
        return experimentalPreparations;
    }

    public void setExperimentalPreparations( Collection<CvExperimentalPreparation> experimentalPreparations ) {
        this.experimentalPreparations = experimentalPreparations;
    }

    /**
     * This method is specifically for the clone method of InteractionImpl class. The present setInteraction method
     * changes the argument passed, thus causing changes to the source of the clone.
     *
     * @param interaction the interaction to set. This simply replaces the existing interaction.
     */
    @Transient
    protected void setInteractionForClone( Interaction interaction ) {
        this.interaction = interaction;
    }

    /**
     * See the comments for {@link #setInteractionForClone(Interaction)} method.
     *
     * @param interactor
     */
    @Transient
    protected void setInteractorForClone( Interactor interactor ) {
        this.interactor = interactor;
    }

    /**
     * Equality for Components is currently based on <b>object identity</b> (i.e. the references point to the same
     * objects) for Interactors, Interactions and CvComponentRoles.
     *
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        return equals( o, true );
    }

    /**
     * Equality for Components, which allows to execute the equals excluding possible recursive entities.
     * For instance, Components have Features, and a Feature refers to the Component in its equal. When equaling Features,
     * we need a way to disable the recursion to avoid the infinite loop
     *
     * @param o Object to equal with
     * @param includeFeatures
     *          Include the features in the equal algorithm
     * @return true if they are equal
     */
    public boolean equals( Object o, boolean includeFeatures ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Component ) ) {
            return false;
        }

        if ( !super.equals( o ) ) {
            return false;
        }

        // don't call super because that's a OwnedObject !

        final Component component = ( Component ) o;

        // check cvs and interactor first, and then check the interaction
        if ( !CollectionUtils.isEqualCollection(getExperimentalRoles(), component.getExperimentalRoles()) ) {
            return false;
        }
        if ( !CollectionUtils.isEqualCollection(getParticipantDetectionMethods(), component.getParticipantDetectionMethods()) ) {
            return false;
        }
        if ( !CollectionUtils.isEqualCollection(getExperimentalPreparations(), component.getExperimentalPreparations()) ) {
            return false;
        }
        if ( !CollectionUtils.isEqualCollection(getParameters(), component.getParameters()) ) {
            return false;
        }
        if ( !CollectionUtils.isEqualCollection(getConfidences(), component.getConfidences()) ) {
            return false;
        }
        if ( biologicalRole != null ) {

            if (!biologicalRole.equals( component.getCvBiologicalRole() )){
                return false;
            }
        }
        else if (component.getCvBiologicalRole() != null){
            return false;
        }

        if ( expressedIn != null ) {
            if (!expressedIn.equals( component.getExpressedIn() )){
                return false;
            }
        }
        else if (component.getExpressedIn() != null){
            return false;
        }

        if ((interactor != null && component.getInteractor() == null) || (interactor == null && component.getInteractor() != null)){
            return false;
        } else if (interactor.getAc() != null && component.getInteractor().getAc() != null) {
            if (!interactor.getAc().equals(component.getInteractor().getAc())) {
                return false;
            }
        } else if ( interactor instanceof InteractorImpl && component.getInteractor() instanceof InteractorImpl ) {
            if ( interactor != null && !( ( InteractorImpl ) interactor ).equals( component.getInteractor(), false ) ) {
                return false;
            }
        }

        if ((interaction != null && component.getInteraction() == null) || (interaction == null && component.getInteraction() != null)){
            return false;
        } else if (interaction.getAc() != null && component.getInteraction().getAc() != null) {
            if (!interaction.getAc().equals(component.getInteraction().getAc())) {
                return false;
            }
        } else if ( interaction instanceof InteractionImpl && component.getInteraction() instanceof InteractorImpl ) {
            if ( interaction != null && !( ( InteractionImpl ) interaction ).equals( component.getInteraction(), false ) ) {
                return false;
            }
        }

        if ( includeFeatures ) {
            if ( !CollectionUtils.isEqualCollection( bindingDomains, component.getBindingDomains() ) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable, hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {
        return hashCode( true );
    }

    public int hashCode( boolean includeFeatures ) {
        int result = super.hashCode();

        //need these checks because we still have a no-arg
        //constructor at the moment.....

        if ( interactor != null ) {
            result = result * 31 + interactor.hashCode();
        }
        if ( interaction != null ) {
            result = result * 31 + interaction.hashCode();
        }

        if( experimentalRoles != null ) {
            result = result * 31 + HashCodeUtils.collectionHashCode( experimentalRoles );
        }

        if ( biologicalRole != null ) {
            result = result * 31 + biologicalRole.hashCode();
        }

        if ( participantDetectionMethods != null ) {
            result = result * 31 + HashCodeUtils.collectionHashCode( participantDetectionMethods );
        }
        if ( experimentalPreparations != null ) {
            result = result * 31 + HashCodeUtils.collectionHashCode( experimentalPreparations );
        }
        if ( componentParameters != null) {
            result = result * 31 + HashCodeUtils.collectionHashCode( componentParameters );
        }
        if ( confidences != null ) {
            result = result * 31 + HashCodeUtils.collectionHashCode( confidences );
        }

        if ( expressedIn != null ) {
            result = result * 31 + expressedIn.hashCode();
        }

        if( includeFeatures ) {
            result = result * 31 + HashCodeUtils.collectionHashCode( bindingDomains );
        }

        return result;
    }

    /**
     * Returns a cloned version of the current Component.
     *
     * @return a cloned version of the current Component. References to interactor and interaction are set to null.
     *         Features are deep cloned.
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Component copy = ( Component ) super.clone();

        setShortLabel(NON_APPLICABLE);

        // Reset interactor and interaction.
        copy.interaction = null;
        copy.interactor = null;
        copy.interactionAc = null;
        copy.interactorAc = null;

        // Make deep copies of Features.
        copy.bindingDomains = new ArrayList<Feature>( bindingDomains.size() );
        for ( Feature feature : bindingDomains ) {
            Feature copyFeature = ( Feature ) feature.clone();
            // Set the copy component as the component for feature copy.
            copyFeature.setComponentForClone( copy );
            // Add the cloned feature to the binding domains.
            copy.bindingDomains.add( copyFeature );
        }
        return copy;
    }
}
