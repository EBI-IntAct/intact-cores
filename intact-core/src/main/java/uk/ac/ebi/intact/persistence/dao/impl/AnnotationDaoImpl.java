package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.persistence.dao.AnnotationDao;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * DAO for annotations
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class AnnotationDaoImpl extends IntactObjectDaoImpl<Annotation> implements AnnotationDao {

    public AnnotationDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Annotation.class, entityManager, intactSession );
    }


    public List<Annotation> getByTextLike( String text ) {
        return getSession().createCriteria( getEntityClass() )
                .add( Restrictions.like( "annotationText", text ) ).list();
    }
}
