/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.InteractionImpl
 */
public interface Interaction extends Interactor {

    public Float getKD();

    public void setKD( Float kD );

    public void setComponents( Collection<Component> someComponent );

    public Collection<Component> getComponents();

    public void addComponent( Component component );

    public void removeComponent( Component component );

    public void setReleased( Collection<Product> someReleased );

    public Collection<Product> getReleased();

    public void addReleased( Product product );

    public void removeReleased( Product product );

    public void setExperiments( Collection<Experiment> someExperiment );

    public Collection<Experiment> getExperiments();

    public void addExperiment( Experiment experiment );

    public void removeExperiment( Experiment experiment );

    public CvInteractionType getCvInteractionType();

    public void setCvInteractionType( CvInteractionType cvInteractionType );

    //attributes used for mapping BasicObjects - project synchron
    public String getCvInteractionTypeAc();

    public void setCvInteractionTypeAc( String ac );

    public Component getBait();

    public boolean equals( Object o );

    public int hashCode();

    public String toString();
}
