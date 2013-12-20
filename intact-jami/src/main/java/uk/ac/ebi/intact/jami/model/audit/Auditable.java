package uk.ac.ebi.intact.jami.model.audit;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface for objects having basic audit methods
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/12/13</pre>
 */

public interface Auditable extends Serializable {

    /**
     *
     * @return the created date of the object
     */
    public Date getCreated();

    /**
     *
     * @param created
     */
    public void setCreated( java.util.Date created );

    /**
     *
     * @return The updated date of the object
     */
    public Date getUpdated();

    /**
     *
     * @param updated
     */
    public void setUpdated( java.util.Date updated );

    /**
     * The creator userstamp of this object
     * @return
     */
    public String getCreator();

    /**
     *
     * @param createdUser
     */
    public void setCreator( String createdUser );

    /**
     *
     * @return The updator userstamp of this object
     */
    public String getUpdator();

    /**
     *
     * @param userStamp
     */
    public void setUpdator( String userStamp );
}
