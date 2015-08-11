/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * The factory to create various polymer types.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class PolymerFactory {

    private PolymerFactory() {
    }

    /**
     * Creates an instance of Polymer type based on type.
     *
     * @param owner      The Institution which owns this instance
     * @param source     The biological source of the Protein observation
     * @param shortLabel The memorable label to identify this instance
     * @param type       The interactor type. This alone decides which type to create - should be not null.
     *
     * @return an instance of <code>Polymer</code> based on <code>type</code> or null if a Polymer cannot be
     * instantiated (for exampple, no MI found in given type)
     */
    public static Polymer factory( Institution owner, BioSource source, String shortLabel, CvInteractorType type ) {
        Polymer polymer = null;

        if ( CvObjectUtils.isProteinType( type ) ) {
            polymer = new ProteinImpl( owner, source, shortLabel, type );
        } else if ( CvObjectUtils.isPeptideType( type ) ) {
            polymer = new ProteinImpl( owner, source, shortLabel, type );
        } else if ( CvObjectUtils.isNucleicAcidType( type ) ) {
            polymer = new NucleicAcidImpl( owner, source, shortLabel, type );
        } else {
            throw new IllegalArgumentException( "The given MI is neither a Protein, a Peptide or a NUcleicAcid MI: " + type );
        }

        return polymer;
    }

    /**
     * Returns the Xref with MI number for given CV object
     *
     * @param cvobj the CV to search for MI
     *
     * @return xref with MI or null if no xref found whose primaryid starts with 'MI:'.
     */
    private static Xref getMIXref( CvInteractorType cvobj ) {
        final Collection<CvObjectXref> xrefs = XrefUtils.getIdentityXrefs( cvobj );
        if( xrefs.size() != 1 ) {
           throw new IllegalStateException( "Only a single MI identity expected here, found " + xrefs.size() );
        }

        return xrefs.iterator().next();
    }
}
