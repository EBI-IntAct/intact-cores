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


/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractPersister<T extends AnnotatedObject> implements Persister<T> {

    private static final Log log = LogFactory.getLog(AbstractPersister.class);

    protected AbstractPersister() {
    }
    
    public final void saveOrUpdate(T intactObject) throws PersisterException {
        if (intactObject == null) {
            throw new NullPointerException("intactObject");
        }

        if (PersisterContext.getInstance().contains(intactObject)) {
            if ( log.isDebugEnabled() ) {
                log.debug( intactObject.getClass().getSimpleName() + ": " + intactObject.getShortLabel() + " is already in the PersisterContext, skipping..." );
            }

            return;
        }

        if (log.isDebugEnabled()) log.debug("Saving "+intactObject.getClass().getSimpleName()+": "+intactObject.getShortLabel());

        SyncTransientResponse<T> syncResponse = syncIfTransientResponse(intactObject);

        // This newAnnotatedObject will be persisted, depending on the resulting behaviour
        T newAnnotatedObject = null;

        if (syncResponse.isAlreadyPresent()) {

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

                        // evict the objectToUpdate and the originalObject,
                        // so the are not updated automatically on the next flush
                        getIntactContext().getDataContext().getDaoFactory().getIntactObjectDao().evict(intactObject);
                        getIntactContext().getDataContext().getDaoFactory().getIntactObjectDao().evict(objectToUpdate);

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
            if (behaviour == BehaviourType.UPDATE || behaviour == BehaviourType.IGNORE)  {
                return;
            }
        } else {
            newAnnotatedObject = syncResponse.getValue();
        }

        log.debug("\tNot present in a data source - Will persist - "+syncResponse.getValue().getShortLabel());

        PersisterContext.getInstance().addToPersist(newAnnotatedObject);

        saveOrUpdateAttributes(newAnnotatedObject);
    }

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

    public T syncIfTransient(T intactObject) {
        if (log.isDebugEnabled()) log.debug("\t\tSyncing "+intactObject.getClass().getSimpleName()+": "+intactObject.getShortLabel());

        T refreshedObject = get(intactObject);

        if (refreshedObject != null) {
            if (log.isDebugEnabled()) log.debug("\t\t\tAlready synced: " + intactObject.getClass().getSimpleName() + "(" + intactObject.getShortLabel() + " - " + intactObject.getAc() + ")");
            return refreshedObject;
        }

        if (log.isDebugEnabled()) log.debug("\t\t\tNot previously synced");

        SyncContext.getInstance().addToSynced(intactObject);

        return syncAttributes(intactObject);
    }

    protected final T get(T intactObject) {
        if (PersisterContext.getInstance().contains(intactObject)) {
            if ( log.isDebugEnabled() ) {
                log.debug( "GET: Found it in PersisterContext" );
            }
            return (T) PersisterContext.getInstance().get(intactObject);
        }
        if (SyncContext.getInstance().isAlreadySynced(intactObject)) {
            if ( log.isDebugEnabled() ) {
                log.debug( "GET: Found it in SyncContext" );
            }
            return (T) SyncContext.getInstance().get(intactObject);
        }

        T t = fetchFromDataSource( intactObject );

        if ( log.isDebugEnabled() ) {
            if( t!= null ) {
                log.debug( "GET: Found it in DataSource" );
            } else {
                log.debug( "GET: Could not find it" );
            }
        }

        return t;
    }

    protected abstract void saveOrUpdateAttributes(T intactObject) throws PersisterException;

    protected abstract T syncAttributes(T intactObject);

    protected abstract T fetchFromDataSource(T intactObject);

    protected abstract BehaviourType syncedAndCandidateAreEqual(T synced, T candidate);

    protected abstract boolean update(T objectToUpdate, T existingObject) throws PersisterException;

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