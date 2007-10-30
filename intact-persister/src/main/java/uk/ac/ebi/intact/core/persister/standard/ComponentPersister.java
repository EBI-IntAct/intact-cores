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
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.ArrayList;
import java.util.Collection;

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
        // Note: we do not try to reconnect to the database, we create a new one every time.
        return null;
    }

    @Override
    protected void saveOrUpdateAttributes(Component intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);

        if (intactObject.getExpressedIn() != null) {
            BioSourcePersister bioSourcePersister = BioSourcePersister.getInstance();
            bioSourcePersister.saveOrUpdate(intactObject.getExpressedIn());
         }

        CvObjectPersister cvPersister = CvObjectPersister.getInstance();
        cvPersister.saveOrUpdate(intactObject.getCvBiologicalRole());
        cvPersister.saveOrUpdate(intactObject.getCvExperimentalRole());

        for (CvIdentification participantDetection : intactObject.getParticipantDetectionMethods()) {
            cvPersister.saveOrUpdate(participantDetection);
        }

        for (CvExperimentalPreparation experimentalPreparation : intactObject.getExperimentalPreparations()) {
            cvPersister.saveOrUpdate(experimentalPreparation);
        }

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

        Collection<CvIdentification> syncedParticipantDetectionMethods = new ArrayList<CvIdentification>(intactObject.getParticipantDetectionMethods().size());
        for (CvIdentification participantDetection : intactObject.getParticipantDetectionMethods()) {
            CvIdentification detMethod = (CvIdentification) cvPersister.syncIfTransient(participantDetection);
            syncedParticipantDetectionMethods.add(detMethod);
        }
        intactObject.setParticipantDetectionMethods(syncedParticipantDetectionMethods);

        Collection<CvExperimentalPreparation> syncedExperimentalPreparations = new ArrayList<CvExperimentalPreparation>(intactObject.getExperimentalPreparations().size());
        for (CvExperimentalPreparation experimentalPreparation : intactObject.getExperimentalPreparations()) {
            syncedExperimentalPreparations.add((CvExperimentalPreparation) cvPersister.syncIfTransient(experimentalPreparation));
        }
        intactObject.setExperimentalPreparations(syncedExperimentalPreparations);

        // note that to avoid cyclic invocations, do not try to sync the interaction here

        intactObject.setInteractor((Interactor) InteractorPersister.getInstance().syncIfTransient(intactObject.getInteractor()));

        // TODO Bruno: Not syncing the features (and there sub-CVs) seems to have caused a bug !!
        // don't sync features (consider all them as new)
        Collection<Feature> features = new ArrayList<Feature>( intactObject.getBindingDomains().size() );
        for ( Feature feature : intactObject.getBindingDomains() ) {
            features.add( FeaturePersister.getInstance().syncIfTransient( feature ) );
        }
        intactObject.setBindingDomains( features );

        return super.syncAttributes(intactObject);
    }

     /**
     * Checks if two components are the same
     */
    protected static boolean areEquivalent(Component c1, Component c2) {
        boolean areEquivalent = (haveSameRoles(c1, c2) && haveSameDetectionMethods(c1, c2));

         if (!areEquivalent) return false;

         return (InteractorPersister.haveSameIdentity(c1.getInteractor(), c2.getInteractor()));
    }

    /**
     * Checks if two components have the same roles
     */
    protected static boolean haveSameRoles(Component c1, Component c2) {
        String miExpRole1 = CvObjectUtils.getPsiMiIdentityXref(c1.getCvExperimentalRole()).getPrimaryId();
        String miExpRole2 = CvObjectUtils.getPsiMiIdentityXref(c2.getCvExperimentalRole()).getPrimaryId();

        String miBioRole1 = CvObjectUtils.getPsiMiIdentityXref(c1.getCvBiologicalRole()).getPrimaryId();
        String miBioRole2 = CvObjectUtils.getPsiMiIdentityXref(c2.getCvBiologicalRole()).getPrimaryId();

        return (miExpRole1.equals(miExpRole2) &&
                miBioRole1.equals(miBioRole2));
    }

    /**
     * Checks if two component shave the same detection methods
     */
    protected static boolean haveSameDetectionMethods(Component c1, Component c2) {
        if (c1.getParticipantDetectionMethods().size() != c2.getParticipantDetectionMethods().size()) {
            return false;
        }

        for (CvIdentification detMethod1 : c1.getParticipantDetectionMethods()) {
            boolean found = false;

            String mi1 = CvObjectUtils.getPsiMiIdentityXref(detMethod1).getPrimaryId();

            for (CvIdentification detMethod2 : c2.getParticipantDetectionMethods()) {
                String mi2 = CvObjectUtils.getPsiMiIdentityXref(detMethod2).getPrimaryId();

                if (mi1.equals(mi2)) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }
}