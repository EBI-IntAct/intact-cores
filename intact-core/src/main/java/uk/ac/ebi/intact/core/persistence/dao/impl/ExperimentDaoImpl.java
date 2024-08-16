/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.DaoUtils;
import uk.ac.ebi.intact.core.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.Order;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of the ExperimentDao.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>26-Apr-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class ExperimentDaoImpl extends AnnotatedObjectDaoImpl<Experiment> implements ExperimentDao {

    public ExperimentDaoImpl( ) {
        super( Experiment.class );
    }

    public ExperimentDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Experiment.class, entityManager, intactSession );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Integer countInteractionsForExperimentWithAc( String ac ) {
        final Long count = (Long) getSession().createCriteria(InteractionImpl.class)
                .createAlias("experiments", "exp")
                .add(Restrictions.eq("exp.ac", ac))
                .setProjection(Projections.rowCount()).uniqueResult();
        return count.intValue();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Interaction> getInteractionsForExperimentWithAc( String ac, int firstResult, int maxResults ) {
        return getSession().createCriteria( InteractionImpl.class )
                .addOrder(org.hibernate.criterion.Order.asc("ac"))
                .setFirstResult(firstResult)
                .setMaxResults( maxResults )
                .createCriteria( "experiments" )
                .add( Restrictions.idEq( ac ) ).list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Iterator<Interaction> getInteractionsForExperimentWithAcIterator( String ac ) {
        Query query = getSession().createQuery("from InteractionImpl as interaction left join interaction.experiments as exp where exp.ac = :ac");
        query.setParameter("ac", ac);
        return query.iterate();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Interaction> getInteractionsForExperimentWithAcExcluding( String ac, String[] excludedAcs, int firstResult, int maxResults ) {
        Criteria crit = getSession().createCriteria( InteractionImpl.class )
                .addOrder(org.hibernate.criterion.Order.asc("ac"))
                .setFirstResult( firstResult )
                .setMaxResults( maxResults );

        for ( String excludedAc : excludedAcs ) {
            crit.add( Restrictions.ne( "ac", excludedAc ) );
        }

        crit.createCriteria( "experiments" )
                .add( Restrictions.idEq( ac ) );

        return crit.list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Interaction> getInteractionsForExperimentWithAcExcludingLike( String ac, String[] excludedAcsLike, int firstResult, int maxResults ) {
        Criteria crit = getSession().createCriteria( InteractionImpl.class )
                .addOrder(org.hibernate.criterion.Order.asc("ac"))
                .setFirstResult( firstResult )
                .setMaxResults( maxResults );

        for ( String excludedAc : excludedAcsLike ) {
            excludedAc = DaoUtils.replaceWildcardsByPercent( excludedAc );
            crit.add( Restrictions.not( Restrictions.like( "ac", excludedAc ) ) );
        }

        crit.createCriteria( "experiments" )
                .add( Restrictions.idEq( ac ) );

        return crit.list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Experiment> getByPubId(String pubId) {
        Query query = getSession()
                .createQuery("select distinct exp from Experiment exp " +
                             "left join exp.publication as pub " +
                             "join exp.xrefs as xref  where pub = :pubId or xref.primaryId = :pubId");
        query.setString("pubId", pubId);

        return query.list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Experiment> getByPubAc(String pubAc) {
        Query query = getSession()
                .createQuery("select distinct exp from Experiment exp " +
                             "left join exp.publication as pub " +
                             " where pub.ac = :pubAc");
        query.setString("pubAc", pubAc);

        return query.list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Experiment> getByPubIdAndLabelLike(String pubId, String labelLike) {
        Query query = getSession()
                .createQuery("select distinct exp from Experiment exp " +
                             "left join exp.publication as pub " +
                             "join exp.xrefs as xref  where pub = :pubId or xref.primaryId = :pubId " +
                             "and exp.shortLabel like :label");
        query.setString("pubId", pubId);
        query.setString("label", labelLike);

        return query.list();
    }

    /**
     * @inheritDoc
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Experiment> getByLastImexUpdate( Date fromDate, Date toDate) {
        if ( fromDate == null ) {
            throw new IllegalArgumentException( "You must give a non null fromDate" );
        }
        if ( toDate == null ) {
            throw new IllegalArgumentException( "You must give a non null toDate" );
        }
        if( toDate.before(fromDate ) ) {
            throw new IllegalArgumentException( "Invalid date range, toDate is before fromDate." );
        }

        javax.persistence.Query query = getEntityManager().createQuery("select e " +
                                                                       "from Experiment e " +
                                                                       "where     e.lastImexUpdate >= :fromDate " +
                                                                       "      and e.lastImexUpdate <= :toDate");
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        return query.getResultList();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Experiment> getByHostOrganism(String biosourceAc){

        return getSession().createCriteria(getEntityClass())
                .createCriteria("bioSource")
                .add(Restrictions.idEq(biosourceAc)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

        /**
     * @InheritDoc
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Experiment> getByInteractionAc( String interactionAc) {
        javax.persistence.Query query = getEntityManager().createQuery("select e " +
                                                     "from Experiment e join e.interactions i " +
                                                     "where i.ac = :interactionAc " +
                                                     "order by e.created");
        query.setParameter("interactionAc", interactionAc);

        return query.getResultList();
    }
}
