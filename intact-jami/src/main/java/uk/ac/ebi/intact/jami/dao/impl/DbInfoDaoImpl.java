/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.jami.dao.impl;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.DbInfoDao;
import uk.ac.ebi.intact.jami.model.meta.DbInfo;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Dao for DbInfo
 *
 */
public class DbInfoDaoImpl extends AbstractIntactBaseDao<DbInfo,DbInfo> implements DbInfoDao {

    public DbInfoDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(DbInfo.class, entityManager, context);
    }

    public DbInfo get( String key ) {
        return getEntityManager().find( DbInfo.class, key );
    }

    public List<DbInfo> getAll() {
        return getEntityManager().createQuery( "select d from DbInfo d" ).getResultList();
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getDbInfoSynchronizer();
    }



}
