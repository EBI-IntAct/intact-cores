/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.ComponentParameterDao;
import uk.ac.ebi.intact.model.ComponentParameter;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Data Access Object for Interaction parameter.
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since 1.9.0
 */
@Repository
@Transactional(readOnly = true)
public class ComponentParameterDaoImpl extends IntactObjectDaoImpl<ComponentParameter> implements ComponentParameterDao {

    public ComponentParameterDaoImpl() {
        super(ComponentParameter.class);
    }

    public ComponentParameterDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( ComponentParameter.class, entityManager, intactSession );
    }

    public List<ComponentParameter> getByComponentAc( String componentAc ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "component" )
                .add( Restrictions.idEq(componentAc) ).list();
    }
}
