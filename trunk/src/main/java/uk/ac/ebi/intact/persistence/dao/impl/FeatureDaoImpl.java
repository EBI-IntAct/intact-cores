package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.persistence.dao.FeatureDao;

/**
 * DAO for features
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-jul-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class FeatureDaoImpl extends AnnotatedObjectDaoImpl<Feature> implements FeatureDao {

    public FeatureDaoImpl( Session session, IntactSession intactSession ) {
        super( Feature.class, session, intactSession );
    }
}
