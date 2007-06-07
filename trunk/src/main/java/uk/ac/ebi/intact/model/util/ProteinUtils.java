/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import java.util.Iterator;
import java.util.Collection;

/**
 * Utility methods for Proteins
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinUtils {

    /**
     * Checks if the protein has been annotated with the no-uniprot-update CvTopic, if so, return false, otherwise true.
     * That flag is added to a protein when created via the editor. As some protein may have a UniProt ID as identity we
     * don't want those to be overwitten.
     *
     * @param protein the protein to check
     *
     * @return true if uniprot
     */
    public static boolean isFromUniprot( Protein protein ) {
        boolean isFromUniprot = true;

        CvTopic noUniprotUpdate = IntactContext.getCurrentInstance().getCvContext().getByLabel( CvTopic.class, CvTopic.NON_UNIPROT );

        if ( null == noUniprotUpdate ) {
            // in case the term hasn't been created, assume there are no proteins created via editor.
            return true;
        }

        for ( Iterator iterator = protein.getAnnotations().iterator(); iterator.hasNext() && isFromUniprot; ) {
            Annotation annotation = ( Annotation ) iterator.next();

            if ( noUniprotUpdate.getAc().equals( annotation.getCvTopic().getAc() ) ) {
                isFromUniprot = false;
            }
        }

        return isFromUniprot;
    }

    /**
     * Return the xref of the protein having as cvQualifier, the CvQualifier with psi-mi equal to
     * CvXrefQualifier.IDENTITY_MI_REF and as cvDatabase, the CvDatabase with psi-mi equal to CvDatabase.UNIPROT_MI_REF
     * and returns it. Return null otherwise.
     * @param protein a non null Protein object.
     * @return the uniprotkb identity xref if the protein has one, null otherwise.
     */

    public static InteractorXref getUniprotXref(Protein protein){
        if(protein == null){
            throw new NullPointerException("Protein is null, shouldn't be null");
        }
        Collection<InteractorXref> xrefs = protein.getXrefs();
        for(InteractorXref xref : xrefs){
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            if(qualifier != null){
                CvObjectXref qualifierIdentityXref = CvObjectUtils.getPsiMiIdentityXref(qualifier);
                if(qualifierIdentityXref!= null && CvXrefQualifier.IDENTITY_MI_REF.equals(qualifierIdentityXref.getPrimaryId())){
                    CvDatabase database = xref.getCvDatabase();
                    CvObjectXref databaseIdentityXref = CvObjectUtils.getPsiMiIdentityXref(database);
                    if(databaseIdentityXref != null && CvDatabase.UNIPROT_MI_REF.equals(databaseIdentityXref.getPrimaryId())){
                        return xref;
                    }
                }
            }
        }
        return null;
    }
}
