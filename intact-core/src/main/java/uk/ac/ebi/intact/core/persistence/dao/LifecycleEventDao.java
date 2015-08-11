package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.model.LifecycleEvent;

import java.util.List;

/**
 * LifecycleEvent DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public interface LifecycleEventDao extends BaseDao<LifecycleEvent> {

    List<LifecycleEvent> getByPublicationAc( String publicationAc );

}
