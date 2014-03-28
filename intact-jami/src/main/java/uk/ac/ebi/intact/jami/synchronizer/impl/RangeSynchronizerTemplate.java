package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactRange;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactResultingSequence;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Default synchronizer/finder for Ranges
 *
 * It does not cache persisted ranges. It only synchronize the start and end range status (with persist = true) to make sure that the start/end range status
 * are persisted before so the range can be persisted.
 * It also synchronize the resulting sequence and its xrefs.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class RangeSynchronizerTemplate<I extends AbstractIntactRange> extends AbstractIntactDbSynchronizer<Range, I> {
    private static final Log log = LogFactory.getLog(RangeSynchronizerTemplate.class);

    public RangeSynchronizerTemplate(SynchronizerContext context, Class<? extends I> intactClass){
        super(context, intactClass);
    }

    public I find(Range object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(I object) throws FinderException, PersisterException, SynchronizerException {
        // prepare start position
        preparePositions(object);

        // prepare resulting sequence
        prepareResultingSequence(object);

        // synchronize participant
        if (object.getParticipant() != null){
            Participant synchronizedParticipant = getContext().getParticipantSynchronizer().synchronize(object.getParticipant(), false);
            object.setParticipant(synchronizedParticipant);
        }
    }

    protected void prepareResultingSequence(I object) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here. Delegates to sub classes
    }

    protected void preparePositions(I object) throws FinderException, PersisterException, SynchronizerException {
        IntactPosition start = preparePosition(object.getStart());
        // prepare end position
        IntactPosition end = preparePosition(object.getEnd());
        object.setPositions(start, end);
    }

    protected IntactPosition preparePosition(Position position) throws FinderException, PersisterException, SynchronizerException {
        IntactPosition pos;
        if (!(position instanceof IntactPosition)){
            pos = new IntactPosition(position.getStatus(), position.getStart(), position.getEnd());
        }
        else{
            pos = (IntactPosition)position;
        }
        // prepare status
        CvTerm status = getContext().getRangeStatusSynchronizer().synchronize(pos.getStatus(), true);
        pos.setStatus(status);
        return pos;
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected I instantiateNewPersistentInstance(Range object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(Position.class, Position.class, Boolean.class, ResultingSequence.class).newInstance(object.getStart(), object.getEnd(), object.isLink(), object.getResultingSequence());
    }

    @Override
    protected void storeInCache(Range originalObject, I persistentObject, I existingInstance) {
        // nothing to do
    }

    @Override
    protected I fetchObjectFromCache(Range object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Range object) {
        return false;
    }

    protected void prepareXrefs(AbstractIntactResultingSequence intactObj) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here. Delegates to sub classes
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Range, I>(this));
    }
}
