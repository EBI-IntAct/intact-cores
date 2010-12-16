package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.AnnotationDao;
import uk.ac.ebi.intact.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for annotations
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class AnnotationDaoImpl extends IntactObjectDaoImpl<Annotation> implements AnnotationDao {

    public AnnotationDaoImpl() {
        super(Annotation.class);
    }

    public AnnotationDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Annotation.class, entityManager, intactSession );
    }


    public List<Annotation> getByTextLike( String text ) {
        return getSession().createCriteria( getEntityClass() )
                .add( Restrictions.like( "annotationText", text ) ).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnnotatedObject> getParentsWithAnnotationAc(String annotationAc) {
        String[] aoClassNames = {Publication.class.getName(), Experiment.class.getName(),
                Interactor.class.getName(), Component.class.getName(), BioSource.class.getName(),
                CvObject.class.getName()};

        List<AnnotatedObject> annotatedObjects = new ArrayList<AnnotatedObject>();

        for (String aoClassName : aoClassNames) {
            Query query = getEntityManager().createQuery("select ao from " + aoClassName + " ao join ao.annotations as annot " +
                    "where annot.ac = :annotAc");
            query.setParameter("annotAc", annotationAc);

            annotatedObjects.addAll(query.getResultList());
        }

        return annotatedObjects;
    }
}
