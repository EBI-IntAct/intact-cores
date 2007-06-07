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
 * @see uk.ac.ebi.intact.model.Interactor
 */
public interface Interactor extends AnnotatedObject<InteractorXref, InteractorAlias> {

    String getObjClass();

    void setObjClass( String objClass );

    BioSource getBioSource();

    void setBioSource( BioSource bioSource );

    ///////////////////////////////////////
    // access methods for associations
    void setActiveInstances( Collection<Component> someActiveInstance );

    Collection<Component> getActiveInstances();

    void addActiveInstance( Component component );

    void removeActiveInstance( Component component );

    void setProducts( Collection<Product> someProduct );

    Collection<Product> getProducts();

    void addProduct( Product product );

    void removeProduct( Product product );

    CvInteractorType getCvInteractorType();

    void setCvInteractorType( CvInteractorType type );
}
