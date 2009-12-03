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
package uk.ac.ebi.intact.core.imex;

import org.joda.time.DateTime;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.meta.ImexExportRelease;

import java.util.Date;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexExportManager {

    protected ImexExportManager() {
    }

    public static ImexExportManager getInstance() {
       return (ImexExportManager) IntactContext.getCurrentInstance().getSpringContext().getBean("imexExportManager");
    }

    public void prepareRelease() {
        DateTime fromDate = getLastReleaseDate();
        prepareRelease(fromDate, new DateTime());
    }

    public void prepareRelease(DateTime fromDate, DateTime toDate) {
        if (fromDate == null) throw new NullPointerException("fromDate cannot be null");
        if (toDate == null) throw new NullPointerException("toDate cannot be null");

        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("toDate ["+toDate+"] cannot be before fromDate ["+fromDate+"]");
        }

        // TODO check that there are no releases in preparation, synchronize

        final ImexExportRelease release = new ImexExportRelease();

        // TODO add some kind of status "IN PROGRESS" for the release?
        getDaoFactory().getImexExportInteractionDao().persist(release);

        releasePublications(release, fromDate.toDate(), toDate.toDate());
        releaseExperiments(release, fromDate.toDate(), toDate.toDate());
        releaseInteractions(release, fromDate.toDate(), toDate.toDate());

        // TODO change the release status to ERROR (if exceptions) or OK
    }

    protected void releasePublications(ImexExportRelease release, Date fromDate, Date toDate) {
        // fetch the publications with the last-imex-update between these dates
        List<Publication> publications = getDaoFactory().getPublicationDao().getByLastImexUpdate(fromDate, toDate);

        // iterate through the publications

            // for each, check presence of imexId. If not, throw exception. Option 2 fetch IDs using generator

            // iterate through its interactions, using jpa pages instead of iterating through collections of entities

                    // for each, add missing imexId (checking and manipulating the last-interaction-imex-id)

                    // create the ImexExportInteraction and persist, link it to the release and persist it
    }

    protected void releaseExperiments(ImexExportRelease release, Date fromDate, Date toDate) {
    }

    protected void releaseInteractions(ImexExportRelease release, Date fromDate, Date toDate) {
    }

    private DateTime getLastReleaseDate() {
        DateTime fromDate;

        ImexExportRelease lastRelease = getDaoFactory().getImexExportReleaseDao().getLastRelease();

        if (lastRelease != null) {
            fromDate = new DateTime(lastRelease.getCreated());
        } else {
            fromDate = new DateTime("1900-01-01");
        }
        return fromDate;
    }

    private DaoFactory getDaoFactory() {
        return IntactContext.getCurrentInstance().getDaoFactory();
    }


}
