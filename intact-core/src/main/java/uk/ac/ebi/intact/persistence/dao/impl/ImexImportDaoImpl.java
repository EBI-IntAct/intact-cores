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
package uk.ac.ebi.intact.persistence.dao.impl;

import org.joda.time.DateTime;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.meta.ImexImport;
import uk.ac.ebi.intact.model.meta.ImexImportActivationType;
import uk.ac.ebi.intact.persistence.dao.ImexImportDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexImportDaoImpl extends HibernateBaseDaoImpl<ImexImport> implements ImexImportDao {

    public ImexImportDaoImpl(EntityManager entityManager, IntactSession intactSession) {
        super(ImexImport.class, entityManager, intactSession);
    }


    public DateTime getLatestUpdate(ImexImportActivationType activationType) {
        final Query query = getEntityManager()
                .createQuery("select max(importDate) from uk.ac.ebi.intact.model.meta.ImexImport " +
                             "where activationType = :activationType");
        query.setParameter("activationType", activationType);
        
        List<DateTime> dateTimes = query.getResultList();

        if (dateTimes.isEmpty()) {
            return null;
        }

        return dateTimes.iterator().next();
    }
}