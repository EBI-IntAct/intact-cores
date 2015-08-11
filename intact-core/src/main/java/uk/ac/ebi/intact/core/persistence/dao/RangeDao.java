package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Range;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Jul-2006</pre>
 */
@Mockable
public interface RangeDao extends IntactObjectDao<Range> {
     List<Range> getByFeatureAc( String featureAc );
}
