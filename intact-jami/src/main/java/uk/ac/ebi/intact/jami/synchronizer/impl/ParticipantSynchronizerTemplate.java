package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ParticipantMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParticipant;
import uk.ac.ebi.intact.jami.model.extension.IntactStoichiometry;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Synchronizer for entities and participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class ParticipantSynchronizerTemplate<T extends Participant, I extends AbstractIntactParticipant> extends AbstractIntactDbSynchronizer<T, I>
implements ParticipantSynchronizer<T,I> {
    private Map<T, I> persistedObjects;

    private static final Log log = LogFactory.getLog(ParticipantSynchronizerTemplate.class);

    public ParticipantSynchronizerTemplate(SynchronizerContext context, Class<I> intactClass){
        super(context, intactClass);
        // to keep track of persisted cvs
        this.persistedObjects = new IdentityMap();
    }

    public I find(T term) throws FinderException {
        if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        else{
            return null;
        }
    }

    public void synchronizeProperties(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        // then check interactor
        prepareInteractor(intactEntity);
        // then check stoichiometry
        prepareStoichiometry(intactEntity);
        // then check features
        prepareFeatures(intactEntity);
        // prepare biological role
        prepareBiologicalRole(intactEntity);
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected I instantiateNewPersistentInstance(T object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newParticipant = intactClass.getConstructor(Interactor.class).newInstance(object.getInteractor());
        ParticipantCloner.copyAndOverrideBasicParticipantProperties(object, newParticipant, false);
        return newParticipant;
    }

    @Override
    protected void storeInCache(T originalObject, I persistentObject, I existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected I fetchObjectFromCache(T object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(T object) {
        return this.persistedObjects.containsKey(object);
    }

    protected void prepareStoichiometry(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        Stoichiometry stc = intactEntity.getStoichiometry();
        if (stc != null && !(stc instanceof IntactStoichiometry)){
            intactEntity.setStoichiometry(new IntactStoichiometry(stc.getMinValue(), stc.getMaxValue()));
        }
    }

    protected void prepareFeatures(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areFeaturesInitialized()) {
            List<Feature> featuresToPersist = new ArrayList<Feature>(intactEntity.getFeatures());
            for (Feature feature : featuresToPersist) {
                feature.setParticipant(intactEntity);
                // do not persist or merge features because of cascades
                Feature persistentFeature = (Feature)getFeatureSynchronizer().synchronize(feature, false);
                // we have a different instance because needed to be synchronized
                if (persistentFeature != feature) {
                    intactEntity.getFeatures().remove(feature);
                    intactEntity.addFeature(persistentFeature);
                }
            }
        }
    }

    protected void prepareInteractor(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        // persist interactor if not there
        Interactor interactor = intactEntity.getInteractor();
        intactEntity.setInteractor(getContext().getInteractorSynchronizer().synchronize(interactor, true));
    }

    protected void prepareBiologicalRole(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        // persist biological role if not here
        CvTerm role = intactEntity.getBiologicalRole();
        intactEntity.setBiologicalRole(getContext().getBiologicalRoleSynchronizer().synchronize(role, true));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ParticipantMergerEnrichOnly<T,I,Feature>());
    }

    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getFeatureSynchronizer();
    }
}
