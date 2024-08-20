package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.InstitutionDao;
import uk.ac.ebi.intact.model.Institution;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

/**
 * DAO for institutions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class InstitutionDaoImpl extends AnnotatedObjectDaoImpl<Institution> implements InstitutionDao {

    public InstitutionDaoImpl() {
        super( Institution.class );
    }

    public InstitutionDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Institution.class, entityManager, intactSession );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Institution getByAc( String ac ) {
        Query query = getEntityManager().createQuery("select i from Institution i left join fetch i.xrefs " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);

        List<Institution> results = query.getResultList();

        if (results.size() > 0) {
            return results.iterator().next();
        }

        return null;
    }
}
