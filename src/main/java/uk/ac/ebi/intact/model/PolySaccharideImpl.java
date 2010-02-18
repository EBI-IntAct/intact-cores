/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Arnaud Ceol (arnaud.ceol@gmail.com)
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.PolySaccharideImpl" )
@EditorTopic( name = "PolySaccharide" )
public class PolySaccharideImpl extends InteractorImpl implements SmallMolecule, Editable {

    public PolySaccharideImpl() {
    }

    public PolySaccharideImpl( String shortLabel, Institution owner, CvInteractorType type ) {
        super( shortLabel, owner, type );
    }
}
