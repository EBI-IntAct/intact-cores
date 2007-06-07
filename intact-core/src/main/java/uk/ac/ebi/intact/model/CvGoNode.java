/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Controlled vocabulary class for GeneOntology. CVs which are organised in
 * a Directed Acyclic (DAG). Each node can have many multiple parents and multiple
 * children. Each node represents one specific GO ID (cv) in the GO hierarchy.<br>
 * Before this class can be used, GoTools.insertGoDag(CvGoNode.class, GoDagFlatfile)
 * must be executet.
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvGoNode" )
public class CvGoNode extends CvDagObject {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    /**
     * record nodes that have been visited during graph exploration.<br>
     * Used by "getAllChilds"
     */
    private HashSet<CvGoNode> visited;

    /**
     * level of hierarchy in dag. Level 0 is the original node (this).
     */
    private int level;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * This constructor should be replaced as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     */
    public CvGoNode() {
        super();
        level = 0;
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * @return The GO id, which belongs to this unique CvGoNode Object
     */
    @Transient
    public String getGoId() {
        Collection<CvObjectXref> someXrefs = this.getXrefs();
        if ( someXrefs.size() != 1 ) {
            System.out.println( "warning: a CvGoNode can only have one specific Xref" );
        }
        Xref xref = someXrefs.iterator().next();
        return xref.getPrimaryId();
    }

    /**
     * This CvGoNode can have children - the equivalent method is implemented in the
     * super class CvDagObject by "getChilds" method<br>
     * This method should provide ALL children (subchildren) of the node,<br>
     * that means also the children of the children of the children etc.
     *
     * @return all possible children of this node as a Collection of CvGoNodes
     */
    @Transient
    public Collection<CvGoNode> getAllChilds() {
        visited = new HashSet<CvGoNode>();
        Collection<CvGoNode> childs = new ArrayList<CvGoNode>();
        childs = this.calculateAllChilds( this, childs );
        return childs; // Collection of CvGoNodes
    }

    // === Common interface ==================================================

    /**
     * @return String represention of this node
     */
    @Override
    public String toString() {
        StringBuffer string = new StringBuffer();
        string.append( "\nCvGoNode object for " + this.getGoId() );
        string.append( "\n      -> childs: " );
        for ( CvDagObject cvDagObject : this.getChildren() ) {
            CvGoNode cvGoNode = ( CvGoNode ) cvDagObject;
            string.append( cvGoNode.getGoId() ).append( ", " );
        }
        return string.toString();
    }

    // =======================================================================
    // Private Methods
    // =======================================================================

    /**
     * Recursive method wich calculates all children and subchildren of a node.
     *
     * @param aParent
     * @param collection - to call method with an empty collection - it is used internally for recursive call.
     *
     * @return all children and subchildren as CvGoNode objects
     */
    private Collection<CvGoNode> calculateAllChilds( CvGoNode aParent, Collection<CvGoNode> collection ) {
        if ( !visited.contains( aParent ) ) {
            // Iterate over children to add subtrees.
            level++; // Hierarchylevel
            for ( CvDagObject cvDagObject : aParent.getChildren() ) {
                // For each child recursive call
                CvGoNode node = ( CvGoNode ) cvDagObject;
                collection = calculateAllChilds( node, collection );
            }
            level--;
            // only children are in interested - root of request will be filtered out
            if ( level > 0 ) {
                collection.add( aParent );
                visited.add( aParent );
            }
        }
        return collection; // CvGoNodes
    }
}




