/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;
import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents one experiment. Describes the conditions in which the experiment has been performed. The information
 * should allow to classify experiments and make them comparable. The Experiment object does not aim to contain enough
 * information to redo the experiment, it refers to the original publication for this purpose.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@Table( name = "ia_experiment" )
@EditorTopic
public class Experiment extends AnnotatedObjectImpl<ExperimentXref, ExperimentAlias> implements Editable, Searchable {

    ///////////////////////////////////////
    // associations

    private String detectionMethodAc;
    private String identMethodAc;
    private String relatedExperimentAc;
    private String cvIdentificationAc;
    private String cvInteractionAc;
    private String bioSourceAc;
    private String publicationAc;

    /**
     * TODO comments TODO would be easier/meaningful to have plural
     */
    private Collection<Interaction> interactions = new ArrayList<Interaction>();

    /**
     * One experiment should group all interactions from a publication which have been performed under the same
     * conditions. However, one experiment might explicitely involve different conditions, for example a time series, or
     * before and after a stimulus. This association can establish this relation.
     * <p/>
     * This might be extended into an association class which could state the type of relationship.
     */
    // TODO not clear, why only one Experiment ?
    private Experiment relatedExperiment;

    /**
     * TODO comments
     */
    private CvIdentification cvIdentification;

    /**
     * TODO comments
     */
    private CvInteraction cvInteraction;

    /**
     * TODO comments
     */
    private BioSource bioSource;

    /**
     * Publication to which relates that experiment.
     */
    private Publication publication;

    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public Experiment() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Experiment instance. A valid Experiment must contain at least a shortLabel that refers to it, an
     * owner of the Experiment and also the biological source of the experiment data (ie organism). A side-effect of
     * this constructor is to set the <code>created</code> and <code>updated</code> fields of the instance to the
     * current time.
     *
     * @param owner      The <code>Institution</code> which owns this Experiment (non-null)
     * @param shortLabel A String which can be used to refer to the Experiment (non-null)
     * @param source     The biological source of the experimental data (non-null)
     *
     * @throws NullPointerException thrown if any of the parameters are not set
     */
    public Experiment( Institution owner, String shortLabel, BioSource source ) {

        //TODO Q: does it make sense to create an Experiment without interactions? If not then all the Cv stuff (eg ident method etc) needs to be set also...
        super( shortLabel, owner );
        setBioSource( source );
    }

    /**
     * Builds a SHALLOW copy of the Experiment parameter. This means that a new Experiment instance is returned, but you
     * should be aware that the object references it contains <b>point to the objects referenced in the original object
     * </b>.
     *
     * @param experiment The Experiment you want a shallow copy of
     *
     * @return Experiment a new Experiment instance containing new references to the parameter object's attributes.
     *
     * @throws NullPointerException thrown if required items are not present in the parameter Experiment instance.
     */
    public static Experiment getShallowCopy( Experiment experiment ) {

        Experiment ex = new Experiment( experiment.getOwner(),
                                        experiment.getShortLabel(),
                                        experiment.getBioSource() );
        ex.ac = ( experiment.getAc() );
        ex.setAnnotations( experiment.getAnnotations() );
        ex.setCvInteraction( experiment.getCvInteraction() );
        ex.setCvIdentification( experiment.getCvIdentification() );
        ex.setFullName( experiment.getFullName() );
        ex.setRelatedExperiment( experiment.getRelatedExperiment() );
        ex.setXrefs( experiment.getXrefs() );
        ex.setPublication( experiment.getPublication() );
        return ex;
    }

    ///////////////////////////////////////
    // access methods for associations

    // TODO could wipe all existing interaction ... maybe dangerous.
    // TODO shold also allow to have no interaction if the collection is null or empty.

    public void setInteractions( Collection<Interaction> someInteraction ) {
        if ( someInteraction == null ) {
            throw new IllegalArgumentException( "Interactions must not be null." );
        }
        this.interactions = someInteraction;
    }

    @ManyToMany( targetEntity = InteractionImpl.class, mappedBy = "experiments" )
    public Collection<Interaction> getInteractions() {
        return interactions;
    }

    /**
     * Add an interaction in that experiment. It also adds the experiment in the interaction.
     *
     * @param interaction
     */
    public void addInteraction( Interaction interaction ) {
        if ( !this.interactions.contains( interaction ) ) {
            this.interactions.add( interaction );
            interaction.addExperiment( this );
        }
    }

    /**
     * Removes given interaction from the experiment.
     *
     * @param interaction
     */
    public void removeInteraction( Interaction interaction ) {
        boolean removed = this.interactions.remove( interaction );
        if ( removed ) {
            interaction.removeExperiment( this );
        }
    }

