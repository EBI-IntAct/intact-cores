package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.FeatureDao;
import uk.ac.ebi.intact.model.Feature;

import javax.persistence.EntityManager;

/**
 * DAO for features
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-jul-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class FeatureDaoImpl extends AnnotatedObjectDaoImpl<Feature> implements FeatureDao {

    public FeatureDaoImpl() {
        super(Feature.class);
    }

    public FeatureDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Feature.class, entityManager, intactSession );
    }
}
