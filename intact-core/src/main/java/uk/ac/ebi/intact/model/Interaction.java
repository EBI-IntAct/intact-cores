/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;
import java.util.Date;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.InteractionImpl
 */
public interface Interaction extends Interactor, Parameterizable<InteractionParameter>, ConfidenceHolder<Confidence> {

    Date getLastImexUpdate();

    void setLastImexUpdate( Date lastImexUpdate );

    Float getKD();

    void setKD( Float kD );

    void setComponents( Collection<Component> someComponent );

    Collection<Component> getComponents();

    void addComponent( Component component );

    void removeComponent( Component component );

    void setExperiments( Collection<Experiment> someExperiment );

    Collection<Experiment> getExperiments();

    void addExperiment( Experiment experiment );

    void removeExperiment( Experiment experiment );

    CvInteractionType getCvInteractionType();

    void setCvInteractionType( CvInteractionType cvInteractionType );

    //attributes used for mapping BasicObjects - project synchron
    String getCvInteractionTypeAc();

    void setCvInteractionTypeAc( String ac );

    Component getBait();

    String getCrc();

    void setCrc( String crc );

    boolean isPredictedComplex();

    void setPredictedComplex(Boolean predictedComplex);
}
