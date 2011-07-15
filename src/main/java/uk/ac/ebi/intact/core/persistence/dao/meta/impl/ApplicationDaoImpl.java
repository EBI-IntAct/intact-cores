package uk.ac.ebi.intact.core.persistence.dao.meta.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.core.persistence.dao.meta.ApplicationDao;
import uk.ac.ebi.intact.core.persistence.dao.meta.ApplicationPropertyDao;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.meta.ApplicationProperty;
import uk.ac.ebi.intact.model.user.User;

import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.5.0
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class ApplicationDaoImpl extends IntactObjectDaoImpl<Application> implements ApplicationDao {

    public ApplicationDaoImpl() {
        super( Application.class );
    }

    @Override
    public Application getByKey(String key) {
        final Query query = getEntityManager().createQuery( "select app from Application as app where app.key = :key" );
        query.setParameter( "key", key );
        List<Application> apps = query.getResultList();
        if ( apps.isEmpty() ) {
            return null;
        }
        return apps.get( 0 );
    }
}
