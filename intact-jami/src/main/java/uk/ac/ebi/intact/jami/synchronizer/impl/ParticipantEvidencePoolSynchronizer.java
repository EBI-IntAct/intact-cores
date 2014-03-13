package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidencePool;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ParticipantEvidencePoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidencePool;
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

public class ParticipantEvidencePoolSynchronizer extends ParticipantEvidenceSynchronizerTemplate<ParticipantEvidencePool,IntactParticipantEvidencePool> {

    public ParticipantEvidencePoolSynchronizer(SynchronizerContext context) {
        super(context, IntactParticipantEvidencePool.class);
    }

    @Override
    public void synchronizeProperties(IntactParticipantEvidencePool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);

        CvTerm type = intactEntity.getType();
        intactEntity.setType(getContext().getInteractorTypeSynchronizer().synchronize(type, true));
    }

    protected void prepareEntities(IntactParticipantEvidencePool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ParticipantEvidence> entitiesToPersist = new ArrayList<ParticipantEvidence>(intactEntity);
            for (ParticipantEvidence entity : entitiesToPersist){
                if (entity != intactEntity){
                    ParticipantEvidence persistentEntity = (ParticipantEvidence)getContext().getParticipantSynchronizer().synchronize(entity, true);
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
    protected IntactParticipantEvidencePool instantiateNewPersistentInstance( ParticipantEvidencePool object, Class<? extends IntactParticipantEvidencePool> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactParticipantEvidencePool newParticipant = new IntactParticipantEvidencePool(object.getInteractor().getShortName());
        ParticipantCloner.copyAndOverrideExperimentalEntityPoolProperties(object, newParticipant, false);
        return newParticipant;
    }

    @Override
    public void clearCache() {
        super.clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ParticipantEvidencePoolMergerEnrichOnly());
    }
}
