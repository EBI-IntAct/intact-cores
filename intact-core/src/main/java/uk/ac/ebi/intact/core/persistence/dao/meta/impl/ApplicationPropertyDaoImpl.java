package uk.ac.ebi.intact.core.persistence.dao.meta.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.core.persistence.dao.meta.ApplicationPropertyDao;
import uk.ac.ebi.intact.core.persistence.dao.user.PreferenceDao;
import uk.ac.ebi.intact.model.meta.ApplicationProperty;
import uk.ac.ebi.intact.model.user.Preference;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.5.0
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class ApplicationPropertyDaoImpl extends IntactObjectDaoImpl<ApplicationProperty> implements ApplicationPropertyDao {

    public ApplicationPropertyDaoImpl() {
        super( ApplicationProperty.class );
    }

    @Override
    public List<ApplicationProperty> getByApplicationAc(String ac) {
        Query query = getEntityManager().createQuery("select ap from ApplicationProperty ap where ap.application.ac = :ac");
        query.setParameter("ac", ac);

        return query.getResultList();
    }
}
