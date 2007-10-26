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
package uk.ac.ebi.intact.core.persister.standard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueResultException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.BehaviourType;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterUnexpectedException;
import uk.ac.ebi.intact.core.persister.UndefinedCaseException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorPersister<T  extends Interactor> extends AbstractAnnotatedObjectPersister<T>{

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(InteractorPersister.class);

    private static ThreadLocal<InteractorPersister> instance = new ThreadLocal<InteractorPersister>() {
        @Override
        protected InteractorPersister initialValue() {
            return new InteractorPersister();
        }
    };

    public static InteractorPersister getInstance() {
        return instance.get();
    }

    protected InteractorPersister() {
        super();
    }

    @Override
    protected void saveOrUpdateAttributes(T intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);

        if (intactObject.getBioSource() != null) {
            BioSourcePersister.getInstance().saveOrUpdate(intactObject.getBioSource());
        }

        if (intactObject.getCvInteractorType() != null) {
            CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvInteractorType());
        }

        if ( intactObject.getActiveInstances() != null ) {
            for ( Component c : intactObject.getActiveInstances() ) {
                ComponentPersister.getInstance().saveOrUpdate( c );
            }
        }
    }

    @Override
    protected T syncAttributes(T intactObject) {
        if (intactObject.getBioSource() != null) {
            BioSource syncedBioSource = BioSourcePersister.getInstance().syncIfTransient(intactObject.getBioSource());
            intactObject.setBioSource(syncedBioSource);
        }

        if (intactObject.getCvInteractorType() != null) {
            CvInteractorType cvIntType = (CvInteractorType) CvObjectPersister.getInstance().syncIfTransient(intactObject.getCvInteractorType());
            intactObject.setCvInteractorType(cvIntType);
        }

        if ( intactObject.getActiveInstances() != null ) {
            Collection<Component> components = new ArrayList<Component>( intactObject.getActiveInstances().size() );
            for ( Component component : intactObject.getActiveInstances() ) {
                final Component c = ComponentPersister.getInstance().syncIfTransient( component );
                c.setInteractor( intactObject );
                c.setInteraction( component.getInteraction() );
                components.add( c );
            }
            intactObject.setActiveInstances( components );
        }

        return super.syncAttributes(intactObject);
    }

    /**
     * This method tries to fetch an intactObject from the database that matches the object passed as parameter.
     * The order of the fetch is: (1) AC, (2) primaryID, (3) primaryID as AC, (4) short label
     * @param intactObject
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T fetchFromDataSource(T intactObject) {

        final InteractorDao<InteractorImpl> interactorDao = getIntactContext().getDataContext().getDaoFactory().getInteractorDao();

        // 1. AC
        if (intactObject.getAc() != null) {
            return (T) interactorDao
                    .getByAc(intactObject.getAc());
        }

        // 2. try to fetch first the object using the primary ID
        final Collection<InteractorXref> identityXrefs = XrefUtils.getIdentityXrefs(intactObject);
        if (identityXrefs.size() > 1) {
            throw new UndefinedCaseException("Interactor with more than one xref");
        }

        if (identityXrefs.size() == 1) {
            final String primaryId = identityXrefs.iterator().next().getPrimaryId();
            T existingObject = null;
            try {
                existingObject = (T) interactorDao
                        .getByXref(primaryId);
            } catch (NonUniqueResultException e) {
                throw new PersisterUnexpectedException("Query for '"+primaryId+"' returned more than one xref: "+interactorDao.getByXrefLike(primaryId));
            }

            if (existingObject != null) {
                if (log.isDebugEnabled()) log.debug("Fetched existing object from the database: "+primaryId);
                return existingObject;
            }

            // 3. special case: check if the xref corresponds to an identifier of the own database
            existingObject = (T) interactorDao
                    .getByAc(primaryId);

            if (existingObject != null) {
                if (log.isDebugEnabled()) log.debug("Fetched existing object from the databse; the primaryId is an object from this database: "+primaryId);
                return existingObject;
            }
        }

        // 4. if the primaryId is not found, try the short label
        return fetchFromDataSourceByShortLabel(intactObject);
    }

    protected T fetchFromDataSourceByShortLabel(T intactObject) {
        InteractorDao<InteractorImpl> interactorDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractorDao(InteractorImpl.class);
        Collection<T> fetchedObjects = (Collection<T>) interactorDao.getColByPropertyName("shortLabel", intactObject.getShortLabel());

        if (fetchedObjects.isEmpty()) {
            return null;
        } else if (fetchedObjects.size() == 1) {
            return fetchedObjects.iterator().next();
        } else {

            // return the one that was last modified
            T fetchedLastModified = null;

            for (T fetchedObject : fetchedObjects) {
                if (fetchedLastModified == null) {
                    fetchedLastModified = fetchedObject;
                } else {
                    // TODO Bruno: was == 1 which contradict the use of that method, cf JavaDoc.
                    if (fetchedObject.getUpdated().compareTo(fetchedLastModified.getUpdated()) > 0) {
                        // fetchedObject.getUpdated() is after than fetchedLastModified.getUpdated()
                        fetchedLastModified = fetchedObject;
                    }
                }
            }

            return fetchedLastModified;
        }
    }

    /**
     * TODO: base this methods on the interactor equals (except AC check)
     */
    @Override
    protected BehaviourType syncedAndCandidateAreEqual(T synced, T candidate) {
        if (synced == null) return BehaviourType.NEW;

        if (synced.getXrefs().size() != candidate.getXrefs().size()) {
            return BehaviourType.UPDATE;
        }
        if (synced.getAliases().size() != candidate.getAliases().size()) {
            return BehaviourType.UPDATE;
        }
        
        return BehaviourType.IGNORE;
    }

    @Override
    protected boolean update(T candidateObject, T objectToUpdate) throws PersisterException
    {
        Collection<InteractorXref> objToUpdateXrefs = objectToUpdate.getXrefs();

        for (InteractorXref xref : candidateObject.getXrefs()) {
            if (!objToUpdateXrefs.contains(xref)) {
                objectToUpdate.addXref(xref);
            }
        }

        for (InteractorAlias alias : candidateObject.getAliases()) {
            objectToUpdate.addAlias(alias);
        }

        super.updateCommonAttributes(candidateObject, objectToUpdate);

        return true;
    }
}