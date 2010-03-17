package uk.ac.ebi.intact.core.users.model;

/**
 * Identifies object having a primaryKey.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface HasIdentity {

    Long getPk();

    void setPk( Long pk );
}
