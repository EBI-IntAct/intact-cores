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

import uk.ac.ebi.intact.model.*;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @since 1.6.1
 */
public class ExperimentUtils {

    /**
     * Gets the pubmed ID for an Experiment - whitout checking database
     * @param experiment the experiment to get the pubmed id from
     * @return the pubmed id
     */
    public static String getPubmedId(Experiment experiment) {
        String pubmedId = null;

        Publication publication = experiment.getPublication();
        if (publication != null) {
            pubmedId = publication.getShortLabel();
        }

        if (pubmedId == null) {
            for (ExperimentXref xref : experiment.getXrefs()) {
                CvObjectXref idQualXref = CvObjectUtils.getPsiMiIdentityXref(xref.getCvXrefQualifier());
                CvObjectXref idCvDatabase = CvObjectUtils.getPsiMiIdentityXref(xref.getCvDatabase());

                if (idQualXref.getPrimaryId().equals(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF) &&
                        idCvDatabase.getPrimaryId().equals(CvDatabase.PUBMED_MI_REF)) {
                    pubmedId = xref.getPrimaryId();
                }
            }
        }

        return pubmedId;
    }
}