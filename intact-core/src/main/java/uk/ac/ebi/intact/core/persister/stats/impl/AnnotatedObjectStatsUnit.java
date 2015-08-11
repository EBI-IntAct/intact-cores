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
package uk.ac.ebi.intact.core.persister.stats.impl;

import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.core.persister.stats.StatsUnit;
import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
* @version $Id$
*/
public class AnnotatedObjectStatsUnit implements StatsUnit {

    private String shortLabel;
    private String ac;
    private Class<? extends AnnotatedObject> type;

    public AnnotatedObjectStatsUnit(AnnotatedObject ao) {
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

    @Override
    public String toString() {
        return getType().getSimpleName()+": "+getAc()+", "+getShortLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotatedObjectStatsUnit that = (AnnotatedObjectStatsUnit) o;

        if (ac != null ? !ac.equals(that.ac) : that.ac != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ac != null ? ac.hashCode() : 0;
    }
}