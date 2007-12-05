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
package uk.ac.ebi.intact.core.persister.stats;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;

import java.io.Serializable;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
* @version $Id$
*/
public class StatUnit implements Serializable {

    private String shortLabel;
    private String ac;
    private Class<? extends AnnotatedObject> type;

    public StatUnit(AnnotatedObject ao) {
        this.shortLabel = ao.getShortLabel();
        this.ac = ao.getAc();
        this.type = CgLibUtil.removeCglibEnhanced(ao.getClass());
    }

    public String getAc() {
        return ac;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public Class<? extends AnnotatedObject> getType() {
        return type;
    }
}