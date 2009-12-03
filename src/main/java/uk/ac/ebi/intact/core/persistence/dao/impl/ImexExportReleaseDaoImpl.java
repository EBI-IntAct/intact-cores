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
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.UserContext;
import uk.ac.ebi.intact.core.persistence.dao.ImexExportReleaseDao;
import uk.ac.ebi.intact.model.meta.ImexExportInteraction;
import uk.ac.ebi.intact.model.meta.ImexExportRelease;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Repository
@Transactional(readOnly = true)
public class ImexExportReleaseDaoImpl extends HibernateBaseDaoImpl implements ImexExportReleaseDao {

    @PersistenceContext(unitName = "intact-core-default")
    private EntityManager entityManager;

    @Autowired
    private UserContext userContext;

    public ImexExportReleaseDaoImpl() {
        super(ImexExportRelease.class);
    }

    public List<ImexExportInteraction> getUpdatedAfter(Date date) {
        Query query = entityManager.createQuery("select ie from uk.ac.ebi.intact.model.meta.ImexExportRelease ie " +
                "where ie.updated > :date and ie.created <= :date");
        query.setParameter("date", date, TemporalType.TIMESTAMP);
        return query.getResultList();
    }

    public List<ImexExportInteraction> getCreatedAfter(Date date) {
        Query query = entityManager.createQuery("select ie from uk.ac.ebi.intact.model.meta.ImexExportRelease ie " +
                "where ie.created > :date");
        query.setParameter("date", date, TemporalType.TIMESTAMP);
        return query.getResultList();
    }
}