package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.merger.IntactEntityMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
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

public class IntactEntityBaseSynchronizer<T extends Entity, I extends AbstractIntactEntity> extends AbstractIntactDbSynchronizer<T, I>{
    private Map<T, I> persistedObjects;

    private IntactDbSynchronizer<Alias, EntityAlias> aliasSynchronizer;
    private IntactDbSynchronizer<Annotation, EntityAnnotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref, EntityXref> xrefSynchronizer;

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> biologicalRoleSynchronizer;
    private IntactDbSynchronizer<Feature, AbstractIntactFeature> featureSynchronizer;
    private IntactDbSynchronizer<CausalRelationship, IntactCausalRelationship> causalRelationshipSynchronizer;
    private IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer;

    private static final Log log = LogFactory.getLog(IntactEntityBaseSynchronizer.class);

    public IntactEntityBaseSynchronizer(EntityManager entityManager, Class<I> intactClass){
        super(entityManager, intactClass);
        // to keep track of persisted cvs
        this.persistedObjects = new IdentityMap();
        this.aliasSynchronizer = new IntactAliasSynchronizer<EntityAlias>(entityManager, EntityAlias.class);
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer<EntityAnnotation>(entityManager, EntityAnnotation.class);
        this.xrefSynchronizer = new IntactXrefSynchronizer<EntityXref>(entityManager, EntityXref.class);

        this.biologicalRoleSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.BIOLOGICAL_ROLE_OBJCLASS);
        initialiseFeatureSynchronizer(entityManager);
        this.causalRelationshipSynchronizer = new IntactCausalRelationchipSynchronizer(entityManager);
        this.interactorSynchronizer = new IntactInteractorSynchronizer(entityManager);
    }

    public IntactEntityBaseSynchronizer(EntityManager entityManager, Class<I> intactClass,
                                        IntactDbSynchronizer<Alias, EntityAlias> aliasSynchronizer,
                                        IntactDbSynchronizer<Annotation, EntityAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, EntityXref> xrefSynchronizer,
                                        IntactDbSynchronizer<CvTerm, IntactCvTerm> biologicalRoleSynchronizer,
                                        IntactDbSynchronizer<Feature, AbstractIntactFeature> featureSynchronizer,
                                        IntactDbSynchronizer<CausalRelationship, IntactCausalRelationship> causalRelationshipSynchronizer,
                                        IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer){
        super(entityManager, intactClass);
        // to keep track of persisted cvs
        this.persistedObjects = new IdentityMap();
        this.aliasSynchronizer = aliasSynchronizer != null ? aliasSynchronizer : new IntactAliasSynchronizer<EntityAlias>(entityManager, EntityAlias.class);
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer<EntityAnnotation>(entityManager, EntityAnnotation.class);
        this.xrefSynchronizer = xrefSynchronizer != null? xrefSynchronizer : new IntactXrefSynchronizer<EntityXref>(entityManager, EntityXref.class);

        this.biologicalRoleSynchronizer = biologicalRoleSynchronizer != null ? biologicalRoleSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.BIOLOGICAL_ROLE_OBJCLASS);
        if (featureSynchronizer == null){
            initialiseFeatureSynchronizer(entityManager);
        }
        else{
            this.featureSynchronizer = featureSynchronizer;
        }
        this.causalRelationshipSynchronizer = causalRelationshipSynchronizer != null ? causalRelationshipSynchronizer : new IntactCausalRelationchipSynchronizer(entityManager);
        this.interactorSynchronizer = interactorSynchronizer != null ? interactorSynchronizer : new IntactInteractorSynchronizer(entityManager);

    }

    public I find(T term) throws FinderException{
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
        prepareAndSynchronizeShortLabel(intactEntity);
        // then check interactor
        prepareInteractor(intactEntity);
        // then check organism
        prepareBiologicalRole(intactEntity);
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
        this.aliasSynchronizer.clearCache();
        this.xrefSynchronizer.clearCache();
        this.annotationSynchronizer.clearCache();
        this.biologicalRoleSynchronizer.clearCache();
        this.causalRelationshipSynchronizer.clearCache();
        this.featureSynchronizer.clearCache();
        this.interactorSynchronizer.clearCache();
    }

    protected void initialiseFeatureSynchronizer(EntityManager entityManager) {
        this.featureSynchronizer = new IntactFeatureSynchronizer(entityManager);
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
                CausalRelationship persistentRelationship = this.causalRelationshipSynchronizer.synchronize(causalRelationship, false);
                // we have a different instance because needed to be synchronized
                if (persistentRelationship != causalRelationship){
                    intactEntity.getCausalRelationships().remove(causalRelationship);
                    intactEntity.getCausalRelationships().add(persistentRelationship);
                }
            }
        }
    }

    protected void prepareStoichiometry(I intactEntity) {
        Stoichiometry stc = intactEntity.getStoichiometry();
        if (stc != null && !(stc instanceof IntactStoichiometry)){
            intactEntity.setStoichiometry(new IntactStoichiometry(stc.getMinValue(), stc.getMaxValue()));
        }
    }

    protected void prepareBiologicalRole(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
       CvTerm bioRole = intactEntity.getBiologicalRole();
       intactEntity.setBiologicalRole(this.biologicalRoleSynchronizer.synchronize(bioRole, true));
    }

    protected void prepareFeatures(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areFeaturesInitialized()){
            List<Feature> featuresToPersist = new ArrayList<Feature>(intactEntity.getFeatures());
            for (Feature feature : featuresToPersist){
                feature.setParticipant(intactEntity);
                // do not persist or merge features because of cascades
                Feature persistentFeature = this.featureSynchronizer.synchronize(feature, false);
                // we have a different instance because needed to be synchronized
                if (persistentFeature != feature){
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
                Xref persistentXref = this.xrefSynchronizer.synchronize(xref, false);
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
                Annotation persistentAnnotation = this.annotationSynchronizer.synchronize(annotation, false);
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
                Alias persistentAlias = this.aliasSynchronizer.synchronize(alias, false);
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
        intactEntity.setInteractor(this.interactorSynchronizer.synchronize(interactor, true));
    }

    protected void prepareAndSynchronizeShortLabel(I intactParticipant) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactParticipant.getShortLabel().length()){
            log.warn("Participant shortLabel too long: "+intactParticipant.getShortLabel()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactParticipant.setShortLabel(intactParticipant.getShortLabel().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactEntityMergerEnrichOnly<T,I,Feature>());
    }
}
