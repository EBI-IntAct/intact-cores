/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;

/**
 * TODO comment this
*
* @author Bruno Aranda (baranda@ebi.ac.uk)
* @version $Id$
*/
public class CvObjectBuilder {

    private CvDatabase cvDatabase;
    private CvXrefQualifier cvXrefQualifier;

    public CvDatabase createPsiMiCvDatabase(IntactContext intactContext) {
        if (cvDatabase != null) {
            return cvDatabase;
        }

        cvDatabase = new CvDatabase(intactContext.getInstitution(), CvDatabase.PSI_MI);

        CvObjectXref xref = createPsiMiXref(CvObjectXref.class, CvDatabase.PSI_MI_MI_REF, intactContext);
        cvDatabase.addXref(xref);

        return cvDatabase;
    }

    public CvXrefQualifier createIdentityCvXrefQualifier(IntactContext intactContext) {
        if (cvXrefQualifier != null) {
            return cvXrefQualifier;
        }

        cvXrefQualifier = new CvXrefQualifier(intactContext.getInstitution(), CvXrefQualifier.IDENTITY);

        CvObjectXref xref = createPsiMiXref(CvObjectXref.class, CvXrefQualifier.IDENTITY_MI_REF, intactContext);
        cvXrefQualifier.addXref(xref);

        return cvXrefQualifier;
    }

    private <X extends Xref> X createPsiMiXref(Class<X> xrefClass, String psiMi, IntactContext intactContext) {
        if (xrefClass == null) {
            throw new NullPointerException("xrefClass");
        }

        X xref;
        try {
            xref = xrefClass.newInstance();
        } catch (Exception e) {
            throw new IntactException("Problems instantiating Xref of type: " + xrefClass.getName());
        }
        xref.setOwner(intactContext.getInstitution());
        xref.setPrimaryId(psiMi);
        xref.setCvDatabase(createPsiMiCvDatabase(intactContext));
        xref.setCvXrefQualifier(createIdentityCvXrefQualifier(intactContext));

        return xref;
    }

}