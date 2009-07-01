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
package uk.ac.ebi.intact.config;

import org.hibernate.ejb.Ejb3Configuration;
import uk.ac.ebi.intact.config.hibernate.SequenceAuxiliaryDatabaseObject;

/**
 * Configures the persistence engine, adding additional objects needed by hibernate (e.g. cv_local_id sequence, etc...).
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @since 1.9.0
 */
public class IntactAuxiliaryConfigurator {

    public static final String CV_LOCAL_SEQ = "cv_local_seq";

    private IntactAuxiliaryConfigurator() {}

    public static void configure(Ejb3Configuration configuration) {
        configuration.addAuxiliaryDatabaseObject(new SequenceAuxiliaryDatabaseObject(CV_LOCAL_SEQ, 1));
    }

}
