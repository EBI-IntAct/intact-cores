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
import uk.ac.ebi.intact.core.persister.PersisterUnexpectedException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.ExperimentUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentPersister extends AbstractAnnotatedObjectPersister<Experiment> {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(ExperimentPersister.class);

    private static ThreadLocal<ExperimentPersister> instance = new ThreadLocal<ExperimentPersister>() {
        @Override
        protected ExperimentPersister initialValue() {
            return new ExperimentPersister();
        }
    };

    public static ExperimentPersister getInstance() {
        return instance.get();
    }

    public ExperimentPersister() {
        super();
    }

    protected Experiment fetchFromDataSource(Experiment intactObject) {
        return getIntactContext().getDataContext().getDaoFactory()
                .getExperimentDao().getByShortLabel(intactObject.getShortLabel());
    }

    @Override
    protected BehaviourType syncedAndCandidateAreEqual(Experiment synced, Experiment candidate) {
        if (synced == null) return BehaviourType.NEW;

        if (!synced.getShortLabel().equals(candidate.getShortLabel())) {
            return BehaviourType.NEW;
        }

        if (!synced.getShortLabel().equals(candidate.getShortLabel())
            && ExperimentUtils.matchesSyncedLabel(synced.getShortLabel())) {
            return BehaviourType.NEW;
        }

        final String syncedPubmedId = ExperimentUtils.getPubmedId(synced);
        final String candidatePubmedId = ExperimentUtils.getPubmedId(candidate);

        if (syncedPubmedId == null && candidatePubmedId != null) {
            if (log.isDebugEnabled())
                log.debug("Synced pubmedId: " + syncedPubmedId + " (" + synced.getShortLabel() + ") - candidate pubmedId: " + candidatePubmedId + " (" + synced.getShortLabel() + ")");
            return BehaviourType.UPDATE;
        }

        if (syncedPubmedId != null && !syncedPubmedId.equals(
                candidatePubmedId) && ExperimentUtils.matchesSyncedLabel(candidate.getShortLabel())) {
            throw new PersisterUnexpectedException("Trying to persist an Experiment with a short label that already exists in the database," +
                                                   " but they have different pubmed IDs: " + synced.getShortLabel() + " (Existing: " + syncedPubmedId + ", Found: " + candidatePubmedId + ")");
        }

        Collection<Interaction> syncedInteractions = synced.getInteractions();
        Collection<Interaction> candidateInteractions = candidate.getInteractions();

        if (syncedInteractions.size() != candidateInteractions.size()) {
            return BehaviourType.UPDATE;
        }

        List<String> syncedInteractionLabels = new ArrayList<String>(syncedInteractions.size());

        for (Interaction syncedInteraction : syncedInteractions) {
            syncedInteractionLabels.add(syncedInteraction.getShortLabel());
        }

        for (Interaction candidateInteraction : candidateInteractions) {
            if (!syncedInteractionLabels.contains(candidateInteraction.getShortLabel())) {
                return BehaviourType.UPDATE;
            }
        }

        return BehaviourType.IGNORE;
    }

    @Override
    protected void saveOrUpdateAttributes(Experiment intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);

        if (intactObject.getBioSource() != null) {
            BioSourcePersister.getInstance().saveOrUpdate(intactObject.getBioSource());
        }

        if (intactObject.getCvInteraction() != null) {
            CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvInteraction());
        } else {
            throw new NullPointerException("Experiment without CvInteraction: " + intactObject.getShortLabel());
        }

        if (intactObject.getCvIdentification() != null) {
            CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvIdentification());
        }

        if (intactObject.getPublication() != null) {
            PublicationPersister.getInstance().saveOrUpdate(intactObject.getPublication());
        }

        for (Interaction interaction : intactObject.getInteractions()) {
            InteractionPersister.getInstance().saveOrUpdate(interaction);
        }
    }

    @Override
    protected Experiment syncAttributes(Experiment intactObject) {
        if (intactObject.getBioSource() != null) {
            BioSource bioSource = BioSourcePersister.getInstance().syncIfTransient(intactObject.getBioSource());
            intactObject.setBioSource(bioSource);
        }

        intactObject.setCvInteraction((CvInteraction) CvObjectPersister.getInstance().syncIfTransient(intactObject.getCvInteraction()));

        if (intactObject.getCvIdentification() != null) {
            intactObject.setCvIdentification((CvIdentification) CvObjectPersister.getInstance().syncIfTransient(intactObject.getCvIdentification()));
        }

        if (intactObject.getPublication() != null) {
            intactObject.setPublication(PublicationPersister.getInstance().syncAttributes(intactObject.getPublication()));
        }

        return super.syncAttributes(intactObject);
    }

    @Override
    protected boolean update(Experiment candidateObject, Experiment objectToUpdate) throws PersisterException {
        for (Interaction interaction : candidateObject.getInteractions()) {
            objectToUpdate.getInteractions().add(interaction);
        }

        getIntactContext().getDataContext().getDaoFactory().getIntactObjectDao().evict(candidateObject.getPublication());

        if (objectToUpdate.getPublication() == null && candidateObject.getPublication() != null) {
            objectToUpdate.setPublication(candidateObject.getPublication());

            PublicationPersister.getInstance().saveOrUpdate(candidateObject.getPublication());
        }

        Collection<ExperimentXref> objToUpdateXrefs = objectToUpdate.getXrefs();

        for (ExperimentXref xref : candidateObject.getXrefs()) {
            if (!objToUpdateXrefs.contains(xref)) {
                objectToUpdate.addXref(xref);
            }
        }

        for (ExperimentAlias alias : candidateObject.getAliases()) {
            objectToUpdate.addAlias(alias);
        }

        super.updateCommonAttributes(candidateObject, objectToUpdate);

        return true;
    }
}