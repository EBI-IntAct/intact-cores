package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Range;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;

/**
 * Default synchronizer/finder for Ranges
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactRangeSynchronizer implements IntactDbSynchronizer<Range>{
    private IntactDbSynchronizer<CvTerm> statusSynchronizer;
    private EntityManager entityManager;
    private static final Log log = LogFactory.getLog(IntactRangeSynchronizer.class);

    public IntactRangeSynchronizer(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("Xref synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        this.statusSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.RANGE_STATUS_OBJCLASS);
    }

    public IntactRangeSynchronizer(EntityManager entityManager, IntactDbSynchronizer<CvTerm> statusSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Xref synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        this.statusSynchronizer = statusSynchronizer != null ? statusSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.RANGE_STATUS_OBJCLASS);
    }

    public Range find(Range object) throws FinderException {
        return null;
    }

    public Range persist(Range object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((IntactRange) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Range object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((IntactRange)object);
    }

    public Range synchronize(Range object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!(object instanceof IntactRange)){
            IntactRange newRange = new IntactRange(object.getStart(), object.getEnd(), object.isLink(), object.getResultingSequence());

            // synchronize properties
            synchronizeProperties(newRange);
            if (persist){
                this.entityManager.persist(newRange);
            }
            return newRange;
        }
        else{
            IntactRange intactType = (IntactRange)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // synchronize properties
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    return this.entityManager.merge(intactType);
                }
                else{
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // synchronize properties
                synchronizeProperties(intactType);
                // persist alias
                if (persist){
                    this.entityManager.persist(intactType);
                }
                return intactType;
            }
            else{
                // synchronize properties
                synchronizeProperties(intactType);
                return intactType;
            }
        }
    }

    public void clearCache() {
        this.statusSynchronizer.clearCache();
    }

    protected void synchronizeProperties(IntactRange object) throws PersisterException, SynchronizerException, FinderException {
        // prepare start position
        IntactPosition start;
        if (!(object.getStart() instanceof IntactPosition)){
            start = new IntactPosition(object.getStart().getStatus(), object.getStart().getStart(), object.getStart().getEnd());
        }
        else{
            start = (IntactPosition)object.getStart();
        }
        // prepare start status
        CvTerm startStatus = this.statusSynchronizer.synchronize(start.getStatus(), true, true);
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
        CvTerm endStatus = this.statusSynchronizer.synchronize(end.getStatus(), true, true);
        end.setStatus(endStatus);
        // reset positions
        object.setPositions(start, end);
    }
}
