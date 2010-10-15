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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * Protein specific searches.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class ProteinDaoImpl extends PolymerDaoImpl<ProteinImpl> implements ProteinDao {

    private static Log log = LogFactory.getLog( ProteinDaoImpl.class );

    public ProteinDaoImpl( ) {
        super( ProteinImpl.class, null );
    }

    public ProteinDaoImpl( EntityManager entityManager ) {
        super( ProteinImpl.class, entityManager );
    }

    public ProteinDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( ProteinImpl.class, entityManager, intactSession );
    }

    public String getIdentityXrefByProteinAc( String proteinAc ) {
        Criteria crit = getSession().createCriteria( ProteinImpl.class )
                .add( Restrictions.idEq( proteinAc ) )
                .createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvXrefQualifier", "qual" )
                .add( Restrictions.eq( "qual.shortLabel", CvXrefQualifier.IDENTITY ) )
                .setProjection( Projections.property( "xref.ac" ) );

        return ( String ) crit.uniqueResult();
    }

    public String getUniprotAcByProteinAc( String proteinAc ) {
        Criteria crit = getSession().createCriteria( ProteinImpl.class )
                .add( Restrictions.idEq( proteinAc ) )
                .createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvXrefQualifier", "qual" )
                .add( Restrictions.eq( "qual.shortLabel", CvXrefQualifier.IDENTITY ) )
                .setProjection( Projections.property( "xref.primaryId" ) );

        return ( String ) crit.uniqueResult();
    }

    public List<String> getUniprotUrlTemplateByProteinAc( String proteinAc ) {
        if ( proteinAc == null ) {
            throw new NullPointerException( "proteinAc" );
        }

        Criteria crit = getSession().createCriteria( ProteinImpl.class )
                .add( Restrictions.idEq( proteinAc ) )
                .createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvXrefQualifier", "cvQual" )
                .createAlias( "xref.cvDatabase", "cvDb" )
                .createAlias( "cvDb.annotations", "annot" )
                .createAlias( "annot.cvTopic", "cvTopic" )
                .add( Restrictions.eq( "cvQual.shortLabel", CvXrefQualifier.IDENTITY ) )
                .add( Restrictions.eq( "cvTopic.shortLabel", CvTopic.SEARCH_URL ) )
                .setProjection( Projections.property( "annot.annotationText" ) );

        return crit.list();
    }

    public Map<String, Integer> getPartnersCountingInteractionsByProteinAc( String proteinAc ) {
        if ( proteinAc == null ) {
            throw new NullPointerException( "proteinAc" );
        }

        Criteria crit = getSession().createCriteria( ProteinImpl.class )
                .add( Restrictions.idEq( proteinAc ) )
                .createAlias( "activeInstances", "comp" )
                .createAlias( "comp.interaction", "int" )
                .createAlias( "int.components", "intcomp" )
                .createAlias( "intcomp.interactor", "prot" )
                .add( Restrictions.disjunction()
                        .add( Restrictions.ne( "prot.ac", proteinAc ) )
                        .add( Restrictions.eq( "comp.stoichiometry", 2f ) ) )
                .setProjection( Projections.projectionList()
                        .add( Projections.countDistinct( "int.ac" ) )
                        .add( Projections.groupProperty( "prot.ac" ) ) );

        List<Object[]> queryResults = crit.list();

        Map<String, Integer> results = new HashMap<String, Integer>( queryResults.size() );

        for ( Object[] res : queryResults ) {
            results.put( ( String ) res[1], (( Long ) res[0]).intValue() );
        }

        return results;
    }

    @Deprecated
    public Integer countPartnersByProteinAc( String proteinAc ) {
        final Long count = (Long) partnersByAcCriteria(proteinAc)
                .setProjection(Projections.countDistinct("prot.ac")).uniqueResult();
        return count.intValue();
    }

    public List<ProteinImpl> getUniprotProteins( Integer firstResult, Integer maxResults ) {
        Criteria crit = criteriaForUniprotProteins()
                .addOrder( Order.asc( "xref.primaryId" ) );

        if ( firstResult != null && firstResult >= 0 ) {
            crit.setFirstResult( firstResult );
        }

        if ( maxResults != null && maxResults > 0 ) {
            crit.setMaxResults( maxResults );
        }

        return crit.list();
    }

    public List<ProteinImpl> getUniprotProteinsInvolvedInInteractions( Integer firstResult, Integer maxResults ) {
        Criteria crit = criteriaForUniprotProteins()
                .add( Restrictions.isNotEmpty( "activeInstances" ) )
                .addOrder( Order.asc( "xref.primaryId" ) );

        if ( firstResult != null && firstResult >= 0 ) {
            crit.setFirstResult( firstResult );
        }

        if ( maxResults != null && maxResults > 0 ) {
            crit.setMaxResults( maxResults );
        }

        return crit.list();
    }

    public Integer countUniprotProteins() {
        return ( Integer ) criteriaForUniprotProteins()
                .setProjection( Projections.rowCount() ).uniqueResult();
    }

    public Integer countUniprotProteinsInvolvedInInteractions() {
        return ( Integer ) criteriaForUniprotProteins()
                .add( Restrictions.isNotEmpty( "activeInstances" ) )
                .setProjection( Projections.rowCount() ).uniqueResult();
    }


    public List<ProteinImpl> getByUniprotId( String uniprotId ) {
        return getByXrefLike(CvDatabase.UNIPROT_MI_REF, CvXrefQualifier.IDENTITY_MI_REF, uniprotId);
//                getSession().createCriteria( getEntityClass() )
//                .createAlias( "xrefs", "xref" )
//                .createAlias( "xref.cvXrefQualifier", "qual" )
//                .createAlias( "xref.cvDatabase", "database" )
//                .createCriteria( "qual.xrefs", "qualXref" )
//                .createCriteria( "database.xrefs", "dbXref" )
//                .add( Restrictions.eq( "qualXref.primaryId", CvXrefQualifier.IDENTITY_MI_REF ) )
//                .add( Restrictions.eq( "dbXref.primaryId", CvDatabase.UNIPROT_MI_REF ) )
//                .add( Restrictions.eq( "xref.primaryId", uniprotId ) ).list();
    }

    /**
     * @deprecated use the method getPartnersWithInteractionAcsByInteractorAc() instead
     */
    @Deprecated
    public Map<String, List<String>> getPartnersWithInteractionAcsByProteinAc( String proteinAc ) {
        Criteria crit = partnersByAcCriteria( proteinAc )
                .setProjection( Projections.projectionList()
                        .add( Projections.distinct( Projections.property( "prot.ac" ) ) )
                        .add( Projections.property( "int.ac" ) ) )
                .addOrder( Order.asc( "prot.ac" ) );

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

    public List<String> getPartnersUniprotIdsByProteinAc( String proteinAc ) {
        return partnersByAcCriteria( proteinAc )
                .createAlias( "prot.xrefs", "xref" )
                .createAlias( "xref.cvXrefQualifier", "qual" )
                .createAlias( "xref.cvDatabase", "database" )
                .createCriteria( "qual.xrefs", "qualXref" )
                .createCriteria( "database.xrefs", "dbXref" )
                .add( Restrictions.eq( "qualXref.primaryId", CvXrefQualifier.IDENTITY_MI_REF ) )
                .add( Restrictions.eq( "dbXref.primaryId", CvDatabase.UNIPROT_MI_REF ) )
                .setProjection( Projections.distinct( Property.forName( "xref.primaryId" ) ) ).list();
    }

    protected List<ProteinImpl> getProteinTranscripts( Protein protein, String cvXRefQualifier) {
        if ( protein == null ) {
            throw new NullPointerException( "The master protein must not be null." );
        }

        if ( cvXRefQualifier == null ) {
            throw new NullPointerException( "The Cv Xref qualifier must not be null." );
        }

        String ac = protein.getAc();

        if ( ac == null ) {
            // This protein doesn't have an AC, it cannot have protein transcripts.
            if (log.isWarnEnabled()) log.warn("Cannot find protein transcripts for a protein without AC: "+protein.getShortLabel());
            return Collections.EMPTY_LIST;
        }

        Query query = getEntityManager().createQuery("select prot from ProteinImpl prot inner join " +
                "prot.xrefs as xref where " +
                "xref.cvXrefQualifier.identifier = :transcriptParentMi " +
                "and xref.cvDatabase.identifier = :intactMi " +
                "and xref.primaryId = :masterAc");
        query.setParameter("transcriptParentMi", cvXRefQualifier);
        query.setParameter("intactMi", CvDatabase.INTACT_MI_REF);
        query.setParameter("masterAc", ac);

        return query.getResultList();
    }

    public List<ProteinImpl> getSpliceVariants( Protein protein ) {
        /**if ( protein == null ) {
         throw new NullPointerException( "The master protein must not be null." );
         }

         String ac = protein.getAc();

         if ( ac == null ) {
         // This protein doesn't have an AC, it cannot have splice variants.
         if (log.isWarnEnabled()) log.warn("Cannot find splice variants for a protein without AC: "+protein.getShortLabel());
         return Collections.EMPTY_LIST;
         }

         Query query = getEntityManager().createQuery("select prot from ProteinImpl prot inner join " +
         "prot.xrefs as xref where " +
         "xref.cvXrefQualifier.identifier = :isoformParentMi " +
         "and xref.cvDatabase.identifier = :intactMi " +
         "and xref.primaryId = :masterAc");
         query.setParameter("isoformParentMi", CvXrefQualifier.ISOFORM_PARENT_MI_REF);
         query.setParameter("intactMi", CvDatabase.INTACT_MI_REF);
         query.setParameter("masterAc", ac);

         return query.getResultList();**/

        return getProteinTranscripts(protein, CvXrefQualifier.ISOFORM_PARENT_MI_REF);
    }

    public List<ProteinImpl> getProteinChains( Protein protein ) {

        return getProteinTranscripts(protein, CvXrefQualifier.CHAIN_PARENT_MI_REF);
    }
    protected ProteinImpl getProteinTranscriptsMasterProtein( Protein proteinTranscript, String cvXRefQualifier) {
        if ( proteinTranscript == null ) {
            throw new NullPointerException( "proteinTranscript must not be null." );
        }

        if ( cvXRefQualifier == null ) {
            throw new NullPointerException( "The Cv Xref qualifier must not be null." );
        }

        String masterProtAc = null;

        for (InteractorXref xref : proteinTranscript.getXrefs()) {
            if (xref.getCvXrefQualifier() != null && cvXRefQualifier.equals(xref.getCvXrefQualifier().getIdentifier())) {
                if (masterProtAc == null) {
                    masterProtAc = xref.getPrimaryId();
                } else {
                    throw new IntactException("This protein transcript contains more than one "+cvXRefQualifier+" xrefs: "+proteinTranscript.getShortLabel() );
                }
            }
        }

        // search protein by AC
        return getByAc(masterProtAc);
    }


    public ProteinImpl getSpliceVariantMasterProtein( Protein spliceVariant ) {

        return getProteinTranscriptsMasterProtein(spliceVariant, CvXrefQualifier.ISOFORM_PARENT_MI_REF);
    }

    public ProteinImpl getChainMasterProtein( Protein chain ) {

        return getProteinTranscriptsMasterProtein(chain, CvXrefQualifier.CHAIN_PARENT_MI_REF);
    }

    /**
     * Gets all the uniprot ACs from the database, which are involved in interactions
     * @return the uniprot ACs
     *
     * @since 1.8.1
     */
    public List<String> getAllUniprotAcs() {
        Query query = getEntityManager().createQuery("select distinct(xref.primaryId) from InteractorXref xref " +
                "where xref.cvXrefQualifier.identifier = :qualifierMi " +
                "and xref.cvDatabase.identifier = :uniprotMi " +
                "and size(xref.parent.activeInstances) > 0");
        query.setParameter("qualifierMi", CvXrefQualifier.IDENTITY_MI_REF);
        query.setParameter("uniprotMi", CvDatabase.UNIPROT_MI_REF);

        return query.getResultList();
    }

    /**
     * Builds a Hibernate Criteria allowing to select a UniProt protein.
     *
     * @return a non null Hibernate criteria.
     */
    private Criteria criteriaForUniprotProteins() {
        return getSession().createCriteria( ProteinImpl.class )
                .createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvDatabase", "cvDatabase" )
                .createAlias( "xref.cvXrefQualifier", "cvXrefQualifier" )
                .add( Restrictions.eq( "cvDatabase.miIdentifier",
                        CvDatabase.UNIPROT_MI_REF ) )
                .add( Restrictions.eq( "cvXrefQualifier.miIdentifier",
                        CvXrefQualifier.IDENTITY_MI_REF ) )

                .add( Restrictions.not( Restrictions.like( "xref.primaryId", "A%" ) ) )
                .add( Restrictions.not( Restrictions.like( "xref.primaryId", "B%" ) ) )
                .add( Restrictions.not( Restrictions.like( "xref.primaryId", "C%" ) ) );
    }
}