/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;


/**
 * Base of all the persisters
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractPersister<T extends AnnotatedObject> implements Persister<T> {

    private static final Log log = LogFactory.getLog(AbstractPersister.class);

    protected AbstractPersister() {
    }

    /**
     * This method is the main method to be invoked when wanting to persist (or update) an intactObject
     * into the database. It will always synchronize it first and all of its attributes, and it will finally
     * add the object to the PersisterContext. It will be persisted when the <code>commit()</code> method is invoked
     * @param intactObject the intactObject to persist
     * @throws PersisterException if something occurs during the persistence
     */
    public final void saveOrUpdate(T intactObject) throws PersisterException {
        if (intactObject == null) {
            throw new NullPointerException("intactObject");
        }

        if (PersisterContext.getInstance().contains(intactObject)) {
            if ( log.isDebugEnabled() ) {
                log.debug( intactObject.getClass().getSimpleName() + ": " + intactObject.getShortLabel() + "("+ AnnotKeyGenerator.createKey(intactObject)+") is already in the PersisterContext, skipping..." );
            }

            return;
        }

        if (log.isDebugEnabled()) log.debug("Saving "+intactObject.getClass().getSimpleName()+": "+intactObject.getShortLabel());

        SyncTransientResponse<T> syncResponse = syncIfTransientResponse(intactObject);

        // This newAnnotatedObject will be persisted, depending on the resulting behaviour
        T newAnnotatedObject = null;

        if (syncResponse.isAlreadyPresent()) {

            if (intactObject.getAc() != null) {
                intactObject = syncIfTransient(intactObject);
            }

            BehaviourType behaviour = syncedAndCandidateAreEqual(syncResponse.getValue(), intactObject);

            switch (behaviour) {
                case NEW:
                    if (log.isDebugEnabled())
                        log.debug("\tFound similar object in the DB, but not the same so will create a new one(Behaviour:"+behaviour+")");

                    // the synced object will be persisted
                    newAnnotatedObject = intactObject;

                    break;

                case UPDATE:
                    if (log.isDebugEnabled())
                        log.debug("\tData source object and object to persist are not equal (Behaviour:"+behaviour+")");

                    if (intactObject.getAc() == null) {
                        intactObject.setAc(syncResponse.getValue().getAc());
                    }

                    if (!isDryRun()) {
                        T objectToUpdate = syncResponse.getValue();

                        update(intactObject, objectToUpdate);

                        // evict the intactObject,
                        // so the are not updated automatically on the next flush
                        getIntactContext().getDataContext().getDaoFactory().getIntactObjectDao().evict(intactObject);
                        //getIntactContext().getDataContext().getDaoFactory().getIntactObjectDao().evict(objectToUpdate);

                        PersisterContext.getInstance().addToUpdate(objectToUpdate);

                        SyncContext.getInstance().addToSynced(objectToUpdate);
                    } 
                    break;

                case IGNORE:
                    if (log.isDebugEnabled()) log.debug("\tAlready present in a data source (Behaviour:"+behaviour+") - "+syncResponse.getValue().getShortLabel()+" ("+syncResponse.getValue().getAc()+")");
                    if (intactObject.getAc() == null) {
                        intactObject.setAc(syncResponse.getValue().getAc());
                    }
                    break;
            }

            // don't continue if the object already exists or has been updated
            // TODO Bruno: Prevented a publication from being attached to the session.
            if (behaviour == BehaviourType.UPDATE || behaviour == BehaviourType.IGNORE )  {
                return;
            }
        } else {
            newAnnotatedObject = syncResponse.getValue();
        }

        log.debug("\tNot present in a data source - Will persist - "+syncResponse.getValue().getShortLabel());

        PersisterContext.getInstance().addToPersist(newAnnotatedObject);

        saveOrUpdateAttributes(newAnnotatedObject);
    }

    /**
     * Mandatory method to execute the actual data persister in the database.
     */
    public final void commit() {
       PersisterContext.getInstance().persistAll();
    }

    /**
     * Checks is the given object already synchronized with the persistence session.
     * @param intactObject object to be synced.
     * @return a response that contains the answer.
     */
    protected final SyncTransientResponse<T> syncIfTransientResponse(T intactObject) {
         T refreshedObject = syncIfTransient(intactObject);

        if (refreshedObject.getAc() != null) {
            return new SyncTransientResponse<T>(true, refreshedObject);
        }
        
        return new SyncTransientResponse<T>(false, syncAttributes(intactObject));
    }

    /**
     * This method tries to get an object from any of the scopes (invoking the <code>get(IntactObject)</code> method.
     * If it does not exist, it adds it to the SyncContext and returns the newly synced object.
      * @param intactObject The object to sync (if transient)
     * @return a synced object, which can be itself or a previously equal synced object
     */
    public T syncIfTransient(T intactObject) {
        if (log.isDebugEnabled()) log.debug("\t\tSyncing "+intactObject.getClass().getSimpleName()+": "+intactObject.getShortLabel());

        T refreshedObject = get(intactObject);

        if (refreshedObject != null) {
            if (log.isDebugEnabled()) log.debug("\t\t\tAlready synced: " + intactObject.getClass().getSimpleName() + "(" + intactObject.getShortLabel() + " - " + intactObject.getAc() + ")");
            return refreshedObject;
        }

        if ( log.isDebugEnabled() ) {
             log.debug( "\t\t\tAdding to SyncContext. Key: "+ AnnotKeyGenerator.createKey(intactObject));
        }

        SyncContext.getInstance().addToSynced(intactObject);
        
        return syncAttributes(intactObject);
    }

    /**
     * Tries to get an object from the PersisterContext, SyncContext and data source in this order. It is invoked
     * by the sync methods to get the passed object if it exists in any of the scopes.
     * @param intactObject The intactObject to get.
     * @return The synced intactObject. Null otherwise.
     */
    protected final T get(T intactObject) {
        if (PersisterContext.getInstance().contains(intactObject)) {
            if ( log.isDebugEnabled() )  log.debug( "\t\t\tFound it in PersisterContext" );
            T current = (T) PersisterContext.getInstance().get(intactObject);

            if (current == null) {
                throw new IllegalStateException("IntactObject expected but not returned from the PersisterContext: "+intactObject);
            }
            return current;
        }
        if (SyncContext.getInstance().isAlreadySynced(intactObject)) {
            if ( log.isDebugEnabled() ) {
                log.debug( "\t\t\tFound it in SyncContext. Key: "+ AnnotKeyGenerator.createKey(intactObject));
            }
            return (T) SyncContext.getInstance().get(intactObject);
        }

        final DaoFactory daoFactory = getIntactContext().getDataContext().getDaoFactory();
        if (intactObject.getAc() != null && daoFactory.getEntityManager().contains(intactObject)) {
            daoFactory.getBaseDao().evict(intactObject);
        }

        T t = fetchFromDataSource( intactObject );

        if (t != null) {
            if (log.isDebugEnabled()) log.debug("\t\t\tFound in data source");

            SyncContext.getInstance().addToSynced(t);
            return t;
        } else {
            if (log.isDebugEnabled()) log.debug("\t\t\tNot previously synced");
        }

        return null;
    }

    /**
     * Save of update the attributes (entities referenced by the intactObject). For each attribute we will
     * invoke the method <code>saveOrUpdate</code> of its corresponding Persister.
     * @param intactObject the intactObject containing the attributes.
     * @throws PersisterException if something unexpected occurs
     */
    protected abstract void saveOrUpdateAttributes(T intactObject) throws PersisterException;

    /**
     * This method should go through all the entities referenced by the passed intactObject. For each
     * of its entities it should invoke <code>syncIfTransient</code>.<br/>
     * The last call in this method normally should call to super.syncAttributes.
     * @param intactObject the intactObject to sync
     * @return a synced Object
     */
    protected abstract T syncAttributes(T intactObject);

    /**
     * Actual call to the database to fetch this specific Object.
     * @param intactObject The intactObject to get from the database
     * @return If the object exists in the database, return it. Otherwise, return null.
     */
    protected abstract T fetchFromDataSource(T intactObject);

    /**
     * Checks if two objects are equal according to its persistence rules.
     * @param synced Usually is the object that comes from the database
     * @param candidate It is the object that we want to persist
     * @return a BehaviourType enum that will define the persister behaviour
     */
    protected abstract BehaviourType syncedAndCandidateAreEqual(T synced, T candidate);

    /**
     * Updates an object using a model.
     * @param candidateObject The object with the most up to date information.
     * @param objectToUpdate The object to be updated. This object usually comes from the database, and we
     * will use the candidateObject as a source of information.
     * @return true if the has been an update
     * @throws PersisterException if something occurs
     */
    protected abstract boolean update(T candidateObject, T objectToUpdate) throws PersisterException;

    protected IntactContext getIntactContext() {
        return IntactContext.getCurrentInstance();
    }

    public boolean isDryRun() {
        return PersisterContext.getInstance().isDryRun();
    }

    private class SyncTransientResponse<T> {
        private boolean alreadyPresent;
        private T value;

        public SyncTransientResponse(boolean alreadyPresent, T value) {
            this.alreadyPresent = alreadyPresent;
            this.value = value;
        }

        public boolean isAlreadyPresent() {
            return alreadyPresent;
        }

        public T getValue() {
            return value;
        }
    }

}
