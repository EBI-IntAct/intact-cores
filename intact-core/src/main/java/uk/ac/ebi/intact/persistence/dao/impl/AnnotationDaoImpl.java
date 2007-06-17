package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.persistence.dao.AnnotationDao;

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

    public AnnotationDaoImpl( Session session, IntactSession intactSession ) {
        super( Annotation.class, session, intactSession );
    }


    public List<Annotation> getByTextLike( String text ) {
        return getSession().createCriteria( getEntityClass() )
                .add( Restrictions.like( "annotationText", text ) ).list();
    }
}
