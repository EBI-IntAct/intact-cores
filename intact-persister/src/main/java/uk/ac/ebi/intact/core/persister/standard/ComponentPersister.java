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

import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentPersister extends AbstractAnnotatedObjectPersister<Component>{

    private static ThreadLocal<ComponentPersister> instance = new ThreadLocal<ComponentPersister>() {
        @Override
        protected ComponentPersister initialValue() {
            return new ComponentPersister();
        }
    };

    public static ComponentPersister getInstance() {
        return instance.get();
    }

    public ComponentPersister() {
        super();
    }

    /**
     * TODO: check if the interaction fetching/syncing covers this - check by ac at least
     */
    protected Component fetchFromDataSource(Component intactObject) {
        return null;
    }

    @Override
    protected void saveOrUpdateAttributes(Component intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);
        
        if (intactObject.getExpressedIn() != null) {
            BioSource bioSource = BioSourcePersister.getInstance().syncIfTransient(intactObject.getExpressedIn());
            intactObject.setExpressedIn(bioSource);
         }

        CvObjectPersister cvPersister = CvObjectPersister.getInstance();
        cvPersister.saveOrUpdate(intactObject.getCvBiologicalRole());
        cvPersister.saveOrUpdate(intactObject.getCvExperimentalRole());

        // TODO: saveOrUpdate (and sync) participantIdentitifaction

        InteractionPersister.getInstance().saveOrUpdate(intactObject.getInteraction());

        InteractorPersister.getInstance().saveOrUpdate(intactObject.getInteractor());

        for (Feature feature : intactObject.getBindingDomains()) {
            FeaturePersister.getInstance().saveOrUpdate(feature);
        }
    }

    @Override
    protected Component syncAttributes(Component intactObject) {
         if (intactObject.getExpressedIn() != null) {
            BioSource bioSource = BioSourcePersister.getInstance().syncIfTransient(intactObject.getExpressedIn());
            intactObject.setExpressedIn(bioSource);
         }

        CvObjectPersister cvPersister = CvObjectPersister.getInstance();
        intactObject.setCvBiologicalRole((CvBiologicalRole) cvPersister.syncIfTransient(intactObject.getCvBiologicalRole()));
        intactObject.setCvExperimentalRole((CvExperimentalRole) cvPersister.syncIfTransient(intactObject.getCvExperimentalRole()));

        // note that to avoid cyclic invocations, do not try to sync the interaction here

        intactObject.setInteractor((Interactor) InteractorPersister.getInstance().syncIfTransient(intactObject.getInteractor()));

        // don't sync features (consider all them as new)


        return super.syncAttributes(intactObject);
    }
}