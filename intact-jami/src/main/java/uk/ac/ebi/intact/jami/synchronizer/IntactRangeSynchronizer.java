package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Position;
import psidev.psi.mi.jami.model.Range;
import psidev.psi.mi.jami.model.ResultingSequence;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Default synchronizer/finder for Ranges
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactRangeSynchronizer extends AbstractIntactDbSynchronizer<Range, IntactRange>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer;
    private static final Log log = LogFactory.getLog(IntactRangeSynchronizer.class);

    public IntactRangeSynchronizer(EntityManager entityManager){
        super(entityManager, IntactRange.class);
        this.statusSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.RANGE_STATUS_OBJCLASS);
    }

    public IntactRangeSynchronizer(EntityManager entityManager, IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer){
        super(entityManager, IntactRange.class);
        this.statusSynchronizer = statusSynchronizer != null ? statusSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.RANGE_STATUS_OBJCLASS);
    }

    public IntactRange find(Range object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactRange object) throws FinderException, PersisterException, SynchronizerException {
        // prepare start position
        IntactPosition start;
        if (!(object.getStart() instanceof IntactPosition)){
            start = new IntactPosition(object.getStart().getStatus(), object.getStart().getStart(), object.getStart().getEnd());
        }
        else{
            start = (IntactPosition)object.getStart();
        }
        // prepare start status
        CvTerm startStatus = this.statusSynchronizer.synchronize(start.getStatus(), true);
        start.setStatus(startStatus);

        // prepare end position
        IntactPosition end;
        if (!(object.getEnd() instanceof IntactPosition)){
            end = new IntactPosition(object.getEnd().getStatus(), object.getEnd().getStart(), object.getEnd().getEnd());
        }
        else{
            end = (IntactPosition)object.getEnd();
        }
        // prepare end status
        CvTerm endStatus = this.statusSynchronizer.synchronize(end.getStatus(), true);
        end.setStatus(endStatus);
        // reset positions
        object.setPositions(start, end);
    }

    public void clearCache() {
        this.statusSynchronizer.clearCache();
    }

    @Override
    protected Object extractIdentifier(IntactRange object) {
        return object.getAc();
    }

    @Override
    protected IntactRange instantiateNewPersistentInstance(Range object, Class<? extends IntactRange> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(Position.class, Position.class, Boolean.class, ResultingSequence.class).newInstance(object.getStart(), object.getEnd(), object.isLink(), object.getResultingSequence());
    }
}
