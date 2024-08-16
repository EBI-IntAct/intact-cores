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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.ConfidenceDao;
import uk.ac.ebi.intact.model.Confidence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 *        <pre>
 *        05-Dec-2007
 *        </pre>
 */
@Repository
@Transactional(readOnly = true)
public class ConfidenceDaoImpl  extends IntactObjectDaoImpl<Confidence> implements ConfidenceDao {

    public ConfidenceDaoImpl() {
        super(Confidence.class);
    }

      public ConfidenceDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Confidence.class, entityManager, intactSession );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Confidence> getByInteractionAc( String interactionAc ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "interaction" )
                .add( Restrictions.idEq(interactionAc) ).list();
    }
}
