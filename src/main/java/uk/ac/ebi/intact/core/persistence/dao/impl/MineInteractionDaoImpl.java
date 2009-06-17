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
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.model.MineInteraction;
import uk.ac.ebi.intact.core.persistence.dao.MineInteractionDao;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Repository
@Transactional
@SuppressWarnings( "unchecked" )
public class MineInteractionDaoImpl extends HibernateBaseDaoImpl<MineInteraction>
        implements MineInteractionDao {

    public MineInteractionDaoImpl() {
        super(MineInteraction.class);
    }

    public MineInteractionDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( MineInteraction.class, entityManager, intactSession );
    }

    public void persist( MineInteraction mineInteraction ) {
        getSession().persist( mineInteraction );
    }

    public int deleteAll() {
        return getEntityManager().createQuery( "DELETE from MineInteraction" ).executeUpdate();
    }

    public MineInteraction get( String proteinIntactAc1, String proteinIntactAc2 ) {
        return ( MineInteraction ) getSession().createCriteria( getEntityClass() )
                .add( Restrictions.or(
                        Restrictions.and(
                                Restrictions.eq( "protein1Ac", proteinIntactAc1 ),
                                Restrictions.eq( "protein2Ac", proteinIntactAc2 )
                        ),
                        Restrictions.and(
                                Restrictions.eq( "protein2Ac", proteinIntactAc1 ),
                                Restrictions.eq( "protein1Ac", proteinIntactAc2 )
                        )
                ) ).uniqueResult();
    }

    public int countAll() {
        return ( Integer ) getSession().createCriteria( getEntityClass() )
                .setProjection( Projections.rowCount() ).uniqueResult();
    }

    public int countByProteinIntactAc( String proteinIntactAc ) {
        return ( Integer ) getSession().createCriteria( getEntityClass() )
                .add( Restrictions.or(
                        Restrictions.eq( "protein1Ac", proteinIntactAc ),
                        Restrictions.eq( "protein2Ac", proteinIntactAc ) ) )
                .setProjection( Projections.rowCount() ).uniqueResult();
    }

    public List<MineInteraction> getByProteinIntactAc( String proteinIntactAc, Integer firstResult, Integer maxResults ) {
        Criteria crit = getSession().createCriteria( getEntityClass() )
                .add( Restrictions.or(
                        Restrictions.eq( "protein1Ac", proteinIntactAc ),
                        Restrictions.eq( "protein2Ac", proteinIntactAc ) ) );

        if ( firstResult != null && firstResult > 0 ) {
            crit.setFirstResult( firstResult );
        }
        if ( maxResults != null && maxResults > 0 ) {
            crit.setMaxResults( maxResults );
        }

        return crit.list();
    }
}
