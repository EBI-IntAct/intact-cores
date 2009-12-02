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

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexExportManager {

    public ImexExportManager() {
    }

    public void prepareRelease() {
        DateTime fromDate = null; // TODO get last ImexExportRelease date
        prepareRelease(fromDate, new DateTime());
    }

    public void prepareRelease(DateTime fromDate, DateTime toDate) {
        // do this process in an iteration to avoid memory problems
    }
}
