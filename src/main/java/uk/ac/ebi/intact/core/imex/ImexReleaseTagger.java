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
import uk.ac.ebi.intact.model.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexReleaseTagger {

    private IntactContext intactContext;

    public ImexReleaseTagger(IntactContext intactContext) {
        this.intactContext = intactContext;
    }

    public void tag(Publication publication) {
        tag(publication, new DateTime());
    }

    public void tag(Publication publication, DateTime dateTime) {
        tag((ImexRelevant)publication, dateTime);
    }

    public void tag(Experiment experiment) {
        tag(experiment, new DateTime());
    }

    public void tag(Experiment experiment, DateTime dateTime) {
        tag((ImexRelevant)experiment, dateTime);
    }

    public void tag(Interaction interaction) {
        tag(interaction, new DateTime());
    }

    public void tag(Interaction interaction, DateTime dateTime) {
        tag((ImexRelevant)interaction, dateTime);
    }

    protected void tag(ImexRelevant imexRelevantObject, DateTime dateTime) {
        imexRelevantObject.setLastImexUpdate( dateTime.toDate() );
    }
}
