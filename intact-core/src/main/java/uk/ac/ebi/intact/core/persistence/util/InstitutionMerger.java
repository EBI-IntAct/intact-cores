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
package uk.ac.ebi.intact.core.persistence.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.OwnedAnnotatedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Merges institutions from the database, optionally removing the ones that are not needed.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class InstitutionMerger {

    /**
     * Merges the institutions, updating the records in the database using the merged institutions.
     *
     * @param sourceInstitutions     The institutions that will be merged. It is not necessary to include the destinationInstitution
     * @param destinationInstitution The destination institution, which is going to be used by all the objects with the merged institutions
     * @param removeMerged           Whether to remove the merged institutions or not.
     * @return number of updated records
     */
    @Transactional
    public int merge(Institution[] sourceInstitutions, Institution destinationInstitution, boolean removeMerged) {
        int updated = 0;

        IntactContext intactContext = IntactContext.getCurrentInstance();

        Map<String, AnnotatedObjectDao> annotatedObjectDaoMap = intactContext.getSpringContext().getBeansOfType(AnnotatedObjectDao.class);

        for (AnnotatedObjectDao annotatedObjectDao : annotatedObjectDaoMap.values()) {

            if (OwnedAnnotatedObject.class.isAssignableFrom(annotatedObjectDao.getEntityClass())) {

                for (Institution sourceInstitution : sourceInstitutions) {

                    // skip the copy if the destinationInstitution is as well part of the sources
                    if (!sourceInstitution.equals(destinationInstitution)) {
                        updated += annotatedObjectDao.replaceInstitution(sourceInstitution, destinationInstitution);
                    }
                }

            }
        }

        if (removeMerged) {
            for (Institution sourceInstitution : sourceInstitutions) {
                if (!sourceInstitution.equals(destinationInstitution)) {
                    intactContext.getDaoFactory().getInstitutionDao().deleteByAc(sourceInstitution.getAc());
                }
            }
        }

        return updated;
    }
}