    @OneToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "relatedexperiment_ac", referencedColumnName = "ac" )
    public Experiment getRelatedExperiment() {
        return relatedExperiment;
    }

    public void setRelatedExperiment( Experiment experiment ) {
        this.relatedExperiment = experiment;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "identmethod_ac" )
    public CvIdentification getCvIdentification() {
        return cvIdentification;
    }

    public void setCvIdentification( CvIdentification cvIdentification ) {
        this.cvIdentification = cvIdentification;
    }

    /**
     * Get Interaction Detection Method.
     *
     * @return
     */
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "detectmethod_ac" )
    public CvInteraction getCvInteraction() {
        return cvInteraction;
    }

    /**
     * Set Interaction Detection Method.
     *
     * @param cvInteraction the method.
     */
    public void setCvInteraction( CvInteraction cvInteraction ) {
        this.cvInteraction = cvInteraction;
    }

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Cascade (value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn( name = "biosource_ac" )
    public BioSource getBioSource() {
        return bioSource;
    }

    /**
     * Set the Biosource. Cannot be null.
     *
     * @param bioSource the biosource.
     */
    public void setBioSource( BioSource bioSource ) {

// We shouldn't not forbiden bioSource to be set to null as it is not mandatory in psi-mi
//        if ( bioSource == null ) {
//            throw new NullPointerException( "valid Experiment must have a BioSource!" );
//        }

        this.bioSource = bioSource;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "publication_ac" )
    public Publication getPublication() {
        return publication;
    }

    public void setPublication( Publication publication ) {
        this.publication = publication;
    }

    public void setCvIdentificationAc( String ac ) {
        this.cvIdentificationAc = ac;
    }

    @Column( name = "detectmethod_ac", insertable = false, updatable = false )
    public String getCvInteractionAc() {
        return this.cvInteractionAc;
    }

    public void setCvInteractionAc( String ac ) {
        this.cvInteractionAc = ac;
    }

    @Column( name = "biosource_ac", insertable = false, updatable = false )
    public String getBioSourceAc() {
        return this.bioSourceAc;
    }

    public void setBioSourceAc( String ac ) {
        this.bioSourceAc = ac;
    }

    @ManyToMany( cascade = CascadeType.PERSIST)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "ia_exp2annot",
            joinColumns = {@JoinColumn( name = "experiment_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }


    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<ExperimentXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<ExperimentAlias> getAliases() {
        return super.getAliases();
    }

    /**
     * Equality for Experiments is currently based on equality for <code>AnnotatedObjects</code> and BioSources only.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see AnnotatedObject
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final Experiment that = ( Experiment ) o;

        if ( !bioSource.equals( that.bioSource ) ) {
            return false;
        }
        if ( publication != null ? !publication.equals( that.publication ) : that.publication != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + bioSource.hashCode();
        result = 29 * result + ( publication != null ? publication.hashCode() : 0 );
        return result;
    }

    /**
     * Returns a cloned version of the current object.
     *
     * @return a cloned version of the current Experiment with following exceptions. <ul> <li>Interactions are not
     *         cloned. The interactions for the cloned experiment is empty</li> <li>New components but with the same
     *         proteins. The new components has the cloned interaction as their interaction.</li> </ul>
     *
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Experiment copy = ( Experiment ) super.clone();

        // Not copying any interactions.
        copy.interactions = new ArrayList<Interaction>();

        return copy;
    }

    ///////////////////////////////////////
    // instance methods

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer( 128 );

        result.append( "Experiment [AC: " + this.getAc() + " Shortlabel: " + getShortLabel() );
        result.append( " BioSource: " + ( getBioSource() == null ? "-" : getBioSource().getShortLabel() ) );
        result.append( NEW_LINE );
        result.append( "CvIdentification: " + ( cvIdentification == null ? "-" : cvIdentification.getShortLabel() ) );
        result.append( NEW_LINE );
        result.append( "CvInteraction: " + ( cvInteraction == null ? "NOT SPECIFIED" : cvInteraction.getShortLabel() ) );
        result.append( NEW_LINE );

        // TODO in experiments with many interactions this can throw OutOfMemoryErrors
//        result.append( "Interactions (" );
//        if( null != getInteractions() ) {
//            for( Iterator iterator = interactions.iterator(); iterator.hasNext(); ) {
//                Interaction interaction = (Interaction) iterator.next();
//                result.append( interaction.getShortLabel() ).append( ", " );
//            }
//        }
//        result.append( ')' );
//        result.append( NEW_LINE );
        result.append( ']' );

        return result.toString();
    }

} // end Experiment




