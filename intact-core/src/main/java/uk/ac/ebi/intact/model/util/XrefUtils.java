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

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.ClassUtils;

/**
 * Utils with xrefs
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class XrefUtils {

    public static <X extends Xref> X createIdentityXrefPsiMi(AnnotatedObject<X,?> parent, String primaryId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        return createIdentityXref(parent, primaryId, builder.createIdentityCvXrefQualifier(parent.getOwner()), builder.createPsiMiCvDatabase(parent.getOwner()));
    }

    public static <X extends Xref> X createIdentityXrefUniprot(AnnotatedObject<X,?> parent, String primaryId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(parent.getOwner(), CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);

        return createIdentityXref(parent, primaryId, builder.createIdentityCvXrefQualifier(parent.getOwner()), cvDatabase);
    }

    public static <X extends Xref> X createIdentityXref(AnnotatedObject<X,?> parent, String primaryId, CvXrefQualifier identityQual, CvDatabase cvDatabase) {
        X xref = (X) newXrefInstanceFor(parent.getClass());
        xref.setOwner(parent.getOwner());
        xref.setCvDatabase(cvDatabase);
        xref.setCvXrefQualifier(identityQual);
        xref.setPrimaryId(primaryId);
        xref.setParent(parent);

        return xref;
    }

    public static <X extends Xref> X newXrefInstanceFor(Class<? extends AnnotatedObject> aoClass) {
        Class<X> xrefClass = (Class<X>) AnnotatedObjectUtils.getXrefClassType(aoClass);
        return ClassUtils.newInstance(xrefClass);
    }
}