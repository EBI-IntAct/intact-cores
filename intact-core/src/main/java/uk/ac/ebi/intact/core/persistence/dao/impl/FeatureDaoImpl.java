package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.FeatureDao;
import uk.ac.ebi.intact.model.Feature;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

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

    @Override
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<Feature> getByComponentAc(String componentAc) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "component" )
                .add( Restrictions.idEq(componentAc) ).list();
    }
}
