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
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import java.util.ArrayList;
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
            CrcCalculator crcCalculator = new CrcCalculator();
            String interactionCrc = crcCalculator.crc64(intactObject);
            String interactionWithSameInteractorCrc = crcCalculator.crc64(interactionWithSameInteractor);

            if (interactionCrc.equals(interactionWithSameInteractorCrc)) {
                return interactionWithSameInteractor;
            }

        }

        return null;
    }

    /**
     * TODO forget it. because of A -- I mean see A above
     */
    @Override
    protected BehaviourType syncedAndCandidateAreEqual(Interaction synced, Interaction candidate) {
        if (synced == null) {
            return BehaviourType.NEW;
        }

        // never update interactions
        if (experimentLabels(synced).equals(experimentLabels(candidate))) {
            return BehaviourType.IGNORE;
        } else {
            return BehaviourType.NEW;
        }
    }

    /**
     * Used to create a unique chain of experiment labels, used to differenciate interactions
     * that could just have the same short label
     */
    private static String experimentLabels(Interaction interaction) {
        StringBuilder sb = new StringBuilder( interaction.getExperiments().size() * 21 ); // init to max size

        for (Experiment exp : interaction.getExperiments()) {
            sb.append( exp.getShortLabel() ).append( '_' );
        }

        return sb.toString();
    }

    @Override
    protected void saveOrUpdateAttributes(Interaction intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);
        
        CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvInteractionType());

        saveOrUpdateComponents(intactObject);
        saveOrUpdateExperiments(intactObject);
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
    
}