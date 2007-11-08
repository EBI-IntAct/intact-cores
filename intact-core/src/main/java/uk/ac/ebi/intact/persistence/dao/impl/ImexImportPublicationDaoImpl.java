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

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.meta.ImexImportPublication;
import uk.ac.ebi.intact.model.meta.ImexImportPublicationStatus;
import uk.ac.ebi.intact.persistence.dao.ImexImportPublicationDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexImportPublicationDaoImpl extends HibernateBaseDaoImpl<ImexImportPublication> implements ImexImportPublicationDao {

    public ImexImportPublicationDaoImpl(EntityManager entityManager, IntactSession intactSession) {
        super(ImexImportPublication.class, entityManager, intactSession);
    }

    /**
     * Get the entries that have failed and do not have ok status in an ulterior import
     * @return
     */
    public List<ImexImportPublication> getFailed() {
        Query query = getEntityManager().createQuery("from uk.ac.ebi.intact.model.meta.ImexImportPublication " +
                                                     "where status = :failedStatus and pmid not in (" +
                                                     "select pmid from uk.ac.ebi.intact.model.meta.ImexImportPublication " +
                                                     "where status = :okStatus)");
        query.setParameter("failedStatus", ImexImportPublicationStatus.ERROR);
        query.setParameter("okStatus", ImexImportPublicationStatus.OK);

        return query.getResultList();
    }

    public List<ImexImportPublication> getByPmid(String pmid) {
        Query query = getEntityManager().createQuery("from uk.ac.ebi.intact.model.meta.ImexImportPublication " +
                                                     "where pmid = :pmid");
        query.setParameter("pmid", pmid);

        return query.getResultList();
    }
}