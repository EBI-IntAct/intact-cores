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
import uk.ac.ebi.intact.core.persister.BehaviourType;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvInteractionType;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.util.DebugUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Persister for interactions.
 *
 * Two interactions can be considered different if:
 *
 * 1. Two interactions with the same bait and prey but different ranges - should go in as seperate interations
 * 2. Two interactions same bait prey same ranges - should not go in as seperate interactions
 * 3. Bait prey with roles reversed same ranges - should go in as seperate interactions
 * 4. Same protein as bait and prey (may be same range or different ranges) - should go in as different interactions
 * 5. Totally different interactions. - should go in as different interactions
 * 6. Same interaction different annotations - should go in as different interactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionPersister extends InteractorPersister<Interaction>{

    private static final Log log = LogFactory.getLog( InteractionPersister.class );

    private static ThreadLocal<InteractionPersister> instance = new ThreadLocal<InteractionPersister>() {
        @Override
        protected InteractionPersister initialValue() {
            return new InteractionPersister();
        }
    };

    public static InteractionPersister getInstance() {
        return instance.get();
    }

    protected InteractionPersister() {
        super();
    }

    /**
     * Gets the object from the datasource. If it is found there, return it. Otherwise return null
     * @param intactObject
     * @return
     */
    @Override
    protected Interaction fetchFromDataSource(Interaction intactObject) {
        final InteractionDao interactionDao = getIntactContext().getDataContext().getDaoFactory().getInteractionDao();

        // try the AC
        if (intactObject.getAc() != null) {
            return interactionDao.getByAc(intactObject.getAc());
        }

        // Get the interactors where exactly the same interactors are involved
        List<String> interactorPrimaryIDs = InteractionUtils.getInteractorPrimaryIDs(intactObject);
        List<Interaction> interactionsWithSameInteractors =
                interactionDao.getByInteractorsPrimaryId(true, interactorPrimaryIDs.toArray(new String[interactorPrimaryIDs.size()]));

        for (Interaction interactionWithSameInteractor : interactionsWithSameInteractors) {
            IgnoreExperimentCrcCalculator crcCalculator = new IgnoreExperimentCrcCalculator();
            String interactionCrc = crcCalculator.crc64(intactObject);
            String interactionWithSameInteractorCrc = crcCalculator.crc64(interactionWithSameInteractor);

            if (interactionCrc.equals(interactionWithSameInteractorCrc)) {
                return interactionWithSameInteractor;
            }

        }

        return null;
    }


    @Override
    protected BehaviourType syncedAndCandidateAreEqual(Interaction synced, Interaction candidate) {
        if (synced == null) {
            return BehaviourType.NEW;
        }

        String candidateCrc = new CrcCalculator().crc64(candidate);

        // only update an interaction if already has an AC
        if (synced.getCrc().equals(candidateCrc)) {
            return BehaviourType.IGNORE;
        } else {
            return BehaviourType.UPDATE;
        } 
    }

    @Override
    protected void saveOrUpdateAttributes(Interaction intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);
        
        CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvInteractionType());

        saveOrUpdateComponents(intactObject);
        saveOrUpdateExperiments(intactObject);
    }

    @Override
    protected boolean update(Interaction candidateObject, Interaction objectToUpdate) throws PersisterException {
        // if updating, the interaction should maintain the shortlabel
        candidateObject.setShortLabel(objectToUpdate.getShortLabel());

        // update the experiment relationships
        final Collection<Experiment> candidateExperiments = candidateObject.getExperiments();
        List<Experiment> additionalExperiments = new ArrayList<Experiment>(candidateExperiments.size());

        for (Experiment candidateExp : candidateExperiments) {
            if (!objectToUpdate.getExperiments().contains(candidateExp)) {
                additionalExperiments.add(candidateExp);
            }
        }

        if (log.isDebugEnabled() && !additionalExperiments.isEmpty()) {
            log.debug("Adding additional experiments to interaction "+objectToUpdate.getShortLabel()+": "+ DebugUtil.labelList(additionalExperiments));
        }

        for (Experiment additionalExperiment : additionalExperiments) {
            objectToUpdate.addExperiment(additionalExperiment);
            ExperimentPersister.getInstance().saveOrUpdate(additionalExperiment);
        }

        return true;
    }

    @Override
    protected Interaction syncAttributes(Interaction intactObject) {
        CvInteractionType cvIntType = (CvInteractionType) CvObjectPersister.getInstance().syncIfTransient(intactObject.getCvInteractionType());
        intactObject.setCvInteractionType(cvIntType);

        syncComponents(intactObject);
        syncExperiments(intactObject);

        return super.syncAttributes(intactObject);
    }

    protected void saveOrUpdateComponents(Interaction intactObject) throws PersisterException {
        for (Component component : intactObject.getComponents()) {
            ComponentPersister.getInstance().saveOrUpdate(component);
        }
    }

    protected void saveOrUpdateExperiments(Interaction intactObject) throws PersisterException {
        for (Experiment experiment : intactObject.getExperiments()) {
            ExperimentPersister.getInstance().saveOrUpdate(experiment);
        }
    }

    protected void syncComponents(Interaction intactObject)  {
        ComponentPersister compPersister = ComponentPersister.getInstance();

        List<Component> components = new ArrayList<Component>(intactObject.getComponents().size());

        for (Component component : intactObject.getComponents()) {
            Component c = compPersister.syncIfTransient(component);
            c.setInteraction(intactObject);
            c.setInteractor( component.getInteractor() );
            components.add(c);
        }

//        for (Component c : components) {
//            c.setInteraction(intactObject);
//        }

        intactObject.setComponents(components);
    }

    protected void syncExperiments(Interaction intactObject)  {
        ExperimentPersister persister = ExperimentPersister.getInstance();

        List<Experiment> experiments = new ArrayList<Experiment>(intactObject.getExperiments().size());

        for (Experiment experiment : intactObject.getExperiments()) {
            Experiment exp = persister.syncIfTransient(experiment);
            experiments.add(exp);
        }

        intactObject.setExperiments(experiments);
    }

    private class IgnoreExperimentCrcCalculator extends CrcCalculator {

        @Override
        protected UniquenessStringBuilder createUniquenessString(Experiment experiment) {
            return new UniquenessStringBuilder();
        }
    }
    
}