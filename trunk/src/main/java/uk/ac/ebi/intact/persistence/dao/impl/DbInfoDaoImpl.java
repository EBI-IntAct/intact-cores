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
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.meta.DbInfo;
import uk.ac.ebi.intact.persistence.dao.DbInfoDao;

import java.util.List;

/**
 * Dao for DbInfo
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Sep-2006</pre>
 */
@SuppressWarnings( "unchecked" )
public class DbInfoDaoImpl extends HibernateBaseDaoImpl<DbInfo> implements DbInfoDao {

    private static final Log log = LogFactory.getLog( DbInfoDaoImpl.class );

    public DbInfoDaoImpl( Session session, IntactSession intactSession ) {
        super( DbInfo.class, session, intactSession );
    }

    public DbInfo get( String key ) {
        return ( DbInfo ) getSession().get( DbInfo.class, key );
    }

    public List<DbInfo> getAll() {
        return getSession().createCriteria( DbInfo.class ).list();
    }

    public void persist( DbInfo dbInfo ) {
        getSession().persist( dbInfo );
    }
}
