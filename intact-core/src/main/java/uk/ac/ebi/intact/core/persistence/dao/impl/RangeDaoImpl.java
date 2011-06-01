package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.RangeDao;
import uk.ac.ebi.intact.model.Range;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * DAO for ranges
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
public class RangeDaoImpl extends IntactObjectDaoImpl<Range> implements RangeDao {

    public RangeDaoImpl() {
        super(Range.class);
    }

    public RangeDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Range.class, entityManager, intactSession );
    }

    @Override
    public List<Range> getByFeatureAc(String featureAc) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "feature" )
                .add( Restrictions.idEq(featureAc) ).list();
    }
}
