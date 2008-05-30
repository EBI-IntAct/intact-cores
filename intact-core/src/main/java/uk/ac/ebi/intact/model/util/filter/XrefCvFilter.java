/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util.filter;

import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * Filter for Xrefs, based on the database and qualifier.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class XrefCvFilter implements IntactObjectFilter<Xref>{

    private CvObjectFilterGroup databaseFilterGroup;
    private CvObjectFilterGroup qualifierFilterGroup;

    public XrefCvFilter(CvObjectFilterGroup databaseFilterGroup) {
        this.databaseFilterGroup = databaseFilterGroup;
    }

    public XrefCvFilter(CvObjectFilterGroup databaseFilterGroup, CvObjectFilterGroup qualifierFilterGroup) {
        this(databaseFilterGroup);
        this.qualifierFilterGroup = qualifierFilterGroup;
    }

    public boolean accept(Xref xref) {
        String dbId = CvObjectUtils.getIdentity(xref.getCvDatabase());
        if (dbId == null) xref.getCvDatabase().getShortLabel();

        if (databaseFilterGroup.isAccepted(dbId)) {

            if (xref.getCvXrefQualifier() != null && qualifierFilterGroup != null) {
                String qualId = CvObjectUtils.getIdentity(xref.getCvXrefQualifier());
                if (qualId == null) xref.getCvXrefQualifier().getShortLabel();

                if (qualifierFilterGroup.isAccepted(qualId)) {
                    return true;
                }
            } else {
                return true;
            }

        }

        return false;
    }

}
