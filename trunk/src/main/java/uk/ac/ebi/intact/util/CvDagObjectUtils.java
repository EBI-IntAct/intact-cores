/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.model.CvObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Provides method to transform a DAG into a tree, to store these tree information into the database and to retrieve the
 * children for one specific node.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class CvDagObjectUtils {

    private int iLeftBound = -1;
    private int iRightBound = -1;

    /**
     * Constructs a CvDagObjectUtils object.
     */
    public CvDagObjectUtils() {
    }

    /**
     * Constructs the left and right bound for every single cvDagobject in the whole tree of elements, starting from the
     * given instance. The cvDagObject has to be inserted directly into the database because it could get later on
     * another left and rightBound if it has multiple parents
     *
     * @param newLeftBound int with the left bound to start from.
     * @param dag          a CvDagObject object
     *
     * @return int with the last used right bound + 1.
     */
    public int buildBounds( CvDagObject dag, int newLeftBound ) {
        int result = newLeftBound;

        // Init the left bound we received on this instance.
        this.iLeftBound = result++;
        // set the left bound to the CvDagObject just for testing
        dag.setLeftBound( iLeftBound );

        // Check for children, and if they are found, iterate them.
        if ( dag.hasChildren() ) {
            Collection children = dag.getChildren();
            for ( Iterator lIterator = children.iterator(); lIterator.hasNext(); ) {
                CvDagObject lTemp = ( CvDagObject ) lIterator.next();
                result = this.buildBounds( lTemp, result );
            }
        }

        // Finally (after the recursion ran), update this instance with
        // its new right bound.
        iRightBound = result;
        // set the rightbound to the CvDagObject
        dag.setRightBound( iRightBound );
        // insert that dag information directly into the database
        this.transferTreeNodeToDB( dag );
        // Return the last-used right bound + 1 (to be used as a new left bound).
        return ++result;
    }

    /**
     * This method inserts a CvDagObject with its left and rightBounds into the database.
     *
     * @param node the CvDagObject to be inserted into the database.
     */
    private void transferTreeNodeToDB( CvDagObject node ) {
        Statement stmt = null;
        StringBuffer sqlBuffer = new StringBuffer( 256 );
        sqlBuffer.append( "INSERT INTO IA_TreeHierarchie (cvObjectAc, type, leftBound, rightBound)" );
        sqlBuffer.append( "VALUES ('" ).append( node.getAc() ).append( "', '" ).append( node.getClass() ).append( "', " ).append( node.getLeftBound() ).append( ", " ).append( node.getRightBound() ).append( ");" );

        final String insertStatement = sqlBuffer.toString();
        System.out.println( insertStatement );
        try {
            stmt = getConnection().createStatement();
            stmt.execute( insertStatement );
        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
            } catch ( SQLException e ) {
            }
        }
    }

    /**
     * Determines the root node of that class and inserts the whole DAG as a tree into the database. Takes a class which
     * should inherit from the CvDagObject.
     *
     * @param cvClass Cv class to be inserted into the database.
     *
     * @throws IntactException if an error occur.
     */
    public void insertCVs( Class<? extends CvObject> cvClass ) throws IntactException {
        if ( !CvDagObject.class.isAssignableFrom( cvClass ) ) {
            throw new IntactException( "invalid class!" );
        }

        Collection cvDagObjects = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao( cvClass ).getAll();

        // take any object out of the list to get the root
        CvDagObject aDagObject = ( CvDagObject ) cvDagObjects.iterator().next();
        // get the root of the specified class
        CvDagObject root = aDagObject.getRoot();

        // build the bounds of the root and all its children and insert them into the database
        this.buildBounds( root, 1 );
    }


    /**
     * Returns all children of a specific CvDagObject. Provided that the DAG information is stored in the database.
     *
     * @param aCv CvDagObject to find all its children.
     *
     * @return collection containing all children ACs.
     */
    public Collection getCvWithChildren( CvDagObject aCv ) {
        Collection cvWithChildren = null;
        int leftBound;
        int rightBound;
        // SQL statement to get the right and leftBound from the parentCv
        final String queryStatement1 = "SELECT leftbound, rightbound " +
                                       "FROM ia_TreeHierarchie " +
                                       "WHERE cvObjectac = '" + aCv.getAc() + "'";

        String queryStatement2 = null;
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = getConnection().createStatement();
            result = stmt.executeQuery( queryStatement1 );

            // in case the CvDagObject has more than one parent, the resultSet has more than one result.
            // Anyway the children should be the same, so we can take just the first one.
            result.next();
            leftBound = Integer.parseInt( result.getString( 1 ) );
            rightBound = Integer.parseInt( result.getString( 2 ) );

            queryStatement2 = "SELECT cvObjectAc " +
                              "FROM ia_TreeHierarchie " +
                              "WHERE leftbound >" + leftBound + " AND " +
                              "      rightbound < " + rightBound + " AND " +
                              "      type = '" + aCv.getClass().toString() + "'";
            result = stmt.executeQuery( queryStatement2 );

            // just instantiate an new ArrayList, if the resultSet is not empty,
            // otherwise return an empty list
            if ( result.next() ) {
                cvWithChildren = new ArrayList();
            } else {
                return Collections.EMPTY_LIST;
            }
            do {
                // put all found children into the collection
                cvWithChildren.add( result.getString( 1 ) );
            } while ( result.next() );

        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
            if ( result != null ) {
                try {
                    result.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
        return cvWithChildren;
    }

    /**
     * This method retrieves all children of a specific parent CV given by the AC number.
     *
     * @param cvAc AC number of the parent CV.
     *
     * @return a collection with all children.
     *
     * @throws IntactException if an error occur.
     */
    public Collection getCvWithChildren( String cvAc ) throws IntactException {
        // collection to be returned
        Collection cvWithChildren = null;
        int leftBound;
        int rightBound;
        System.out.println( "cvAC: " + cvAc );
        // get the CVDagObject with the given AC number out of the database
        CvDagObject aCv = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao( CvDagObject.class ).getByAc( cvAc );

        // check if that parent CV really exists
        if ( aCv == null ) {
            throw new IntactException( "invalid AC: " + cvAc );
        }

        // get the left and right bounds of the parent AC out of the database
        String queryStatement1 = "SELECT leftbound, rightbound " +
                                 "FROM ia_TreeHierarchie " +
                                 "WHERE cvObjectAc = '" + aCv.getAc() + "'";

        String queryStatement2 = null;
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = getConnection().createStatement();
            result = stmt.executeQuery( queryStatement1 );

            // in case the CvDagObject has more than one parent, the resultSet has more than one result.
            // Anyway the children should be the same, so we can take just the first one.
            result.next();
            leftBound = Integer.parseInt( result.getString( 1 ) );
            rightBound = Integer.parseInt( result.getString( 2 ) );

            queryStatement2 = "SELECT cvObjectAc " +
                              "FROM ia_TreeHierarchie " +
                              "WHERE leftbound >" + leftBound + " AND " +
                              "      rightbound < " + rightBound + " AND " +
                              "      type = '" + aCv.getClass().toString() + "'";
            result = stmt.executeQuery( queryStatement2 );

            // just instantiate an new ArrayList, if the resultSet is not empty,
            // otherwise return an empty list
            if ( result.next() ) {
                cvWithChildren = new ArrayList();
            } else {
                return Collections.EMPTY_LIST;
            }
            do {
                // put all found children into the collection
                cvWithChildren.add( result.getString( 1 ) );
            } while ( result.next() );

        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            // close the statement and the resultSet
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
            if ( result != null ) {
                try {
                    result.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
        return cvWithChildren;
    }

    private Connection getConnection() {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory().connection();
    }
}