package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.InstitutionDao;

/**
 * DAO for institutions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class InstitutionDaoImpl extends IntactObjectDaoImpl<Institution> implements InstitutionDao {

    public InstitutionDaoImpl( Session session, IntactSession intactSession ) {
        super( Institution.class, session, intactSession );
    }

    public Institution getByShortLabel( String shortLabel ) {
        return getByPropertyName( "shortLabel", shortLabel );
    }

    @Override
    public void persist( Institution objToPersist ) {
        getSession().persist( objToPersist );
    }
}
