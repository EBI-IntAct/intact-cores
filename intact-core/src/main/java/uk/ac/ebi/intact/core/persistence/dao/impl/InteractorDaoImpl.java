/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2006</pre>
 */
@Repository
@Qualifier("interactorDao")
@SuppressWarnings( "unchecked" )
@Transactional(readOnly = true)
public class InteractorDaoImpl<T extends InteractorImpl> extends AnnotatedObjectDaoImpl<T> implements InteractorDao<T> {

    private static final Log log = LogFactory.getLog( InteractorDaoImpl.class );

    /**
     * Filter to provide filtering on GeneNames
     */
    private static List<String> geneNameFilter = new ArrayList<String>();

    // nested implementation for providing the gene filter
    static {
        // TODO somehow find a way to use MI references that are stable
        geneNameFilter.add( "gene name" );
        geneNameFilter.add( "gene name-synonym" );
        geneNameFilter.add( "orf name" );
        geneNameFilter.add( "locus name" );
    }

    public InteractorDaoImpl() {
        super((Class<T>) InteractorImpl.class);
    }

    public InteractorDaoImpl( Class<T> entityClass ) {
        super( entityClass );
    }

    public InteractorDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager );
    }

    public InteractorDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Integer countInteractionsForInteractorWithAc( String ac ) {
        final Long count = (Long) getSession().createCriteria(Component.class)
                .createAlias("interactor", "interactor")
                .createAlias("interaction", "interaction")
                .add(Restrictions.eq("interactor.ac", ac))
                .setProjection(Projections.countDistinct("interaction.ac")).uniqueResult();
        return count.intValue();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Integer countComponentsForInteractorWithAc( String ac ) {
        final Long count = (Long) getSession().createCriteria(Component.class)
                .createAlias("interactor", "interactor")
                .add(Restrictions.eq("interactor.ac", ac))
                .setProjection(Projections.countDistinct("ac")).uniqueResult();
        return count.intValue();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<String> getGeneNamesByInteractorAc( String proteinAc ) {
        //the gene names are obtained from the Aliases for the Protein
        //which are of type 'gene name'...
        Criteria crit = getSession().createCriteria( getEntityClass() )
                .add( Restrictions.idEq( proteinAc ) )
                .createAlias( "aliases", "alias" )
                .createAlias( "alias.cvAliasType", "aliasType" )
                .add( Restrictions.in( "aliasType.shortLabel", geneNameFilter ) )
                .setProjection( Property.forName( "alias.name" ) );

        List<String> geneNames = crit.list();

        if ( geneNames.isEmpty() ) {
            geneNames.add( "-" );
        }

        return geneNames;
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<T> getByBioSourceAc( String ac ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "bioSource" )
                .add( Restrictions.idEq( ac ) )
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public int countInteractorInvolvedInInteraction() {
        final Long count = (Long) getSession().createCriteria(InteractorImpl.class)
                .add(Restrictions.isNotEmpty("activeInstances"))
                .setProjection(Projections.rowCount()).uniqueResult();
        return count.intValue();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<T> getInteractorInvolvedInInteraction( Integer firstResult, Integer maxResults ) {
        {
            Criteria crit = getSession().createCriteria( InteractorImpl.class )
//                    .createAlias("xrefs", "xref")
                    .add( Restrictions.isNotEmpty( "activeInstances" ) )
                    .addOrder( Order.asc( "ac" ) )
                    ;

            if ( firstResult != null && firstResult >= 0 ) {
                crit.setFirstResult( firstResult );
            }

            if ( maxResults != null && maxResults > 0 ) {
                crit.setMaxResults( maxResults );
            }

            return crit.list();
        }
    }

    /**
     * Counts the interactors, excluding the interactions
     *
     * @return the number of interactors, excluding the interactions
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public long countAllInteractors() {
        Query query = getEntityManager().createQuery("select count(*) from InteractorImpl  where objClass <> :interactionClass");
        query.setParameter("interactionClass", InteractionImpl.class.getName());
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            // Nothing to do, the query did not find any result
            return 0;
        }
    }

    /**
     * Gets the interactors, excluding the interactions
     *
     * @param firstResult First index to fetch
     * @param maxResults  Number of interactors to fetch
     * @return the interactors in that page
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Interactor> getInteractors(Integer firstResult, Integer maxResults) {
        Query query = getEntityManager().createQuery("select i from InteractorImpl i where i.objClass <> :interactionClass order by i.ac");
        query.setParameter("interactionClass", InteractionImpl.class.getName());
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    /**
     * Counts the partners of the provided interactor AC
     * @param ac The AC to search
     * @return The number of parntners for the interactor AC
     *
     * @since 1.8.0
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Integer countPartnersByAc( String ac ) {
        final Long count = (Long) partnersByAcCriteria(ac)
                .setProjection(Projections.countDistinct("prot.ac")).uniqueResult();
        return count.intValue();
    }

    protected Criteria partnersByAcCriteria( String ac ) {
        if ( ac == null ) {
            throw new NullPointerException( "ac" );
        }

        return getSession().createCriteria( InteractorImpl.class )
                .add( Restrictions.idEq( ac ) )
                .createAlias( "activeInstances", "comp" )
                .createAlias( "comp.interaction", "int" )
                .createAlias( "int.components", "intcomp" )
                .createAlias( "intcomp.interactor", "prot" )
                .add( Restrictions.disjunction()
                        .add( Restrictions.ne( "prot.ac", ac ) )
                        .add( Restrictions.eq( "comp.stoichiometry", 2f ) ) );
    }

    /**
     * Get the partners and the interaction ACs for the passes interactor AC
     * @param ac The AC to look parntners for
     * @return A Map containing the partner AC as key and a list of interaction ACs as value
     *
     * @since 1.8.0
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Map<String, List<String>> getPartnersWithInteractionAcsByInteractorAc( String ac ) {
        Criteria crit = partnersByAcCriteria( ac )
                .setProjection( Projections.projectionList()
                        .add( Projections.distinct( Projections.property( "prot.ac" ) ) )
                        .add( Projections.property( "int.ac" ) ) )
                .addOrder( Order.asc( "prot.ac" ) );

        return getPartnersWithInteractionAcs(crit);
    }

    protected Map<String, List<String>> getPartnersWithInteractionAcs(Criteria crit) {
        Map<String, List<String>> results = new HashMap<String, List<String>>();

        for ( Object[] res : ( List<Object[]> ) crit.list() ) {
            String partnerProtAc = ( String ) res[0];
            String interactionAc = ( String ) res[1];

            if ( results.containsKey( partnerProtAc ) ) {
                results.get( partnerProtAc ).add( interactionAc );
            } else {
                List<String> interactionAcList = new ArrayList<String>();
                interactionAcList.add( interactionAc );

                results.put( partnerProtAc, interactionAcList );
            }
        }
        return results;
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<T> getByInteractorType(String cvIdentifer, boolean includeChildren) {
        return createGetByInteractorTypeQuery(cvIdentifer, includeChildren, false).getResultList();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public long countByInteractorType(String cvIdentifer, boolean includeChildren) {
        try {
            return (Long) createGetByInteractorTypeQuery(cvIdentifer, includeChildren, true).getSingleResult();
        } catch (NoResultException e) {
            // Nothing to do, the query did not find any result
            return 0;
        }
    }

    private Query createGetByInteractorTypeQuery(String cvIdentifer, boolean includeChildren, boolean isCount) {
        List<String> cvIdentifiers = new ArrayList<String>();
        cvIdentifiers.add(cvIdentifer);

        if (includeChildren) {
            CvObjectDao<CvInteractorType> cvObjectDao = IntactContext.getCurrentInstance().getDaoFactory()
                    .getCvObjectDao(CvInteractorType.class);
            CvDagObject cvInteractorType = cvObjectDao.getByPsiMiRef(cvIdentifer);

            if (cvInteractorType != null) {
                final Set<String> childrenMIs = CvObjectUtils.getChildrenMIs(cvInteractorType);
                cvIdentifiers.addAll(childrenMIs);
            } else {
                if (log.isDebugEnabled()) log.debug("CvInteractorType with this identifier was not found in the database: "+cvIdentifer);
            }
        }

        String sqlQuery = (isCount? "select count(*) " : "") +
                          "from " + getEntityClass().getName() + " i where " +
                          "i.cvInteractorType.identifier in (:interactorTypeIdentifiers)";
        Query query = getEntityManager().createQuery(sqlQuery);
        query.setParameter("interactorTypeIdentifiers", cvIdentifiers);
        return query;
    }


}
