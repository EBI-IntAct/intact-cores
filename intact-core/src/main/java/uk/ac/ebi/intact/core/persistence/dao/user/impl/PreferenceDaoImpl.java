package uk.ac.ebi.intact.core.persistence.dao.user.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.core.persistence.dao.user.PreferenceDao;
import uk.ac.ebi.intact.model.user.Preference;

import javax.persistence.Query;
import java.util.Collection;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class PreferenceDaoImpl extends IntactObjectDaoImpl<Preference> implements PreferenceDao {

    public PreferenceDaoImpl() {
        super( Preference.class );
    }

    @Override
    public Collection<Preference> getByUserAc(String ac) {
        Query query = getEntityManager().createQuery("select pref from Preference pref where pref.user.ac = :ac");
        query.setParameter("ac", ac);

        return query.getResultList();
    }
}
