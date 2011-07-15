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
package uk.ac.ebi.intact.core.config.property;

import uk.ac.ebi.intact.core.annotations.PersistentPropertyConverter;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.Institution;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@PersistentPropertyConverter
public class InstitutionPropertyConverter implements PropertyConverter<Institution> {

    public InstitutionPropertyConverter() {
    }

    @Override
    public Institution convertFromString(String str) {
        if (str == null) return null;

        return IntactContext.getCurrentInstance().getDaoFactory().getInstitutionDao().getByShortLabel(str);
    }

    @Override
    public String convertToString(Institution obj) {
        if (obj == null) return null;

        return obj.getShortLabel();
    }

    @Override
    public Class<Institution> getObjectType() {
        return Institution.class;
    }
}
