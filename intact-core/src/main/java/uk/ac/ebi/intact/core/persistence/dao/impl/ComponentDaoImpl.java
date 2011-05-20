package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.ComponentDao;
import uk.ac.ebi.intact.model.Component;

import javax.persistence.EntityManager;
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
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "expressedIn" )
                .add( Restrictions.idEq( biosourceAc ) ).list();
    }
}
