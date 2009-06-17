package uk.ac.ebi.intact.core.persistence.dao.impl;

import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.model.Range;
import uk.ac.ebi.intact.core.persistence.dao.RangeDao;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for ranges
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@Repository
@Transactional
public class RangeDaoImpl extends IntactObjectDaoImpl<Range> implements RangeDao {

    public RangeDaoImpl() {
        super(Range.class);
    }

    public RangeDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( Range.class, entityManager, intactSession );
    }
}
