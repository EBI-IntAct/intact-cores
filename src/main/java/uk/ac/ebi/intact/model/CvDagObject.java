/** Java class "CvDagObject.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Controlled vocabulary class for CVs which are organised in a Directed Acyclic Graph (DAG).
 * <p/>
 * Each node many have multiple parents and multiple children.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
public abstract class CvDagObject extends CvObject {

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments Children are unique
     */
    private Collection<CvDagObject> children = new ArrayList<CvDagObject>();
    /**
     * TODO comments Parents are unique
     */
    private Collection<CvDagObject> parents = new ArrayList<CvDagObject>();

    /**
     * specifies the left bound number if the DAG would be a tree
     */
    private long leftBound = -1;

    /**
     * specifies the right bound number if the DAG would be a tree
     */
    private long rightBound = -1;

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvDagObject() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvDagObject instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvDagObject
     * @param owner      The Institution which owns this CvDagObject
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvDagObject( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

    ///////////////////////////////////////
    // access methods for associations

    @ManyToMany
    @JoinTable(
            name = "ia_cv2cv",
            joinColumns = {@JoinColumn( name = "parent_ac", referencedColumnName = "ac" )},
            inverseJoinColumns = {@JoinColumn( name = "child_ac", referencedColumnName = "ac" )}
    )
    public Collection<CvDagObject> getChildren() {
        return children;
    }


    //////////////////////////////////////////////////////////
    // the setter and getter are only used for testing


    /**
     * this method returns 'true' when children are present 'false' otherwise
     *
     * @return result
     */
    public boolean hasChildren() {
        boolean result = false;

        if ( this.getChildren().size() > 0 && children != null ) {
            result = true;
        }
        return result;
    }


    // end modification (afrie)
    /////////////////////////////

    // TODO are they unique ?

    public void addChild( CvDagObject cvDagObject ) {

        if ( !children.contains( cvDagObject ) ) {
            children.add( cvDagObject );
            cvDagObject.addParent( this );
        }
    }

    public void removeChild( CvDagObject cvDagObject ) {
        boolean removed = children.remove( cvDagObject );
        if ( removed ) {
            cvDagObject.removeParent( this );
        }
    }

    @ManyToMany( mappedBy = "children" )
    public Collection<CvDagObject> getParents() {
        return parents;
    }

    public void addParent( CvDagObject cvDagObject ) {

        if ( !parents.contains( cvDagObject ) ) {
            parents.add( cvDagObject );
            cvDagObject.addChild( this );
        }
    }

    public void removeParent( CvDagObject cvDagObject ) {
        boolean removed = parents.remove( cvDagObject );
        if ( removed ) {
            cvDagObject.removeChild( this );
        }
    }

    public void setChildren( Collection<CvDagObject> children ) {
        if ( children == null ) {
            throw new IllegalArgumentException( "Children cannot be null." );
        }
        this.children = children;
    }

    public void setParents( Collection<CvDagObject> parents ) {
        if ( parents == null ) {
            throw new IllegalArgumentException( "Parents cannot be null." );
        }
        this.parents = parents;
    }

    //////////////////////////////
    // Specific DAG methods

    /**
     * Add the ancestors of the current node to the set of currentAncestors. This assumes that you gives a non null
     * Set.
     *
     * @param currentAncestors Ancestors collected so far.
     *
     * @return currentAncestors, with all the ancestors of the current node added.
     */
    private Collection<CvDagObject> ancestors( Collection<CvDagObject> currentAncestors ) {
        for ( CvDagObject current : getParents() ) {
            currentAncestors = current.ancestors( currentAncestors ); // recursive call
        }
        currentAncestors.add( this );
        return currentAncestors;
    }

    /**
     * TODO comments
     *
     * @return All ancestors of the current object. Each node is listed only once.
     */
    public Collection<CvDagObject> ancestors() {
        return this.ancestors( new ArrayList<CvDagObject>() );
    }

    /**
     * TODO coments
     *
     * @return the root node of the current term
     */
    @Transient
    public CvDagObject getRoot() {
        if ( parents.size() > 0 ) {
            Iterator<CvDagObject> i = parents.iterator();
            return i.next().getRoot();
        } else {
            return this;
        }
    }
}





