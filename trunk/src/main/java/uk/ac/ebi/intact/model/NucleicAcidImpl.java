/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * An implementation of nucleic acid sequence.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.NucleicAcidImpl" )
@EditorTopic( name = "NucleicAcid" )
public class NucleicAcidImpl extends PolymerImpl implements NucleicAcid, Editable {

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public NucleicAcidImpl() {
    }

    /**
     * A valid NucleicAcid must have at least an onwer, a biological source, a
     * short label to refer to it and a molecule type specified.
     *
     * @param owner      The 'owner' of this instance
     * @param source     The biological source of the Protein observation
     * @param shortLabel A memorable label used to refer to this instance
     * @param type       The interactor type
     */
    public NucleicAcidImpl( Institution owner, BioSource source, String shortLabel,
                            CvInteractorType type
    ) {
        super( owner, source, shortLabel, type );
    }
}
