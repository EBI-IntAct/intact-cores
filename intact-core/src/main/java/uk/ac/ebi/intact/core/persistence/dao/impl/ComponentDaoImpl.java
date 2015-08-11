package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.ComponentDao;
import uk.ac.ebi.intact.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * DAO for components
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class ComponentDaoImpl extends AnnotatedObjectDaoImpl<Component> implements ComponentDao {

    public ComponentDaoImpl() {
        super (Component.class);
    }

    public ComponentDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Component.class, entityManager, intactSession );
    }


    public List<Component> getByInteractorAc( String interactorAc ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "interactor" )
                .add( Restrictions.idEq( interactorAc ) ).list();
    }

    public List<Component> getByInteractionAc( String interactionAc ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "interaction" )
                .add( Restrictions.idEq( interactionAc ) ).list();
    }

    public List<Component> getByExpressedIn(String biosourceAc) {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("expressedIn")
                .add(Restrictions.idEq(biosourceAc))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<CvExperimentalPreparation> getExperimentalPreparationsForComponentAc( String componentAc) {
        Query query = getEntityManager().createQuery("select e " +
                "from Component c join c.experimentalPreparations e " +
                "where c.ac = :componentAc ");
        query.setParameter("componentAc", componentAc);

        return query.getResultList();
    }

    @Override
    public List<CvExperimentalRole> getExperimentalRolesForComponentAc(String componentAc) {
        Query query = getEntityManager().createQuery("select e " +
                "from Component c join c.experimentalRoles e " +
                "where c.ac = :componentAc ");
        query.setParameter("componentAc", componentAc);

        return query.getResultList();
    }

    @Override
    public List<CvIdentification> getParticipantIdentificationMethodsForComponentAc(String componentAc) {
        Query query = getEntityManager().createQuery("select p " +
                "from Component c join c.participantDetectionMethods p " +
                "where c.ac = :componentAc ");
        query.setParameter("componentAc", componentAc);

        return query.getResultList();
    }
}
