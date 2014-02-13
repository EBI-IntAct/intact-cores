package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.EntityPool;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Synchronizer for all participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/02/14</pre>
 */

public class IntActEntitySynchronizer<P extends Entity, I extends AbstractIntactEntity, E extends EntityPool, J extends AbstractIntactEntity> extends AbstractIntactDbSynchronizer<P, I>{

    private IntactDbSynchronizer<P, I> participantBaseSynchronizer;
    private IntactDbSynchronizer<E, J> entityPoolSynchronizer;
    private Class<J> intactEntityClass;

    public IntActEntitySynchronizer(EntityManager entityManager, Class<I> intactClass, Class<J> intactEntityClass,
                                    IntactDbSynchronizer<P, I> participantBaseSynchronizer, IntactDbSynchronizer<E, J> entityPoolSynchronizer){
        super(entityManager, intactClass);
        if (entityPoolSynchronizer == null || participantBaseSynchronizer == null){
             throw new IllegalArgumentException("Cannot create an entity synchronizer without base participant synchronizer and entity pool synchronizer");
        }
        this.participantBaseSynchronizer=participantBaseSynchronizer;
        this.entityPoolSynchronizer = entityPoolSynchronizer;
        this.intactEntityClass = intactEntityClass;
    }

    public I find(P term) throws FinderException{
        if (term instanceof EntityPool){
            return (I)this.entityPoolSynchronizer.find((E)term);
        }
        else {
            return this.participantBaseSynchronizer.find(term);
        }
    }

    public I persist(I term) throws FinderException, PersisterException, SynchronizerException{
        if (term instanceof EntityPool){
            return (I)this.entityPoolSynchronizer.persist((J)term);
        }
        else {
            return this.participantBaseSynchronizer.persist(term);
        }
    }

    @Override
    public I synchronize(P term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof EntityPool){
            return (I)this.entityPoolSynchronizer.synchronize((E)term, persist);
        }
        else {
            return this.participantBaseSynchronizer.synchronize(term, persist);
        }
    }

    public void synchronizeProperties(I term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof EntityPool){
            this.entityPoolSynchronizer.synchronizeProperties((J)term);
        }
        else {
            this.participantBaseSynchronizer.synchronizeProperties(term);
        }
    }

    public void clearCache() {
        this.participantBaseSynchronizer.clearCache();
        this.entityPoolSynchronizer.clearCache();
    }

    public IntactDbSynchronizer<P, I> getParticipantBaseSynchronizer() {
        return participantBaseSynchronizer;
    }

    public IntactDbSynchronizer<E, J> getEntityPoolSynchronizer() {
        return entityPoolSynchronizer;
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected I instantiateNewPersistentInstance(P object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (object instanceof EntityPool){
            J newInteractor = this.intactEntityClass.getConstructor(Interactor.class).newInstance(object.getInteractor());
            ParticipantCloner.copyAndOverrideParticipantPoolProperties((E) object, (E) newInteractor, false);
            return (I)newInteractor;
        }
        else{
            I newInteractor = intactClass.getConstructor(Interactor.class).newInstance(object.getInteractor());
            ParticipantCloner.copyAndOverrideBasicParticipantProperties(object, newInteractor, false);
            return newInteractor;
        }
    }

    protected Class<J> getIntactEntityClass() {
        return intactEntityClass;
    }
}
