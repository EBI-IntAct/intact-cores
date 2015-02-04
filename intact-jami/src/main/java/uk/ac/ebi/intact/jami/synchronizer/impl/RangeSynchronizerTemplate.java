package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultResultingSequence;
import psidev.psi.mi.jami.utils.RangeUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactRange;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactResultingSequence;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * Default synchronizer/finder for Ranges
 *
 * It does not cache persisted ranges. It only synchronize the start and end range status (with persist = true) to make sure that the start/end range status
 * are persisted before so the range can be persisted.
 * It does not synchronize the resulting sequence and its xrefs. This job is delegated to the sub classes ExperimentalRangeSynchronizer and
 * ModelledRangeSynchronizer
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

    @Override
    public Collection<I> findAll(Range object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(Range object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(I object) throws FinderException, PersisterException, SynchronizerException {
        // prepare start position
        preparePositions(object, true);

        // synchronize participant
        prepareParticipant(object, true);

        // prepare resulting sequence
        prepareResultingSequence(object, true);
    }

    protected void prepareParticipant(I object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (object.getParticipant() != null ){
            if (object.getParticipant() instanceof Participant){
                Participant synchronizedParticipant = enableSynchronization ?
                        (Participant)getParticipantSynchronizer().synchronize(object.getParticipant(), false) :
                        (Participant)getParticipantSynchronizer().convertToPersistentObject(object.getParticipant());
                object.setParticipant(synchronizedParticipant);

                // init/update resulting sequence
                if (synchronizedParticipant.getInteractor() instanceof Polymer){
                    String sequence = ((Polymer) synchronizedParticipant.getInteractor()).getSequence();
                    if (sequence != null){
                        ResultingSequence seq = object.getResultingSequence();
                        if (seq != null){
                            seq.setOriginalSequence(RangeUtils.extractRangeSequence(object, sequence));
                        }
                        else{
                            seq = new DefaultResultingSequence(RangeUtils.extractRangeSequence(object, sequence), null);
                            object.setResultingSequence(seq);
                        }
                    }
                    else if (object.getResultingSequence() != null && object.getResultingSequence().getOriginalSequence() != null){
                        object.getResultingSequence().setOriginalSequence(null);
                    }
                }
            }
            // TODO: what to do with participant set and candidates?
            else{
                throw new UnsupportedOperationException("The existing range synchronizer does not take into account entities that are not participants");
            }
        }
    }

    protected IntactDbSynchronizer getParticipantSynchronizer(){
        return getContext().getParticipantSynchronizer();
    }

    protected void prepareResultingSequence(I object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here. Delegates to sub classes
    }

    protected void preparePositions(I object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        IntactPosition start = preparePosition(object.getStart(), enableSynchronization);
        // prepare end position
        IntactPosition end = preparePosition(object.getEnd(), enableSynchronization);
        object.setPositions(start, end);
    }

    protected IntactPosition preparePosition(Position position, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        IntactPosition pos;
        if (!(position instanceof IntactPosition)){
            pos = new IntactPosition(position.getStatus(), position.getStart(), position.getEnd());
        }
        else{
            pos = (IntactPosition)position;
        }
        // prepare status
        CvTerm status = enableSynchronization ?
                getContext().getRangeStatusSynchronizer().synchronize(pos.getStatus(), true) :
                getContext().getRangeStatusSynchronizer().convertToPersistentObject(pos.getStatus());
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
        I newRange = intactClass.getConstructor(Position.class, Position.class, ResultingSequence.class).newInstance(object.getStart(), object.getEnd(), object.getResultingSequence());
        newRange.setParticipant(object.getParticipant());
        newRange.setLink(object.isLink());
        return newRange;
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

    @Override
    protected boolean containsObjectInstance(Range object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Range object) {
        // nothing to do
    }

    @Override
    protected I fetchMatchingObjectFromIdentityCache(Range object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(I object) throws SynchronizerException, PersisterException, FinderException {
        // prepare start position
        preparePositions(object, false);

        // prepare resulting sequence
        prepareResultingSequence(object, false);

        // synchronize participant
        prepareParticipant(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(Range originalObject, I persistableObject) {
        // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(Range originalObject) {
        return false;
    }

    protected void prepareXrefs(AbstractIntactResultingSequence intactObj, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here. Delegates to sub classes
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Range, I>(this));
    }
}
