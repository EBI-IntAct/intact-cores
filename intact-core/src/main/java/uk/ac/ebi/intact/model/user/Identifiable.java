package uk.ac.ebi.intact.model.user;

/**
 * Identifies object having a primaryKey.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface Identifiable {

    Long getPk();

    void setPk( Long pk );
}
