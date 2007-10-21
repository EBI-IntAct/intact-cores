package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.persistence.dao.FeatureDao;

import javax.persistence.EntityManager;

/**
 * DAO for features
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-jul-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class FeatureDaoImpl extends AnnotatedObjectDaoImpl<Feature> implements FeatureDao {

    public FeatureDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Feature.class, entityManager, intactSession );
    }
}
