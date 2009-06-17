/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * Utility class for small molecules
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SmallMoleculeUtils {


    /**
     * Return the xref of the smallMolecule having as cvQualifier, the CvQualifier with psi-mi equal to
     * CvXrefQualifier.IDENTITY_MI_REF and as cvDatabase, the CvDatabase with psi-mi equal to CvDatabase.CHEBI_MI_REF
     * and returns it. Return null otherwise.
     *
     * @param smallMolecule a non null smallMolecule object.
     * @return the smallMolecule identity xref if the smallMolecule has one, null otherwise.
     */
    public static InteractorXref getChebiXref( Interactor smallMolecule ) {
        if ( smallMolecule == null ) {
            throw new NullPointerException( "You must give a non null smallMolecule" );
        }

        Collection<InteractorXref> xrefs = smallMolecule.getXrefs();
        for ( InteractorXref xref : xrefs ) {
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            if ( qualifier != null ) {
                String qualifierIdentity = qualifier.getIdentifier();
                if ( qualifierIdentity != null && CvXrefQualifier.IDENTITY_MI_REF.equals( qualifierIdentity ) ) {
                    CvDatabase database = xref.getCvDatabase();
                    String databaseIdentity = database.getIdentifier();
                    if ( databaseIdentity != null && CvDatabase.CHEBI_MI_REF.equals( databaseIdentity ) ) {
                        return xref;
                    }
                }
            }
        }
        return null;
    }


    public static InteractorXref getChebiXref( SmallMolecule smallMolecule ) {
        return getChebiXref((Interactor) smallMolecule );
    }


}
