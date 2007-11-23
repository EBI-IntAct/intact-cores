/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Models a scientific paper and its relationship to (potentialy) many intact Experiments.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Entity
@Table( name = "ia_publication" )
public class Publication extends AnnotatedObjectImpl<PublicationXref, PublicationAlias> implements Editable {

    /**
     * List of experiments related to that publication.
     */
    private Collection<Experiment> experiments = new ArrayList<Experiment>();

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
            if ( experiment.getPublication() != this ) {
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
            experiments.remove( experiment );
            if ( experiment.getPublication() != null && experiment.getPublication() != this ) {
                throw new IllegalStateException( "The experiment ("+ experiment.getAc() +
                                                 ") you are trying to remove is linked to an other publication ("+
                                                 experiment.getPublication().getAc() +")." );
            } else {
                experiment.setPublication( null );
            }
        }
    }

    /**
     * Returns an unmodifiable collection of experiment.
     */
    @OneToMany( mappedBy = "publication" )
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

    ///////////////////////////////
    // Override AnnotatedObject

    @ManyToMany( cascade = {CascadeType.PERSIST} )
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
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<PublicationXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
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