package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Synchronizer for experimental entities and participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class ModelledParticipantSynchronizer extends ParticipantSynchronizerTemplate<ModelledParticipant, IntactModelledParticipant> {

    public ModelledParticipantSynchronizer(SynchronizerContext context){
        super(context, IntactModelledParticipant.class);
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getModelledFeatureSynchronizer();
    }

    @Override
    public void synchronizeProperties(IntactModelledParticipant intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);
        // then check aliases
        prepareAliases(intactEntity);
        // then check annotations
        prepareAnnotations(intactEntity);
        // then check xrefs
        prepareXrefs(intactEntity);
        // then check causal relationships
        prepareCausalRelationships(intactEntity);

        // check experimental roles for backward compatibility ONLY
        prepareExperimentalRoles(intactEntity);
    }

    protected void prepareCausalRelationships(IntactModelledParticipant intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areCausalRelationshipsInitialized()){
            List<CausalRelationship> relationshipsToPersist = new ArrayList<CausalRelationship>(intactEntity.getCausalRelationships());
            for (CausalRelationship causalRelationship : relationshipsToPersist){
                // do not persist or merge causalRelationship because of cascades
                CausalRelationship persistentRelationship = getContext().getModelledCausalRelationshipSynchronizer().synchronize(causalRelationship, false);
                // we have a different instance because needed to be synchronized
                if (persistentRelationship != causalRelationship){
                    intactEntity.getCausalRelationships().remove(causalRelationship);
                    intactEntity.getCausalRelationships().add(persistentRelationship);
                }
            }
        }
    }

    protected void prepareExperimentalRoles(IntactModelledParticipant intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areExperimentalRolesInitialized()){
            List<CvTerm> rolesToPersist = new ArrayList<CvTerm>(intactEntity.getDbExperimentalRoles());
            for (CvTerm role : rolesToPersist){
                CvTerm persistentRole = getContext().getExperimentalRoleSynchronizer().synchronize(role, true);
                // we have a different instance because needed to be synchronized
                if (persistentRole != role){
                    intactEntity.getDbExperimentalRoles().remove(role);
                    intactEntity.getDbExperimentalRoles().add(persistentRole);
                }
            }
        }
    }

    protected void prepareXrefs(IntactModelledParticipant intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactEntity.getXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref persistentXref = getContext().getModelledParticipantXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (persistentXref != xref){
                    intactEntity.getXrefs().remove(xref);
                    intactEntity.getXrefs().add(persistentXref);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactModelledParticipant intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactEntity.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation persistentAnnotation = getContext().getModelledParticipantAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (persistentAnnotation != annotation){
                    intactEntity.getAnnotations().remove(annotation);
                    intactEntity.getAnnotations().add(persistentAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(IntactModelledParticipant intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactEntity.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias persistentAlias = getContext().getModelledParticipantAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (persistentAlias != alias){
                    intactEntity.getAliases().remove(alias);
                    intactEntity.getAliases().add(persistentAlias);
                }
            }
        }
    }

    @Override
    public void deleteRelatedProperties(IntactModelledParticipant intactParticipant){
        super.deleteRelatedProperties(intactParticipant);
        for (CausalRelationship f : intactParticipant.getRelatedExperimentalCausalRelationships()){
            getContext().getExperimentalCausalRelationshipSynchronizer().delete(f);
        }
        intactParticipant.getRelatedExperimentalCausalRelationships().clear();
        for (CausalRelationship f : intactParticipant.getRelatedCausalRelationships()){
            getContext().getModelledCausalRelationshipSynchronizer().delete(f);
        }
        intactParticipant.getRelatedCausalRelationships().clear();
        for (Range f : intactParticipant.getRelatedRanges()){
            f.setParticipant(null);
        }
        intactParticipant.getRelatedRanges().clear();
        for (Range f : intactParticipant.getRelatedExperimentalRanges()){
            f.setParticipant(null);
        }
        intactParticipant.getRelatedExperimentalRanges().clear();
    }

    @Override
    protected void persistObject(IntactModelledParticipant existingInstance) {
        // first remove all dependencies to other participants to avoid cycle dependencies when persisting the objects
        Collection<CausalRelationship> relationships = new ArrayList<CausalRelationship>(existingInstance.getCausalRelationships());
        existingInstance.getCausalRelationships().clear();

        super.persistObject(existingInstance);

        // after persistence, re-attach dependent objects to avoid internal loops when participants are called by each other
        existingInstance.getCausalRelationships().addAll(relationships);
    }

    @Override
    protected IntactModelledParticipant instantiateNewPersistentInstance(ModelledParticipant object, Class<? extends IntactModelledParticipant> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactModelledParticipant newParticipant = new IntactModelledParticipant(object.getInteractor());
        ParticipantCloner.copyAndOverrideModelledParticipantProperties(object, newParticipant, false);
        return newParticipant;
    }
}


