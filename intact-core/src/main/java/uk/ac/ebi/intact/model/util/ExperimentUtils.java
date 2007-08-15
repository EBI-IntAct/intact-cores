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

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;

import java.util.List;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @since 1.6.1
 */
public class ExperimentUtils {

    private static final String SYNCED_LABEL_PATTERN = "\\w+-\\d{4}-\\d+";
    private static final String NOT_SYNCED_LABEL_PATTERN = "\\w+-\\d{4}";

    /**
     * Gets the pubmed ID for an Experiment - whitout hitting the database
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

    /**
     * Syncs a short label with the database, checking that there are no duplicates and that the correct suffix is added.
     *
     * Concurrency note: just after getting the new short label, it is recommended to persist/update the interaction immediately
     * in the database - so this method should ONLY be used before saving the interaction to the database. In some
     * race conditions, two interactions could be created with the same id; currently there is no way to
     * reserve a short label
     *
     * @param shortLabel the short label to sync
     * @return the synced short label
     *
     * @since 1.6.2
     */
    public static String syncShortLabelWithDb(String shortLabel) {
        String syncedLabel;

        if (matchesSyncedLabel(shortLabel)) {
            syncedLabel = shortLabel;
        } else  if (matchesMotSyncedLabel(shortLabel)) {
            ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
            List<String> expLabels = experimentDao.getShortLabelsLike(shortLabel+"-%");

            int maxSuffix = 0;

            for (String expLabel : expLabels) {
                String strSuffix = expLabel.substring(expLabel.lastIndexOf("-")+1, expLabel.length());

                int suffix = Integer.valueOf(strSuffix);

                maxSuffix = Math.max(maxSuffix, suffix);
            }

            syncedLabel = shortLabel+"-"+(maxSuffix+1);

        } else {
            throw new IllegalArgumentException("Short label with wrong format: "+shortLabel);
        }

        return syncedLabel;
    }

    /**
     * Returns true if the experiment label matches this regex: wwww-dddd-d+
     * @param experimentShortLabel the experiment short label to match
     * @return true if matched
     *
     * @since 1.6.2
     */
    public static boolean matchesSyncedLabel(String experimentShortLabel) {
        return experimentShortLabel.matches(SYNCED_LABEL_PATTERN);
    }

    /**
     * Returns true if the experiment label matches this regex: wwww-dddd
     * @param experimentShortLabel the experiment short label to match
     * @return true if matched
     *
     * @since 1.6.2
     */
    public static boolean matchesMotSyncedLabel(String experimentShortLabel) {
        return experimentShortLabel.matches(NOT_SYNCED_LABEL_PATTERN);
    }
}