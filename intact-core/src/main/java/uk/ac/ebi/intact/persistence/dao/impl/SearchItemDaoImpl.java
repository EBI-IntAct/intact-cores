/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.annotation.PotentialThreat;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.SearchItem;
import uk.ac.ebi.intact.persistence.dao.SearchItemDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for search items
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Apr-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class SearchItemDaoImpl extends HibernateBaseDaoImpl<SearchItem> implements SearchItemDao {

    public SearchItemDaoImpl( Session session, IntactSession intactSession ) {
        super( SearchItem.class, session, intactSession );
    }

    public Map<String, Integer> countGroupsByValuesLike( String[] values, String[] objClasses, String type ) {
        return countGroupsByValuesLike( values, objClasses, type, true );
    }

    public Map<String, Integer> countGroupsByValuesLike( String[] values, String[] objClasses, String type, boolean ignoreCase ) {
        Map<String, Integer> results = new HashMap<String, Integer>();

        List<Object[]> critRes = criteriaByValues( values, objClasses, type, ignoreCase )
                .setProjection( Projections.projectionList()
                        .add( Projections.countDistinct( "ac" ) )
                        .add( Projections.groupProperty( "objClass" ) ) ).list();

        for ( Object[] res : critRes ) {
            results.put( ( String ) res[1], ( Integer ) res[0] );
        }

        return results;
    }

    public List<String> getDistinctAc( String[] values, String[] objClasses, String type, int firstResult, int maxResults ) {

        return criteriaByValues( values, objClasses, type, true )
                .setFirstResult( firstResult )
                .setMaxResults( maxResults )
                .setProjection(
                        Projections.distinct( Projections.property( "ac" ) ) ).list();

    }


    public Map<String, String> getDistinctAcGroupingByObjClass( String[] values, String[] objClasses, String type, int firstResult, int maxResults ) {

        Map<String, String> results = new HashMap<String, String>();

        List<Object[]> critRes = criteriaByValues( values, objClasses, type, true )
                .setFirstResult( firstResult )
                .setMaxResults( maxResults )
                .setProjection( Projections.projectionList()
                        .add( Projections.distinct( Projections.property( "ac" ) ) )
                        .add( Projections.property( "objClass" ) ) ).list();

        for ( Object[] res : critRes ) {
            results.put( ( String ) res[0], ( String ) res[1] );
        }

        return results;
    }

    public List<SearchItem> getByAc( String ac ) {
        return getSession().createCriteria( SearchItem.class )
                .add( Restrictions.eq( "ac", ac ) ).list();
    }

    @PotentialThreat( description = "This method is using raw SQL (INSERT Query), which may create problems " +
                                    "when run with hibernate",
                      origin = "This code is used in a EventListener, so the session cannot be committed and we" +
                               "need a workaroung" )
    public void persist( SearchItem searchItem ) {
        String sql = "INSERT INTO ia_search (ac,value,objclass,type) VALUES (?,?,?,?)";
        executeQueryUpdateForSearchItem( sql, searchItem );
    }

    @PotentialThreat( description = "This method is using raw SQL (DELETE Query), which may create problems " +
                                    "when run with hibernate",
                      origin = "This code is used in a EventListener, so the session cannot be committed and we" +
                               "need a workaroung" )
    public void delete( SearchItem searchItem ) {
        String sql = "DELETE from ia_search WHERE ac=? AND value=? AND objclass=? AND type=?";
        executeQueryUpdateForSearchItem( sql, searchItem );
    }

    private int executeQueryUpdateForSearchItem( String sql, SearchItem searchItem ) {
        int rows = 0;

        Connection connection = null;
        try {
            connection = getSession().connection();
            PreparedStatement statement = connection.prepareStatement( sql );
            statement.setString( 1, searchItem.getAc() );
            statement.setString( 2, searchItem.getValue() );
            statement.setString( 3, searchItem.getObjClass() );
            statement.setString( 4, searchItem.getType() );

            rows = statement.executeUpdate();
        }
        catch ( SQLException e ) {
            e.printStackTrace();
        }
        finally {
            if ( connection != null ) {
                try {
                    connection.close();
                }
                catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }

        return rows;
    }

    @PotentialThreat( description = "This method is using raw SQL (DELETE Query), which may create problems " +
                                    "when run with hibernate",
                      origin = "This code is used in a EventListener, so the session cannot be committed and we" +
                               "need a workaroung" )
    public int deleteByAc( String ac ) {
        String sql = "DELETE FROM ia_search WHERE ac=?";

        int rows = 0;

        Connection connection = null;
        try {
            connection = getSession().connection();
            PreparedStatement statement = connection.prepareStatement( sql );
            statement.setString( 1, ac );

            rows = statement.executeUpdate();
        }
        catch ( SQLException e ) {
            e.printStackTrace();
        }
        finally {
            if ( connection != null ) {
                try {
                    connection.close();
                }
                catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }

        return rows;
    }

    private Criteria criteriaByValues( String[] values, String[] objClasses, String type, boolean ignoreCase ) {
        Criteria crit = getSession().createCriteria( SearchItem.class );

        // no restriction (WHERE) necessary if only one value is passed and it is a wildcard
        if ( !( values.length == 1 && values[0].equals( "%" ) ) ) {
            crit.add( disjunctionForArray( "value", values, ignoreCase ) );
        }

        if ( objClasses != null ) {
            if ( objClasses.length > 1 ) {
                crit.add( disjunctionForArray( "objClass", objClasses ) );
            } else {
                if ( objClasses[0] != null ) {
                    crit.add( Restrictions.eq( "objClass", objClasses[0] ) );
                }
            }
        }

        if ( type != null ) {
            crit.add( Restrictions.eq( "type", type ) );
        }

        return crit;
    }

}
