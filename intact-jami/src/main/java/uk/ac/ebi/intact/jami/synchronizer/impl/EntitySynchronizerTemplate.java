package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.EntityMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;
import uk.ac.ebi.intact.jami.model.extension.IntactStoichiometry;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

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

public class EntitySynchronizerTemplate<T extends Entity, I extends AbstractIntactEntity> extends AbstractIntactDbSynchronizer<T, I>
implements EntitySynchronizer<T,I> {
    private Map<T, I> persistedObjects;

    private static final Log log = LogFactory.getLog(EntitySynchronizerTemplate.class);
    private IntactDbSynchronizer featureSynchronizer;

    public EntitySynchronizerTemplate(SynchronizerContext context, Class<I> intactClass){
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

    public I persist(I object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        I persisted = super.persist(object);
        this.persistedObjects.put((T)object, persisted);

        return persisted;
    }

    @Override
    public I synchronize(T object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        I org = super.synchronize(object, persist);
        this.persistedObjects.put(object, org);
        return org;
    }

    public void synchronizeProperties(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareShortLabel(intactEntity);
        // then check interactor
        prepareInteractor(intactEntity);
        // then check stoichiometry
        prepareStoichiometry(intactEntity);
        // then check features
        prepareFeatures(intactEntity);
        // then check aliases
        prepareAliases(intactEntity);
        // then check annotations
        prepareAnnotations(intactEntity);
        // then check xrefs
        prepareXrefs(intactEntity);
        // then check causal relationships
        prepareCausalRelationships(intactEntity);
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

    protected void prepareCausalRelationships(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areCausalRelationshipsInitialized()){
            List<CausalRelationship> relationshipsToPersist = new ArrayList<CausalRelationship>(intactEntity.getCausalRelationships());
            for (CausalRelationship causalRelationship : relationshipsToPersist){
                // do not persist or merge causalRelationship because of cascades
                CausalRelationship persistentRelationship = getContext().getCausalRelationshipSynchronizer().synchronize(causalRelationship, false);
                // we have a different instance because needed to be synchronized
                if (persistentRelationship != causalRelationship){
                    intactEntity.getCausalRelationships().remove(causalRelationship);
                    intactEntity.getCausalRelationships().add(persistentRelationship);
                }
            }
        }
    }

    protected void prepareStoichiometry(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        Stoichiometry stc = intactEntity.getStoichiometry();
        if (stc != null && !(stc instanceof IntactStoichiometry)){
            intactEntity.setStoichiometry(new IntactStoichiometry(stc.getMinValue(), stc.getMaxValue()));
        }
    }

    protected void prepareExperimentalPreparations(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
       CvTerm bioRole = intactEntity.getBiologicalRole();
       intactEntity.setBiologicalRole(getContext().getBiologicalRoleSynchronizer().synchronize(bioRole, true));
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


    protected void prepareXrefs(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactEntity.getXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref persistentXref = getContext().getEntityXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (persistentXref != xref){
                    intactEntity.getXrefs().remove(xref);
                    intactEntity.getXrefs().add(persistentXref);
                }
            }
        }
    }

    protected void prepareAnnotations(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactEntity.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation persistentAnnotation = getContext().getEntityAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (persistentAnnotation != annotation){
                    intactEntity.getAnnotations().remove(annotation);
                    intactEntity.getAnnotations().add(persistentAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactEntity.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias persistentAlias = getContext().getEntityAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (persistentAlias != alias){
                    intactEntity.getAliases().remove(alias);
                    intactEntity.getAliases().add(persistentAlias);
                }
            }
        }
    }

    protected void prepareInteractor(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        // persist interactor if not there
        Interactor interactor = intactEntity.getInteractor();
        intactEntity.setInteractor(getContext().getInteractorSynchronizer().synchronize(interactor, true));
    }

    protected void prepareShortLabel(I intactParticipant) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactParticipant.getShortLabel().length()){
            log.warn("Participant shortLabel too long: "+intactParticipant.getShortLabel()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactParticipant.setShortLabel(intactParticipant.getShortLabel().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new EntityMergerEnrichOnly<T,I,Feature>());
    }

    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getFeatureSynchronizer();
    }
}
