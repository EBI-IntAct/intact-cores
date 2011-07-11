package uk.ac.ebi.intact.core.persistence.dao.user;

import uk.ac.ebi.intact.core.persistence.dao.IntactObjectDao;
import uk.ac.ebi.intact.model.user.Preference;

import java.io.Serializable;
import java.util.Collection;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface PreferenceDao extends IntactObjectDao<Preference> {

    Collection<Preference> getByUserAc(String ac);
}
