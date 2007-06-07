/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;

/**
 * The factory to create various polymer types.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class PolymerFactory {

    private PolymerFactory() {
    }

    /**
     * Creats an instance of Polymer type based on type.
     *
     * @param owner      The Institution which owns this instance
     * @param source     The biological source of the Protein observation
     * @param shortLabel The memorable label to identify this instance
     * @param type       The interactor type. This alone decides which type to
     *                   create
     *
     * @return an instance of <code>Polymer</code> based on <code>type</code> or
     *         null if a Polymer cannot be instantiated (for exampple, no MI found in given
     *         type)
     */
    public static Polymer factory( Institution owner, BioSource source,
                                   String shortLabel, CvInteractorType type
    ) {
        Xref xref = getMIXref( type );
        if ( xref == null ) {
            // This should only happen if we have a CV without an MI number - big NO
            return null;
        }
        // The Polymer to return
        Polymer polymer = null;

        // The MI number
        String mi = xref.getPrimaryId();

        if ( CvInteractorType.isProteinMI( mi ) ) {
            polymer = new ProteinImpl( owner, source, shortLabel, type );
        } else if ( CvInteractorType.isNucleicAcidMI( mi ) ) {
            polymer = new NucleicAcidImpl( owner, source, shortLabel, type );
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
        for ( Xref xref : cvobj.getXrefs() ) {
            if ( xref.getPrimaryId().startsWith( "MI:" ) ) {
                return xref;
            }
        }
        // No primary id found
        return null;
    }
}
