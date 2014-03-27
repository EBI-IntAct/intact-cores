package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.CuratedPublicationMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.model.extension.PublicationXref;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default synchronizer for publications
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class CuratedPublicationSynchronizer extends PublicationSynchronizer<IntactCuratedPublication>{

    private static final Log log = LogFactory.getLog(CuratedPublicationSynchronizer.class);

    public CuratedPublicationSynchronizer(SynchronizerContext context){
        super(context, IntactCuratedPublication.class);
    }

    public void synchronizeProperties(IntactCuratedPublication intactPublication) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactPublication);
        super.synchronizeProperties(intactPublication);
        // then check source
        prepareSource(intactPublication);
        // then check experiments
        prepareExperiments(intactPublication);
        // then prepare users
        prepareStatusAndCurators(intactPublication);
        // then check publication lifecycle
        prepareLifeCycleEvents(intactPublication);
    }

    protected void prepareStatusAndCurators(IntactCuratedPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {
        // first the status
        CvTerm status = intactPublication.getStatus() != null ? intactPublication.getStatus() : IntactUtils.createLifecycleStatus(LifeCycleEvent.NEW_STATUS);
        intactPublication.setStatus(getContext().getLifecycleStatusSynchronizer().synchronize(status, true));

        // then curator
        User curator = intactPublication.getCurrentOwner();
        // do not persist user if not there
        if (curator != null){
            intactPublication.setCurrentOwner(getContext().getUserReadOnlySynchronizer().synchronize(curator, false));
        }

        // then reviewer
        User reviewer = intactPublication.getCurrentReviewer();
        if (reviewer != null){
            intactPublication.setCurrentReviewer(getContext().getUserReadOnlySynchronizer().synchronize(reviewer, false));
        }
    }

    @Override
    protected IntactCuratedPublication instantiateNewPersistentInstance(Publication object, Class<? extends IntactCuratedPublication> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactCuratedPublication pub = new IntactCuratedPublication();
        PublicationCloner.copyAndOverridePublicationPropertiesAndExperiments(object, pub);
        return pub;
    }

    protected void prepareSource(IntactCuratedPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {
        Source source = intactPublication.getSource();
        if (source != null){
            intactPublication.setSource(getContext().getSourceSynchronizer().synchronize(source, true));
        }
    }

    protected void prepareLifeCycleEvents(IntactCuratedPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {

        if (intactPublication.areLifecycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactPublication.getLifecycleEvents());
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = getContext().getPublicationLifecycleSynchronizer().synchronize(event, false);
                // we have a different instance because needed to be synchronized
                if (evt != event){
                    intactPublication.getLifecycleEvents().add(intactPublication.getLifecycleEvents().indexOf(event), evt);
                    intactPublication.getLifecycleEvents().remove(event);
                }
            }
        }
    }

    protected void prepareExperiments(IntactCuratedPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {
        if (intactPublication.areExperimentsInitialized()){
            List<Experiment> experimentToPersist = new ArrayList<Experiment>(intactPublication.getExperiments());
            for (Experiment experiment : experimentToPersist){
                // do not persist or merge experiments because of cascades
                Experiment pubExperiment = getContext().getExperimentSynchronizer().synchronize(experiment, false);
                // we have a different instance because needed to be synchronized
                if (pubExperiment != experiment){
                    intactPublication.removeExperiment(experiment);
                    intactPublication.addExperiment(pubExperiment);
                }
            }
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactCuratedPublication intactPublication) throws SynchronizerException {
        // first initialise shortlabel if not done
        String pubmed = intactPublication.getPubmedId();
        String doi = intactPublication.getDoi();
        if (pubmed != null ){
            intactPublication.setShortLabel(pubmed);
        }
        else if (doi != null){
            intactPublication.setShortLabel(doi);
        }
        else if (!intactPublication.getIdentifiers().isEmpty()){
            intactPublication.setShortLabel(intactPublication.getIdentifiers().iterator().next().getId());
        }
        else {
            // create unassigned pubmed id
            SequenceManager seqManager = ApplicationContextProvider.getBean("sequenceManager");
            if (seqManager == null){
                throw new SynchronizerException("The publication synchronizer needs a sequence manager to automatically generate a unassigned pubmed identifier for backward compatibility. No sequence manager bean " +
                        "was found in the spring context.");
            }
            seqManager.createSequenceIfNotExists(IntactUtils.UNASSIGNED_SEQ, 1);
            String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactUtils.UNASSIGNED_SEQ));
            String identifier = "unassigned" + nextIntegerAsString;
            // set identifier
            intactPublication.setShortLabel(identifier);
            // add xref
            intactPublication.getIdentifiers().add(new PublicationXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), identifier, IntactUtils.createMIQualifier(Xref.PRIMARY, Xref.PRIMARY_MI)));
        }
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactPublication.getShortLabel().length()){
            log.warn("Publication shortLabel too long: "+intactPublication.getShortLabel()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactPublication.setShortLabel(intactPublication.getShortLabel().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new CuratedPublicationMergerEnrichOnly(this));
    }
}
