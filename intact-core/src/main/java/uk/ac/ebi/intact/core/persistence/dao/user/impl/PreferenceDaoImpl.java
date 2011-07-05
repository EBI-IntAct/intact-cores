package uk.ac.ebi.intact.core.persistence.dao.user.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.persistence.dao.user.PreferenceDao;
import uk.ac.ebi.intact.model.user.Preference;

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
