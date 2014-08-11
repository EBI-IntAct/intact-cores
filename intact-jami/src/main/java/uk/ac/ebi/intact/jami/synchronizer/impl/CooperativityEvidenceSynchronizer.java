package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CooperativityEvidence;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.CooperativityEvidenceCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCooperativityEvidence;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Default finder/synchronizer for cooperativity evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class CooperativityEvidenceSynchronizer extends AbstractIntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence>{

    public CooperativityEvidenceSynchronizer(SynchronizerContext context){
        super(context, IntactCooperativityEvidence.class);
    }

    public IntactCooperativityEvidence find(CooperativityEvidence object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCooperativityEvidence object) throws FinderException, PersisterException, SynchronizerException {
        // publication first
        synchronizePublication(object, true);

        // check evidence methodse
        prepareEvidenceMethods(object, true);
    }

    protected void synchronizePublication(IntactCooperativityEvidence object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        Publication pub = object.getPublication();
        object.setPublication(enableSynchronization ?
                getContext().getPublicationSynchronizer().synchronize(pub, true) :
                getContext().getPublicationSynchronizer().convertToPersistentObject(pub));
    }

    public void clearCache() {
        // nothing to do
    }

    protected void prepareEvidenceMethods(IntactCooperativityEvidence object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (object.areEvidenceMethodsInitialized()){
            Collection<CvTerm> parametersToPersist = new ArrayList<CvTerm>(object.getEvidenceMethods());
            for (CvTerm param : parametersToPersist){
                CvTerm expParam = enableSynchronization ?
                        getContext().getTopicSynchronizer().synchronize(param, true) :
                        getContext().getTopicSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getEvidenceMethods().remove(param);
                    object.getEvidenceMethods().add(expParam);
                }
            }
        }
    }

    @Override
    protected Object extractIdentifier(IntactCooperativityEvidence object) {
        return object.getId();
    }

    @Override
    protected IntactCooperativityEvidence instantiateNewPersistentInstance(CooperativityEvidence object, Class<? extends IntactCooperativityEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactCooperativityEvidence ev = new IntactCooperativityEvidence(object.getPublication());
        CooperativityEvidenceCloner.copyAndOverrideCooperativityEvidenceProperties(object, ev);
        return ev;
    }

    @Override
    protected void storeInCache(CooperativityEvidence originalObject, IntactCooperativityEvidence persistentObject, IntactCooperativityEvidence existingInstance) {
        // nothing to do
    }

    @Override
    protected IntactCooperativityEvidence fetchObjectFromCache(CooperativityEvidence object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(CooperativityEvidence object) {
        return false;
    }

    @Override
    protected boolean containsDetachedOrTransientObject(CooperativityEvidence object) {
        return false;
    }

    @Override
    protected IntactCooperativityEvidence fetchMatchingPersistableObject(CooperativityEvidence object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(IntactCooperativityEvidence object) throws SynchronizerException, PersisterException, FinderException {
        // publication first
        synchronizePublication(object, false);

        // check evidence methodse
        prepareEvidenceMethods(object, false);
    }

    @Override
    protected void storeDetachedOrTransientObjectInCache(CooperativityEvidence originalObject, IntactCooperativityEvidence persistableObject) {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<CooperativityEvidence, IntactCooperativityEvidence>(this));
    }
}
