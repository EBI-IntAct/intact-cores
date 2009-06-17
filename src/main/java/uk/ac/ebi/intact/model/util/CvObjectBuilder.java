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

import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;

/**
 * Controlled vocabulary builder.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectBuilder {

    private CvDatabase cvDatabase;
    private CvXrefQualifier cvXrefQualifier;

    @Deprecated
    public CvDatabase createPsiMiCvDatabase(IntactContext intactContext) {
        return createPsiMiCvDatabase(intactContext.getInstitution());
    }

    public CvDatabase createPsiMiCvDatabase(Institution institution) {
        if (cvDatabase != null) {
            return cvDatabase;
        }

        cvDatabase = new CvDatabase(institution, CvDatabase.PSI_MI);
        cvDatabase.setIdentifier(CvDatabase.PSI_MI_MI_REF);

        CvObjectXref xref = createPsiMiXref(CvObjectXref.class, CvDatabase.PSI_MI_MI_REF, institution);
        cvDatabase.addXref(xref);

        return cvDatabase;
    }

    @Deprecated
    public CvXrefQualifier createIdentityCvXrefQualifier(IntactContext intactContext) {
        return createIdentityCvXrefQualifier(intactContext.getInstitution());
    }

    public CvXrefQualifier createIdentityCvXrefQualifier(Institution institution) {
        if (cvXrefQualifier != null) {
            return cvXrefQualifier;
        }

        cvXrefQualifier = new CvXrefQualifier(institution, CvXrefQualifier.IDENTITY);
        cvXrefQualifier.setIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        CvObjectXref xref = createPsiMiXref(CvObjectXref.class, CvXrefQualifier.IDENTITY_MI_REF, institution);
        cvXrefQualifier.addXref(xref);

        return cvXrefQualifier;
    }

    private <X extends Xref> X createPsiMiXref(Class<X> xrefClass, String psiMi, Institution institution) {
        if (xrefClass == null) {
            throw new NullPointerException("xrefClass");
        }

        X xref;
        try {
            xref = xrefClass.newInstance();
        } catch (Exception e) {
            throw new IntactException("Problems instantiating Xref of type: " + xrefClass.getName());
        }
        xref.setOwner(institution);
        xref.setPrimaryId(psiMi);
        xref.setCvDatabase(createPsiMiCvDatabase(institution));
        xref.setCvXrefQualifier(createIdentityCvXrefQualifier(institution));

        return xref;
    }
}