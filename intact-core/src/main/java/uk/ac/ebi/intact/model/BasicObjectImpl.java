package uk.ac.ebi.intact.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * This class is only maintained for backwards compatibility
 *
 * @author intact team
 * @version $Id$
 */
@Deprecated
@MappedSuperclass
public abstract class BasicObjectImpl extends IntactObjectImpl implements BasicObject {


    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Institution owner;

    ///////////////////////////////////////
    // access methods for associations


    @Transient
    @Deprecated
    public Institution getOwner() {
        return owner;
    }

    @Deprecated
    public void setOwner( Institution institution ) {
        this.owner = institution;
    }
}