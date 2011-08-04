/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import uk.ac.ebi.intact.core.persister.IntactCore;
import uk.ac.ebi.intact.model.user.User;

import javax.persistence.*;
import java.util.*;

/**
 * Models a scientific paper and its relationship to (potentialy) many intact Experiments.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Entity
@Table( name = "ia_publication" )
public class Publication extends OwnedAnnotatedObject<PublicationXref, PublicationAlias> implements Editable {

    /**
     * List of experiments related to that publication.
     */
    private Collection<Experiment> experiments = new ArrayList<Experiment>();

    private List<LifecycleEvent> lifecycleEvents = new ArrayList<LifecycleEvent>();

    private CvPublicationStatus status;

    private User currentOwner;

    private User currentReviewer;

    ///////////////////////////
    // Constructors

    /**
     * Necessary constructor for hibernate to instanciate a bean.
     */
    public Publication() {
    }

    public Publication( Institution owner, String name ) {
        super( name, owner );
    }

    //////////////////////////////
    // Experiment handling

    public void addExperiment( Experiment experiment ) {
        if ( experiment == null ) {
            throw new NullPointerException( "experiment must not be null." );
        }
        if ( !experiments.contains( experiment ) ) {
            final Publication pub = experiment.getPublication();
            if ( pub != null && pub != this ) {
                throw new IllegalStateException( "You cannot overwrite an Experiment's publication (experiment:"+ experiment.getAc() +"). " +
                                                 "You must first unlink the underlying experiments from their publication" );
            } else {
                experiment.setPublication( this );
            }
            experiments.add( experiment );
        }
    }

    public void removeExperiment( Experiment experiment ) {
        if ( experiment == null ) {
            throw new NullPointerException( "experiment must not be null." );
        }
        if ( !experiments.contains( experiment ) ) {

            if ( experiment.getPublication() != null && experiment.getPublication() != this ) {
                throw new IllegalStateException( "The experiment ("+ experiment.getAc() +
                                                 ") you are trying to remove is linked to another publication ("+
                                                 experiment.getPublication().getAc() +")." );
            } else {
                experiment.setPublication( null );
            }
        } else {
            experiments.remove( experiment );
            experiment.setPublication( null );
        }
    }

    /**
     * Returns an unmodifiable collection of experiment.
     */
    @OneToMany( mappedBy = "publication", cascade = { CascadeType.REMOVE, CascadeType.REFRESH } )
    @OrderBy("created")
    public Collection<Experiment> getExperiments() {
        return experiments;
    }

    /**
     * Only meant to be used by hibernate. Users of the API should be using addExperiment.
     *
     * @param experiments
     */
    public void setExperiments( Collection<Experiment> experiments ) {
        this.experiments = experiments;
    }

    @OneToMany( mappedBy = "publication", orphanRemoval = true )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
                       org.hibernate.annotations.CascadeType.DELETE,
                       org.hibernate.annotations.CascadeType.SAVE_UPDATE,
                       org.hibernate.annotations.CascadeType.MERGE,
                       org.hibernate.annotations.CascadeType.REFRESH,
                       org.hibernate.annotations.CascadeType.DETACH} )
    @OrderBy("created")
    public List<LifecycleEvent> getLifecycleEvents() {
        return lifecycleEvents;
    }

    public void addLifecycleEvent( LifecycleEvent event ) {
        if(  event.getPublication() != null && event.getPublication() != this ) {
            throw new IllegalArgumentException( "You are trying to add an event to publication "+
                                                event.getPublication().getAc() +" that already belong to an other " +
                                                "publication " + getAc() );
        }
        event.setPublication( this );
        lifecycleEvents.add( event );
    }

    public boolean removeLifecycleEvent(LifecycleEvent evt) {
        return lifecycleEvents.remove(evt);
    }

    public void setLifecycleEvents( List<LifecycleEvent> lifecycleEvents ) {
        this.lifecycleEvents = lifecycleEvents;
    }

    @ManyToOne
    @JoinColumn( name = "status_ac" )
    @ForeignKey(name="FK_PUBLICATION_STATUS")
    public CvPublicationStatus getStatus() {
        return status;
    }

    public void setStatus( CvPublicationStatus status ) {
        this.status = status;
    }

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "owner_pk" )
    @ForeignKey(name="FK_PUBLICATION_OWNER")
    public User getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner( User currentOwner ) {
        this.currentOwner = currentOwner;
    }

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "reviewer_pk" )
    @ForeignKey(name="FK_PUBLICATION_REVIEWER")
    public User getCurrentReviewer() {
        return currentReviewer;
    }

    public void setCurrentReviewer( User currentReviewer ) {
        this.currentReviewer = currentReviewer;
    }

    ///////////////////////////////
    // Override AnnotatedObject

    @ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH} )
    @JoinTable(
            name = "ia_pub2annot",
            joinColumns = {@JoinColumn( name = "publication_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
                       org.hibernate.annotations.CascadeType.DELETE,
                       org.hibernate.annotations.CascadeType.SAVE_UPDATE,
                       org.hibernate.annotations.CascadeType.MERGE,
                       org.hibernate.annotations.CascadeType.REFRESH,
                       org.hibernate.annotations.CascadeType.DETACH} )
    @Override
    public Collection<PublicationXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent"  )
    @Cascade( value = {org.hibernate.annotations.CascadeType.PERSIST,
                       org.hibernate.annotations.CascadeType.DELETE,
                       org.hibernate.annotations.CascadeType.SAVE_UPDATE,
                       org.hibernate.annotations.CascadeType.MERGE,
                       org.hibernate.annotations.CascadeType.REFRESH,
                       org.hibernate.annotations.CascadeType.DETACH} )
    @Override
    public Collection<PublicationAlias> getAliases() {
        return super.getAliases();
    }

    ////////////////////////////
    // Object's override
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( "Publication" );
        sb.append( "{ac='" ).append( ac ); // + "', pmid='" ).append( pmid ).append( '\'' );

        if ( !xrefs.isEmpty() ) {
            sb.append( ", xrefs={" );
            for ( Iterator<PublicationXref> iterator = xrefs.iterator(); iterator.hasNext(); ) {
                Xref xref = iterator.next();
                sb.append( "xref('" + xref.getCvDatabase().getShortLabel() + "', '" + xref.getPrimaryId() + "', '" +
                           ( xref.getCvXrefQualifier() == null ? "-" : xref.getCvXrefQualifier().getShortLabel() ) + "')" );
                if ( iterator.hasNext() ) {
                    sb.append( ", " );
                }
            }
            sb.append( "}" );
        }

        if ( !annotations.isEmpty() ) {
            sb.append( ", annotations={" );
            for ( Iterator<Annotation> iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation annotation = iterator.next();
                sb.append( "annotation('" + annotation.getCvTopic().getShortLabel() + "', '" + annotation.getAnnotationText() + "')" );
                if ( iterator.hasNext() ) {
                    sb.append( ", " );
                }
            }
            sb.append( "}" );
        }

        sb.append( '}' );
        return sb.toString();
    }

    @Column(name="shortLabel", insertable = false, updatable = false)
    public String getPublicationId() {
        return super.getShortLabel();
    }

    public void setPublicationId(String publicationId) {
        setShortLabel(publicationId);
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Publication )) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        // shouldn't we be relying on the Xrefs solely ??
        // annotated object's equals relies on shortlabel, fullname and xrefs !!
        // one publication Xref in common should be enough ! either pmid, doi as primary-reference

        return true;
    }

    @Override
    public int hashCode() {
        return 29 * super.hashCode();
    }
}