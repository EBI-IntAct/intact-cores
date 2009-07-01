package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.InstitutionDao;

import javax.persistence.EntityManager;

/**
 * DAO for institutions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class InstitutionDaoImpl extends AnnotatedObjectDaoImpl<Institution> implements InstitutionDao {

    public InstitutionDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Institution.class, entityManager, intactSession );
    }
}
