/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
public class InteractionImpl extends InteractorImpl implements Editable, Interaction {

    ///////////////////////////////////////
    //attributes

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
     * TODO comments
     */
    private Collection<Component> components; // initialized via constructor

    /**
     * TODO comments
     */
    private Collection<Product> released; // mapping not implemented yet

    /**
     * TODO comments
     */
    private Collection<Experiment> experiments; // initialized via constructor

    /**
     * TODO comments
     */
    private CvInteractionType cvInteractionType;

    public InteractionImpl() {
        //super call sets creation time data
        super();
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
    //access methods for attributes

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

    @OneToMany( mappedBy = "interaction", cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<Component> getComponents() {
        return components;
    }

    public void addComponent( Component component ) {
        if ( !this.components.contains( component ) ) {
            this.components.add( component );
            component.setInteraction( this );
        }
    }

    public void removeComponent( Component component ) {
        boolean removed = this.components.remove( component );
        if ( removed ) {
            component.setInteraction( null );
        }
    }

    public void setReleased( Collection<Product> someReleased ) {
        this.released = someReleased;
    }

    @Transient
    public Collection<Product> getReleased() {
        return released;
    }

    public void addReleased( Product product ) {
        if ( !this.released.contains( product ) ) {
            this.released.add( product );
            product.setInteraction( this );
        }
    }

    public void removeReleased( Product product ) {
        boolean removed = this.released.remove( product );
        if ( removed ) {
            product.setInteraction( null );
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
            experiment.addInteraction( this );
        }
    }

    public void removeExperiment( Experiment experiment ) {
        boolean removed = this.experiments.remove( experiment );
        if ( removed ) {
            experiment.removeInteraction( this );
        }
    }

    @ManyToOne( fetch = FetchType.LAZY )
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

    ///////////////////////////////////////
    // instance methods

    /**
     * Returns the first components marked as bait. If no such components is found, return null.
     *
     * @return The first components marked as bait, otherwise null.
     *
     * @deprecated this method should not be part of the model. A utility class should instead,
     *             for isntance: uk.ac.ebi.intact.model.util.InteractionUtils.
     */
    @Transient
    @Deprecated
    public Component getBait() {
        for ( Component component : components ) {
            CvExperimentalRole role = component.getCvExperimentalRole();
            if ( null == role ) {
                return null;
            }
            if ( role.getShortLabel().equals( "bait" ) ) {
                return component;
            }
        }
        return null;

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
        if ( super.equals( o ) ) {
            return true;
        }

        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Interaction ) ) {
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

        return CollectionUtils.isEqualCollection( getComponents(), interaction.getComponents() );
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
        return copy;
    }

    @Override
    public String toString() {
        String result = "Interaction: " + getAc() + " Label: " + getShortLabel()
                        + " [" + NEW_LINE;
        if ( null != this.getComponents() ) {
            for ( Iterator iter = this.getComponents().iterator(); iter.hasNext(); ) {
                result += ( ( Component ) iter.next() ).getInteractor();
            }
        }
        return result + "] Interaction" + NEW_LINE;
    }
} // end Interaction




