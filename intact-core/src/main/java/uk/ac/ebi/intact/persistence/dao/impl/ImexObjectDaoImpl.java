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

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.meta.ImexObject;
import uk.ac.ebi.intact.model.meta.ImexObjectStatus;
import uk.ac.ebi.intact.persistence.dao.ImexObjectDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexObjectDaoImpl extends HibernateBaseDaoImpl<ImexObject> implements ImexObjectDao {

    public ImexObjectDaoImpl(Session session, IntactSession intactSession) {
        super(ImexObject.class, session, intactSession);
    }

    public void persist(ImexObject imexObject) {
        getSession().persist(imexObject);
    }

    public int countAll() {
        return ( Integer ) getSession()
                .createCriteria( getEntityClass() )
                .setProjection( Projections.rowCount() )
                .uniqueResult();
    }

    public List<ImexObject> getFailed() {
        return getSession().createCriteria(getEntityClass())
                .add(Restrictions.eq("status", ImexObjectStatus.ERROR)).list();
    }
}