package uk.ac.ebi.intact.core.persistence.dao.impl;

import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.core.persistence.dao.InstitutionDao;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for institutions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@Repository
@Transactional
@SuppressWarnings( {"unchecked"} )
public class InstitutionDaoImpl extends AnnotatedObjectDaoImpl<Institution> implements InstitutionDao {

    public InstitutionDaoImpl() {
        super( Institution.class );
    }

    public InstitutionDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Institution.class, entityManager, intactSession );
    }
}
