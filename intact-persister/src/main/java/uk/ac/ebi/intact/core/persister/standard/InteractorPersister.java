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

import uk.ac.ebi.intact.core.persister.BehaviourType;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorPersister<T  extends Interactor> extends AbstractAnnotatedObjectPersister<T>{

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

        return super.syncAttributes(intactObject);
    }

    @Override
    protected T fetchFromDataSource(T intactObject) {
        // TODO the next section is commented out as there can be interactors with the same label (which is a bug)
        // see IDU-2 (http://www.ebi.ac.uk/interpro/internal-tools/jira-intact/browse/IDU-2)
        /*
        try {
            return (T) getIntactContext().getDataContext().getDaoFactory()
                    .getInteractorDao().getByShortLabel(intactObject.getShortLabel());
        } catch (NonUniqueResultException e) {
            throw new PersisterUnexpectedException("There is more than one interactor with this label in the database: "+intactObject.getShortLabel(), e);
        }
        */

        Collection<T> fetchedObjects = (Collection<T>) getIntactContext().getDataContext().getDaoFactory()
                .getInteractorDao().getColByPropertyName("shortLabel", intactObject.getShortLabel());

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
                    if (fetchedObject.getUpdated().compareTo(fetchedLastModified.getUpdated()) == 1) {
                        fetchedLastModified = fetchedObject;
                    }
                }
            }

            return fetchedLastModified;
        }
    }

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