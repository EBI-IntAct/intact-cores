/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.InteractorImpl;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2006</pre>
 */
@SuppressWarnings( "unchecked" )
public class InteractorDaoImpl<T extends InteractorImpl> extends AnnotatedObjectDaoImpl<T> implements InteractorDao<T> {

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

    public InteractorDaoImpl( Class<T> entityClass, Session session, IntactSession intactSession ) {
        super( entityClass, session, intactSession );
    }

    public Integer countInteractionsForInteractorWithAc( String ac ) {
        return ( Integer ) getSession().createCriteria( Component.class )
                .createAlias( "interactor", "interactor" )
                .createAlias( "interaction", "interaction" )
                .add( Restrictions.eq( "interactor.ac", ac ) )
                .setProjection( Projections.countDistinct( "interaction.ac" ) ).uniqueResult();
    }

    public Integer countComponentsForInteractorWithAc( String ac ) {
        return ( Integer ) getSession().createCriteria( Component.class )
                .createAlias( "interactor", "interactor" )
                .add( Restrictions.eq( "interactor.ac", ac ) )
                .setProjection( Projections.countDistinct( "ac" ) ).uniqueResult();
    }


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

    public List<T> getByBioSourceAc( String ac ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "bioSource" )
                .add( Restrictions.idEq( "ac" ) ).list();
    }

    public int countInteractorInvolvedInInteraction() {
        return ( Integer ) getSession().createCriteria( InteractorImpl.class )
                .add( Restrictions.isNotEmpty( "activeInstances" ) )
                .setProjection( Projections.rowCount() ).uniqueResult();
    }

    public List<T> getInteractorInvolvedInInteraction( Integer firstResult, Integer maxResults ) {
        {
            Criteria crit = getSession().createCriteria( InteractorImpl.class )
//                    .createAlias("xrefs", "xref")
                    .add( Restrictions.isNotEmpty( "activeInstances" ) )
//                    .addOrder( Order.asc( "xref.primaryId" ) )
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
}
