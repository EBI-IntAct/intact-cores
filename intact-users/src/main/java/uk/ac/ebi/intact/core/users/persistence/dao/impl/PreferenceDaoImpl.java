package uk.ac.ebi.intact.core.users.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.users.model.Preference;
import uk.ac.ebi.intact.core.users.persistence.dao.PreferenceDao;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class PreferenceDaoImpl extends UsersBaseDaoImpl<Preference> implements PreferenceDao {

    public PreferenceDaoImpl() {
        super( Preference.class );
    }

}
