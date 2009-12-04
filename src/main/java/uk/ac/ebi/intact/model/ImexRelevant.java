package uk.ac.ebi.intact.model;

import java.util.Date;

/**
 * An object that can have a imex update date.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface ImexRelevant {

    Date getLastImexUpdate();

    void setLastImexUpdate( Date lastImexUpdate );
}