/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;
import org.joda.time.Duration;
import org.joda.time.Instant;
import uk.ac.ebi.intact.annotation.EditorTopic;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persister.IntactCore;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Represents an interaction.
 * <p/>
 * Interaction is derived from Interactor, therefore a given interaction can participate in new interactions. This
 * allows to build up hierachical assemblies.
 * <p/>
 * An Interaction may also have other Interactions as products. This allows to model decomposition of complexes into
 * subcomplexes.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.InteractionImpl" )
@EditorTopic( name = "Interaction" )
public class InteractionImpl extends InteractorImpl
        implements Editable, Interaction {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(InteractionImpl.class);

    ///////////////////////////////////////
    //attributes

    /**
     * Last relevant change in this object that requires an export to IMEx.
     */
    private Date lastImexUpdate;

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String cvInteractionTypeAc;

    /**
     * TODO Represents ...
     */
    private Float kD;

    ///////////////////////////////////////
    // associations

    /**
     * Participant in this interaction.
     */
    private Collection<Component> components; // initialized via constructor

    /**
     * Experiments that have detected this interactions.
     */
    private Collection<Experiment> experiments; // initialized via constructor

    /**
     * Controlled vocabulary term defining the typs of this interaction.
     */
    private CvInteractionType cvInteractionType;

    private CvDatabase cvEvidenceType;

    /**
     * CRC of the interaction, that makes it unique and allows to check for redundancy.
     */
    private String crc;

    /**
     * Confidence values for this interaction.
     */
    private Collection<Confidence> confidences;  // initialized via constructor

    /**
     * Collection of interaction's parameter (eg. dissociation constant)
     */
    private Collection<InteractionParameter> interactionParameters;

    private Boolean predictedComplex;

    public InteractionImpl() {
        //super call sets creation time data
        super();

        components = new ArrayList<Component>( );
        experiments =  new ArrayList<Experiment>( );
        confidences = new ArrayList<Confidence>();
        interactionParameters = new ArrayList<InteractionParameter>();
    }

    /**
     * Creates a valid Interaction instance. This requires at least the following: <ul> <li> At least one valid
     * Experiment</li> <li>at least two Components</li> <li>an Interaction type (eg covalent binding)</li> <li>a short
     * label to refer to this instance</li> <li>an owner</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time. NOTE: the BioSource value is required for this class as it is not set via
     * Interactor - this will be taken from the (first) Experiment in the Collection parameter. It is tehrefore assumed
     * that the Experiment will be a valid one.
     *
     * @param experiments A Collection of Experiments which observed this Interaction (non-empty) NB The BioSource for
     *                    this Interaction will be taken from the first element of this Collection.
     * @param components  A Collection of Interaction components (eg Proteins). This cannot be null but may be empty to
     *                    allow creation of an Interaction for later population with Components
     * @param type        The type of Interaction observed - may be null if initially unkown
     * @param shortLabel  The short label to refer to this instance (non-null)
     * @param owner       the owner of this Interaction
     *
     * @throws NullPointerException     thrown if any of the specified paraneters are null OR the Experiment does not
     *                                  contain a BioSource.
     * @throws IllegalArgumentException thrown if either of the experiments or components Collections are empty, or if
     *                                  there are less than two components specified
     * @deprecated Use {@link #InteractionImpl(java.util.Collection,java.util.Collection,CvInteractionType,
     *CvInteractorType,String,Institution)} instead.
     */
    @Deprecated
    public InteractionImpl( Collection experiments, Collection components,
                            CvInteractionType type, String shortLabel,
                            Institution owner
    ) {
        this( experiments, components, type, null, shortLabel, owner );
    }

    /**
     * Creates a valid Interaction instance. This requires at least the following: <ul> <li> At least one valid
     * Experiment</li> <li>at least two Components</li> <li>an Interaction type (eg covalent binding)</li> <li>an
     * Interactor type</li> <li>a short label to refer to this instance</li> <li>an owner</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time. NOTE: the BioSource value is required for this class as it is not set via
     * Interactor - this will be taken from the (first) Experiment in the Collection parameter. It is tehrefore assumed
     * that the Experiment will be a valid one.
     *
     * @param experiments    A Collection of Experiments which observed this Interaction (non-empty) NB The BioSource
     *                       for this Interaction will be taken from the first element of this Collection.
     * @param components     A Collection of Interaction components (eg Proteins). This cannot be null but may be empty
     *                       to allow creation of an Interaction for later population with Components
     * @param type           The type of Interaction observed - may be null if initially unkown
     * @param interactorType The interactor type
     * @param shortLabel     The short label to refer to this instance (non-null)
     * @param owner          the owner of this Interaction
     *
     * @throws NullPointerException     thrown if any of the specified paraneters are null OR the Experiment does not
     *                                  contain a BioSource.
     * @throws IllegalArgumentException thrown if either of the experiments or components Collections are empty, or if
     *                                  there are less than two components specified
     */
    public InteractionImpl( Collection experiments, Collection components,
                            CvInteractionType type, CvInteractorType interactorType,
                            String shortLabel, Institution owner
    ) {
        super( shortLabel, owner, interactorType );

        setExperiments( experiments );
        setComponents( components );
        setCvInteractionType( type );
        confidences = new ArrayList<Confidence>();
        interactionParameters = new ArrayList<InteractionParameter>();
        // the bioSource has to be set using setBioSource( BioSource bs ).
    }

    /**
     * Creates a valid Interaction instance. This requires at least the following: <ul> <li> At least one valid
     * Experiment</li> <li>at least two Components</li> <li>an Interaction type (eg covalent binding)</li> <li>a short
     * label to refer to this instance</li> <li>an owner</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time. NOTE: the BioSource value is required for this class as it is not set via
     * Interactor - this will be taken from the (first) Experiment in the Collection parameter. It is tehrefore assumed
     * that the Experiment will be a valid one. <br> A default empty collection of component is created when calling
     * that constructor.
     *
     * @param experiments A Collection of Experiments which observed this Interaction (non-empty) NB The BioSource for
     *                    this Interaction will be taken from the first element of this Collection.
     * @param type        The type of Interaction observed - may be null if initially unkown
     * @param shortLabel  The short label to refer to this instance (non-null)
     * @param owner       the owner of this Interaction
     *
     * @throws NullPointerException     thrown if any of the specified paraneters are null OR the Experiment does not
     *                                  contain a BioSource.
     * @throws IllegalArgumentException thrown if either of the experiments or components Collections are empty, or if
     *                                  there are less than two components specified
     * @deprecated {@link #InteractionImpl(java.util.Collection,CvInteractionType,CvInteractorType,String,
     *Institution)} instead
     */
    public InteractionImpl( Collection experiments, CvInteractionType type,
                            String shortLabel, Institution owner
    ) {
        this( experiments, new ArrayList(), type, shortLabel, owner );
    }


    /**
     * Creates a valid Interaction instance. This requires at least the following: <ul> <li> At least one valid
     * Experiment</li> <li>at least two Components</li> <li>an Interaction type (eg covalent binding)</li> <li>an
     * Interactor type</li> <li>a short label to refer to this instance</li> <li>an owner</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time. NOTE: the BioSource value is required for this class as it is not set via
     * Interactor - this will be taken from the (first) Experiment in the Collection parameter. It is tehrefore assumed
     * that the Experiment will be a valid one. <br> A default empty collection of component is created when calling
     * that constructor.
     *
     * @param experiments    A Collection of Experiments which observed this Interaction (non-empty) NB The BioSource
     *                       for this Interaction will be taken from the first element of this Collection.
     * @param type           The type of Interaction observed - may be null if initially unkown
     * @param interactorType The interactor type
     * @param shortLabel     The short label to refer to this instance (non-null)
     * @param owner          the owner of this Interaction
     *
     * @throws NullPointerException     thrown if any of the specified paraneters are null OR the Experiment does not
     *                                  contain a BioSource.
     * @throws IllegalArgumentException thrown if either of the experiments or components Collections are empty, or if
     *                                  there are less than two components specified
     */
    public InteractionImpl( Collection experiments, CvInteractionType type,
                            CvInteractorType interactorType, String shortLabel,
                            Institution owner
    ) {
        this( experiments, new ArrayList(), type, interactorType, shortLabel, owner );
    }

    ///////////////////////////////////////
    // entity listeners

    @PrePersist
    public void onPrePersist() {
        super.onPrePersist();
        calculateCrc();
        synchronizeShortLabel();
    }

    @PreUpdate
    public void onPreUpdate() {
        super.onPreUpdate();
        calculateCrc();
    }

    public void calculateCrc() {
        CrcCalculator crcCalculator = new CrcCalculator();

        Instant before = new Instant();

        String newCrc = crcCalculator.crc64(this);

        if (crc == null){
            crc = newCrc;
        }
        else if (!crc.equals(newCrc)){
            crc = newCrc;
        }

        Instant after = new Instant();

        if (log.isDebugEnabled()) log.debug("Calculated crc for interaction '" + getShortLabel() +
                "' in " + new Duration(before, after).getMillis() + "ms: " + crc);
    }

    /**
     * Update the shortlabel based on the data available in the database.
     */
    public void synchronizeShortLabel() {
        if( IntactContext.currentInstanceExists() ) {
            if( IntactContext.getCurrentInstance().getConfig().isAutoUpdateInteractionLabel() ) {
                String shortLabel = getShortLabel();
                String newShortLabel = InteractionUtils.syncShortLabelWithDb(shortLabel);

                if (!shortLabel.equals(newShortLabel)) {
                    if (log.isDebugEnabled()) log.debug("Interaction with label '"+shortLabel+"' renamed '"+newShortLabel+"'" );
                    setShortLabel(newShortLabel);
                }
            }
        } else {
            log.warn( "There is no IntAct Context initialized, skipping interaction shortlabel synchronization." );
        }
    }

    ///////////////////////////////////////
    //access methods for attributes

    @Transient
//    @Temporal( value = TemporalType.TIMESTAMP )
    public Date getLastImexUpdate() {
        return lastImexUpdate;
    }

    public void setLastImexUpdate( Date lastImexUpdate ) {
        this.lastImexUpdate = lastImexUpdate;
    }

    public Float getKD() {
        return kD;
    }

    public void setKD( Float kD ) {
        this.kD = kD;
    }

    ///////////////////////////////////////
    // access methods for associations

    public void setComponents( Collection<Component> someComponent ) {
        if ( someComponent == null ) {
            throw new NullPointerException( "Cannot create an Interaction without any Components!" );
        }

        this.components = someComponent;
    }

    @OneToMany( mappedBy = "interaction", orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.REFRESH} )
    public Collection<Component> getComponents() {
        if (components == null) {
            components = new ArrayList<Component>();
        }
        return components;
    }

    public void addComponent( Component component ) {
        // make sure we don't add twice the same instance, but still allow multiple components with same data attached.
        for ( Component c : components ) {
            if( c == component ){
                return;
            }
        }

        this.components.add( component );
        component.setInteraction( this );
    }

    public void removeComponent( Component component ) {
        boolean removed = this.components.remove( component );
        if ( removed ) {
            component.setInteraction( null );
        }
    }

    public void setExperiments( Collection<Experiment> someExperiment ) {

        if ( someExperiment == null ) {
            throw new NullPointerException( "Cannot create an Interaction without an Experiment!" );
        }
        /*
        if( ( someExperiment.isEmpty() ) || ( !( someExperiment.iterator().next() instanceof Experiment ) ) ) {
            throw new IllegalArgumentException( "must have at least one VALID Experiment to create an Interaction" );
        } */

        this.experiments = someExperiment;
    }

    @ManyToMany
    @JoinTable(
            name = "ia_int2exp",
            joinColumns = {@JoinColumn( name = "interaction_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experiment_ac" )}
    )
    public Collection<Experiment> getExperiments() {
        return experiments;
    }

    public void addExperiment( Experiment experiment ) {
        if ( !this.experiments.contains( experiment ) ) {
            this.experiments.add( experiment );

            if (experiment != null) {
                experiment.addInteraction( this );
            }
        }
    }

    public void removeExperiment( Experiment experiment ) {
        boolean removed = this.experiments.remove( experiment );
        if ( removed ) {
            experiment.removeInteraction( this );
        }
    }

    @ManyToOne
    @JoinColumn( name = "interactiontype_ac" )
    public CvInteractionType getCvInteractionType() {
        return cvInteractionType;
    }

    public void setCvInteractionType( CvInteractionType cvInteractionType ) {
        this.cvInteractionType = cvInteractionType;
    }

    //attributes used for mapping BasicObjects - project synchron
    @Column( name = "interactiontype_ac", insertable = false, updatable = false )
    public String getCvInteractionTypeAc() {
        return this.cvInteractionTypeAc;
    }

    public void setCvInteractionTypeAc( String ac ) {
        this.cvInteractionTypeAc = ac;
    }

    @ManyToOne
    @JoinColumn( name = "evidence_type_ac" )
    public CvDatabase getCvEvidenceType() {
        return cvEvidenceType;
    }

    public void setCvEvidenceType( CvDatabase cvEvidenceType ) {
        this.cvEvidenceType = cvEvidenceType;
    }

    public void setConfidences( Collection<Confidence> someConfidences ) {
        this.confidences = someConfidences;
    }

    public void addConfidence( Confidence confidence ) {
        if ( !this.confidences.contains( confidence ) ) {
            this.confidences.add( confidence );
            confidence.setInteraction( this);
        }
    }

    public void removeConfidence( Confidence confidence ) {
        this.confidences.remove( confidence);
    }

    public void setParameters( Collection<InteractionParameter> someInteractionParameters ) {
        if( someInteractionParameters == null ) {
            throw new IllegalArgumentException( "You must set a non null collection of parameter" );
        }
        this.interactionParameters = someInteractionParameters;
    }

    public void addParameter( InteractionParameter interactionParameter ) {
        if ( !this.interactionParameters.contains( interactionParameter ) ) {
            this.interactionParameters.add( interactionParameter );
            interactionParameter.setInteraction( this );
        }
    }

    public void removeParameter( InteractionParameter interactionParameter ) {
        this.interactionParameters.remove( interactionParameter );
    }

    @OneToMany( mappedBy = "interaction", orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<Confidence> getConfidences() {
        return confidences;
    }

    @OneToMany( mappedBy = "interaction", orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<InteractionParameter> getParameters() {
        return interactionParameters;
    }

    ///////////////////////////////////////
    // instance methods

    /**
     * Returns the first components marked as bait. If no such components is found, return null.
     *
     * @return The first components marked as bait, otherwise null.
     *
     * @deprecated this method should not be part of the model. A utility class should instead,
     *             for instance: uk.ac.ebi.intact.model.util.InteractionUtils.
     */
    @Transient
    @Deprecated
    public Component getBait() {
        for ( Component component : components ) {

            Collection<CvExperimentalRole> roles = component.getExperimentalRoles();
            if ( roles == null ) {
                return null;
            }

            for ( CvExperimentalRole role : roles ) {
                if ( role.getShortLabel().equals( "bait" ) ) {
                    return component;
                }
            }
        }
        return null;

    }

    @Column(name="crc64", length = 16)
    public String getCrc() {
        //calculateCrc();
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "predicted_complex")
    public boolean isPredictedComplex() {
        return predictedComplex != null && predictedComplex;
    }

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "predicted_complex")
    public void setPredictedComplex(Boolean predictedComplex) {
        this.predictedComplex = predictedComplex;
    }

    @Override
    @Transient
    protected CvDagObject getInteractorOrInteractionType() {
        return cvInteractionType;
    }

    /**
     * Equality for Interactions is currently based on equality for <code>Interactors</code>, CvInteractionType, kD and
     * Components.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see InteractorImpl
     * @see Component
     * @see CvInteractionType
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Interaction ) ) {
            return false;
        }

        if ( !super.equals( o ) ) {
            return false;
        }

        return equals(o, true);
    }

    /**
     * Equals method that optionally checks on the components
     */
    public boolean equals( Object o, boolean checkComponents) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Interaction ) ) {
            return false;
        }

        if ( !super.equals( o, false ) ) {
            return false;
        }

        final Interaction interaction = ( Interaction ) o;

        if ( cvInteractionType != null ) {
            if ( !cvInteractionType.equals( interaction.getCvInteractionType() ) ) {
                return false;
            }
        } else {
            if ( interaction.getCvInteractionType() != null ) {
                return false;
            }
        }

        if ( kD != null ) {
            if ( !kD.equals( interaction.getKD() ) ) {
                return false;
            }
        } else {
            if ( interaction.getKD() != null ) {
                return false;
            }
        }

        if (checkComponents) {
            // the components must be initialized to check if equals
            Collection<Component> initializedComponents1 = IntactCore.ensureInitializedParticipants(this);
            Collection<Component> initializedComponents2 = IntactCore.ensureInitializedParticipants(interaction);

            if (!CollectionUtils.isEqualCollection(initializedComponents1, initializedComponents2)) {
                return false;
            }

            // the confidences must be initialized to check if equals
            Collection<Confidence> initializedConfidence1 = IntactCore.ensureInitializedConfidences(this);
            Collection<Confidence> initializedConfidence2 = IntactCore.ensureInitializedConfidences(interaction);

            if (!CollectionUtils.isEqualCollection( initializedConfidence1, initializedConfidence2)){
                return false;
            }

            // the parameters must be initialized to check if equals
            Collection<InteractionParameter> initializedParameter1 = IntactCore.ensureInitializedInteractionParameters(this);
            Collection<InteractionParameter> initializedParameter2 = IntactCore.ensureInitializedInteractionParameters(interaction);
            if (!CollectionUtils.isEqualCollection( initializedParameter1, initializedParameter2)){
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int code = super.hashCode();

        if ( cvInteractionType != null ) {
            code = 29 * code + cvInteractionType.hashCode();
        }
        if ( kD != null ) {
            code = 29 * code + kD.hashCode();
        }
//        for (Iterator iterator = components.iterator(); iterator.hasNext();) {
//            Component components = (Component) iterator.next();
//            code = 29 * code + components.hashCode();
//        }

        return code;
    }

    /**
     * Returns a cloned version of the current object.
     *
     * @return a cloned version of the current Interaction with the folowing exceptions. <ul> <li>Experiments are not
     *         cloned. The experiments for the cloned interaction is empty.</li> <li>New components but with the same
     *         proteins. The new components has the cloned interaction as their interaction.</li> </ul>
     *
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        InteractionImpl copy = ( InteractionImpl ) super.clone();

        // Not copying any experiments.
        copy.experiments = new ArrayList<Experiment>();

        copy.setActiveInstances( new ArrayList<Component>() );
        // New components, will contain same number of componets. Can't use
        // clone here as components are OJB list proxies if an interation
        // is loaded from the database.
        copy.components = new ArrayList<Component>( components.size() );

        // Make deep copies.
        for ( Component comp : components ) {
            // The cloned component.
            Component copyComp = ( Component ) comp.clone();
            // Set the interactor as the current cloned interactions.
            copyComp.setInteractionForClone( copy );
            Interactor interactor = comp.getInteractor();
            interactor.setActiveInstances( new ArrayList<Component>() );
            copyComp.setInteractorForClone( interactor );
            copy.components.add( copyComp );
        }

        copy.confidences = new ArrayList<Confidence>();
        copy.interactionParameters = new ArrayList<InteractionParameter>();

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Interaction: ").append(getAc()).append(" Label: ").append(getShortLabel()).append(" [").append(NEW_LINE);

        if (IntactCore.isInitialized(getComponents())) {
            if ( null != this.getComponents() ) {
                for ( Object o : this.getComponents() ) {
                    sb.append(( ( Component ) o ).getInteractor());
                }
            }
        } else {
            sb.append("Components not initialized");
        }
        sb.append("] Interaction").append(NEW_LINE);
        return sb.toString();
    }
} // end Interaction




