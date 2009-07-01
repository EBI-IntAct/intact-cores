/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.business.IntactException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class InteractionDaoImpl extends InteractorDaoImpl<InteractionImpl> implements InteractionDao {

    private static final Log log = LogFactory.getLog( InteractionDaoImpl.class );

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

        return ( Integer ) getSession().createCriteria( InteractionImpl.class )
                .add( Restrictions.idEq( interactionAc ) )
                .createAlias( "components", "comp" )
                .createAlias( "comp.interactor", "interactor" )
                .setProjection( Projections.count( "interactor.ac" ) ).uniqueResult();
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
                .add( Restrictions.in( "shortLabel", experimentLabels ) );

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
        Query query = getEntityManager().createQuery( "SELECT i FROM InteractionImpl AS i, Component AS c1, Component AS c2 " +
                                                "WHERE i.ac = c1.interactionAc AND i.ac = c2.interactionAc AND " +
                                                "c1.interactorAc = :protAc1 AND c2.interactorAc = :protAc2" );

        query.setParameter( "protAc1", protAc1 );
        query.setParameter( "protAc2", protAc2 );

        return query.getResultList();
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

        Query query = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getEntityManager().createQuery(sb.toString());

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
}
