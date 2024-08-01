/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

/**
 * Default implementation of the InteractionDao.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class InteractionDaoImpl extends InteractorDaoImpl<InteractionImpl> implements InteractionDao {

    private static final Log log = LogFactory.getLog( InteractionDaoImpl.class );

    public InteractionDaoImpl(  ) {
        super( InteractionImpl.class, null );
    }

    public InteractionDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( InteractionImpl.class, entityManager, intactSession );
    }

    /**
     * Counts the interactors for an interaction
     *
     * @param interactionAc The interaction accession number to use
     *
     * @return number of distinct interactors
     */
    public Integer countInteractorsByInteractionAc( String interactionAc ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Counting interactors for interaction with ac: " + interactionAc );
        }

        final Long count = (Long) getSession().createCriteria(InteractionImpl.class)
                .add(Restrictions.idEq(interactionAc))
                .createAlias("components", "comp")
                .createAlias("comp.interactor", "interactor")
                .setProjection(Projections.count("interactor.ac")).uniqueResult();
        return count.intValue();
    }

    public Integer countAllComplexes( ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Counting all complexes" );
        }

        final Long count = (Long) getSession().createCriteria(InteractionImpl.class)
                .createAlias("annotations", "a")
                .createAlias("a.cvTopic", "cv")
                .add(Restrictions.eq("cv.shortLabel", "curated-complex"))
                .setProjection(Projections.count("ac")).uniqueResult();
        return count.intValue();
    }

    public List<String> getNestedInteractionAcsByInteractionAc( String interactionAc ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Getting nested interactions for interaction with ac: " + interactionAc );
        }

        return getSession().createCriteria( InteractionImpl.class )
                .add( Restrictions.idEq( interactionAc ) )
                .createAlias( "components", "comp" )
                .createAlias( "comp.interactor", "interactor" )
                .add( Restrictions.eq( "interactor.objClass", InteractionImpl.class.getName() ) )
                .setProjection( Projections.distinct( Projections.property( "interactor.ac" ) ) ).list();
    }

    public List<Interaction> getInteractionByExperimentShortLabel( String[] experimentLabels, Integer firstResult, Integer maxResults ) {
        Criteria criteria = getSession().createCriteria( Interaction.class )
                .createCriteria( "experiments" )
                .add( Restrictions.in( "shortLabel", experimentLabels ) )
                .addOrder(org.hibernate.criterion.Order.asc("ac"));

        if ( firstResult != null && firstResult >= 0 ) {
            criteria.setFirstResult( firstResult );
        }

        if ( maxResults != null && maxResults > 0 ) {
            criteria.setMaxResults( maxResults );
        }

        return criteria.list();
    }

    public List<Interaction> getInteractionsByInteractorAc( String interactorAc ) {
        return getSession().createCriteria( Interaction.class )
                .createAlias( "components", "comp" )
                .createAlias( "comp.interactor", "interactor" )
                .add( Restrictions.eq( "interactor.ac", interactorAc ) ).list();
    }

    @Deprecated
    public List<Interaction> getInteractionsForProtPair( String protAc1, String protAc2 ) {
          return getInteractionsForProtPairAc(protAc1, protAc2);
    }

    public List<Interaction> getInteractionsForProtPairAc( String protAc1, String protAc2 ) {
        // check first if the ACs exist (in the main table or as secondary ACs.
        protAc1 = getAcByPrimaryOrSecondary(protAc1);

        if (protAc1 == null) return Collections.EMPTY_LIST;

        protAc2 = getAcByPrimaryOrSecondary(protAc2);

        if (protAc2 == null) return Collections.EMPTY_LIST;

        Query query = getEntityManager().createQuery( "SELECT i FROM InteractionImpl AS i, Component AS c1, Component AS c2 " +
                                                "WHERE i.ac = c1.interactionAc AND i.ac = c2.interactionAc AND " +
                                                "c1.interactorAc = :protAc1 AND c2.interactorAc = :protAc2" );

        query.setParameter( "protAc1", protAc1 );
        query.setParameter( "protAc2", protAc2 );

        return query.getResultList();
    }

    private String getAcByPrimaryOrSecondary(String protAc) {
        Query interactorQuery = getEntityManager().createQuery("SELECT COUNT (i) FROM InteractorImpl i WHERE i.ac = :interactorAc");
        interactorQuery.setParameter("interactorAc", protAc);

        Long interactorQueryCount = 0L;

        try {
            interactorQueryCount = (Long) interactorQuery.getSingleResult();
        } catch (NoResultException e) {
            // Nothing to do, the query did not find any result
        }

        if (interactorQueryCount == 0) {
            Query secondaryQuery = getEntityManager().createQuery("SELECT xref.parent.ac FROM InteractorXref xref WHERE xref.primaryId = :primaryId");
            secondaryQuery.setParameter("primaryId", protAc);

            List<String> results = secondaryQuery.getResultList();

            if (results.size() > 0) {
                return results.iterator().next();
            } else {
                return null;
            }
        }

        return protAc;
    }

    public Collection<Interaction> getSelfBinaryInteractionsByProtAc( String protAc ) {
        List<Interaction> interactions = getInteractionsByInteractorAc( protAc );

        Set<Interaction> selfInteractions = new HashSet<Interaction>();

        for ( Interaction inter : interactions ) {
            boolean isSelfInteraction = InteractionUtils.isSelfBinaryInteraction( inter );

            if ( isSelfInteraction ) {
                selfInteractions.add( inter );
            }
        }

        return selfInteractions;
    }

    /**
     * @inheritDoc
     */
    public List<Interaction> getByInteractorsPrimaryId(boolean exactComponents, String... primaryIds) {
        if (primaryIds.length > 5) {
            if (exactComponents) {
                return getByInteractorsPrimaryIdExactComponents(primaryIds);
            } else {
                throw new IntactException("Searching for more than 5 components will make this query inefficient");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select i from InteractionImpl as i ");

        for (int i=0; i<primaryIds.length; i++) {
            sb.append("join i.components as comp").append(i).append(" ");
            sb.append("join comp").append(i).append(".interactor.xrefs as xref").append(i).append(" ");
        }

        sb.append("where ");

        for (int i=0; i<primaryIds.length; i++) {
            if (i>0) {
                sb.append("and ");
            }
            sb.append("xref").append(i).append(".primaryId = :protPrimaryId").append(i).append(" ");
        }

        if (exactComponents) {
            if (primaryIds.length > 0) {
                sb.append("and ");
            }
            sb.append("size(i.components) = "+primaryIds.length);
        }

        Query query = getEntityManager().createQuery(sb.toString());

        for (int i=0; i<primaryIds.length; i++) {
            query.setParameter("protPrimaryId"+i, primaryIds[i]);
        }

        return query.getResultList();
    }

    protected List<Interaction> getByInteractorsPrimaryIdExactComponents(String... primaryIds) {
        List<Interaction> results = new ArrayList<Interaction>();

        // get first all the interactions of the same size (efficient only for interactions with several components)
        Query query = getEntityManager().createQuery("from InteractionImpl i where size(i.components) = :compSize");
        query.setParameter("compSize", primaryIds.length);

        List<Interaction> interactionsOfTheSameSize = query.getResultList();

        // the following crappy algorithm checks that the interactors contained in the interactions
        // have the provided list of primaryIds
        for (Interaction interaction : interactionsOfTheSameSize) {
            String[] primIdsToFind = new String[primaryIds.length];
            System.arraycopy(primaryIds, 0, primIdsToFind, 0, primaryIds.length);

            for (Component component : interaction.getComponents()) {
                for (InteractorXref idXref : XrefUtils.getIdentityXrefs(component.getInteractor())) {
                    for (int i=0; i<primIdsToFind.length; i++) {
                        if (idXref.getPrimaryId().equals(primIdsToFind[i])) {
                            primIdsToFind[i] = "";
                        }
                    }
                }
            }

            boolean found = true;

           for (String id : primIdsToFind) {
               if (id.length() > 0) {
                   found = false;
               }
           }

            if (found) {
                results.add(interaction);
            }
        }

        return results;
    }

     /**
     * @inheritDoc
     */
    public Interaction getByCrc(String crc) {
        Query query = getEntityManager().createQuery("from InteractionImpl where crc = :crc");
        query.setParameter("crc", crc);

        List<Interaction> interactions = query.getResultList();

        if (interactions.isEmpty()) {
            return null;
        }

        if (interactions.size() > 1) {
            log.error("Getting an interaction by CRC returned more than one result. Using the first one: "+crc);
        }

        return interactions.get(0);
    }
    
    /**
     * @inheritDoc
     */
    public List<Interaction> getByLastImexUpdate( Date fromDate, Date toDate) {
        if ( fromDate == null ) {
            throw new IllegalArgumentException( "You must give a non null fromDate" );
        }
        if ( toDate == null ) {
            throw new IllegalArgumentException( "You must give a non null toDate" );
        }
        if( toDate.before(fromDate ) ) {
            throw new IllegalArgumentException( "Invalid date range, toDate is before fromDate." );
        }

        Query query = getEntityManager().createQuery("select i " +
                                                     "from InteractionImpl i " +
                                                     "where     i.lastImexUpdate >= :fromDate " +
                                                     "      and i.lastImexUpdate <= :toDate");
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        return query.getResultList();
    }

    /**
     * @InheritDoc
     */
    public List<Interaction> getByExperimentAc( String experimentAc, int firstResult, int maxResults ) {
        Query query = getEntityManager().createQuery("select i " +
                                                     "from InteractionImpl i join i.experiments e " +
                                                     "where e.ac = :experimentAc " +
                                                     "order by i.created, i.ac");
        query.setParameter("experimentAc", experimentAc);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    @Override
    public int countAll( boolean includeNegative ) {
        if( ! includeNegative ) {

            Query query = getEntityManager().createQuery("select count(*) " +
                                                         "from InteractionImpl i " +
                                                         "where not exists ( select ni " +
                                                         "                 from InteractionImpl ni join ni.annotations a " +
                                                         "                 where ni.ac = i.ac and a.cvTopic.shortLabel = 'negative'" +
                                                         "               )");
//            query.setParameter("interactionClass", InteractionImpl.class.getName());
            try {
                return ((Long) query.getSingleResult()).intValue();
            } catch (NoResultException e) {
                // Nothing to do, the query did not find any result
                return 0;
            }
        }

        return countAll();
    }
}
