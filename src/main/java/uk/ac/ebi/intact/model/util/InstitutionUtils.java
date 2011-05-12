/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.InstitutionXref;

/**
 * Utilities for the Interaction object
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.4
 */
public final class InstitutionUtils {

    private InstitutionUtils() {
    }

    /**
     * Fetches the corresponding CvDatabase for a specific institution.
     *
     * @param context     The IntactContext to run the queries to the database
     * @param institution The institution to find a CvDatabase for
     * @return
     */
    public static CvDatabase retrieveCvDatabase(IntactContext context, Institution institution) {
        return retrieveCvDatabase(context, institution, null);
    }

    /**
     * Fetches the corresponding CvDatabase for a specific institution.
     *
     * @param context                   The IntactContext to run the queries to the database
     * @param institution               The institution to find a CvDatabase for
     * @param defaultDatabaseIdentifier If the passed institution does not have a PSI-MI identifier, the default identifier to use.
     * @return
     */
    public static CvDatabase retrieveCvDatabase(IntactContext context, Institution institution, String defaultDatabaseIdentifier) {
        InstitutionXref identityXref = XrefUtils.getPsiMiIdentityXref(institution);

        if (identityXref == null) return null;

        CvDatabase cvDatabase = context.getDaoFactory().getCvObjectDao(CvDatabase.class).getByIdentifier(identityXref.getPrimaryId());

        if (cvDatabase == null && defaultDatabaseIdentifier != null) {
            cvDatabase = context.getDaoFactory().getCvObjectDao(CvDatabase.class).getByIdentifier(defaultDatabaseIdentifier);
        }

        return cvDatabase;
    }

}
