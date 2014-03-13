package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.model.ModelledParticipantPool;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ModelledParticipantPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipantPool;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizer for IntAct modelled entity pools
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class ModelledParticipantPoolSynchronizer extends ModelledParticipantSynchronizerTemplate<ModelledParticipantPool,IntactModelledParticipantPool> {

    public ModelledParticipantPoolSynchronizer(SynchronizerContext context) {
        super(context, IntactModelledParticipantPool.class);
    }

    @Override
    public void synchronizeProperties(IntactModelledParticipantPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);

        CvTerm type = intactEntity.getType();
        intactEntity.setType(getContext().getInteractorTypeSynchronizer().synchronize(type, true));
    }

    protected void prepareEntities(IntactModelledParticipantPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ModelledParticipant> entitiesToPersist = new ArrayList<ModelledParticipant>(intactEntity);
            for (ModelledParticipant entity : entitiesToPersist){
                if (intactEntity != entity){
                    ModelledParticipant persistentEntity = (ModelledParticipant) getContext().getParticipantSynchronizer().synchronize(entity, true);
                    // we have a different instance because needed to be synchronized
                    if (persistentEntity != entity){
                        intactEntity.remove(entity);
                        intactEntity.add(persistentEntity);
                    }
                }
            }
        }
    }

    @Override
    protected IntactModelledParticipantPool instantiateNewPersistentInstance( ModelledParticipantPool object, Class<? extends IntactModelledParticipantPool> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactModelledParticipantPool newParticipant = new IntactModelledParticipantPool(object.getInteractor().getShortName());
        ParticipantCloner.copyAndOverrideParticipantPoolProperties(object, newParticipant, false);
        return newParticipant;
    }

    @Override
    public void clearCache() {
        super.clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ModelledParticipantPoolMergerEnrichOnly());
    }
}
