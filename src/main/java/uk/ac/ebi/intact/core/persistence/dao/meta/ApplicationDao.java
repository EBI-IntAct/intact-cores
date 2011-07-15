package uk.ac.ebi.intact.core.persistence.dao.meta;

import uk.ac.ebi.intact.core.persistence.dao.IntactObjectDao;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.user.User;

import java.io.Serializable;

/**
 * User DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface ApplicationDao extends IntactObjectDao<Application>, Serializable {

    Application getByKey(String key);
}
