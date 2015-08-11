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
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.map.IdentityMap;
import uk.ac.ebi.intact.core.persister.IntactCore;

import java.util.*;

/**
 * This class represents a PSI-MI XML entry in IntAct. It does not have a direct equivalence in the database.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactEntry implements Annotated {

    /**
     * Institution makes up for the entry source.
     */
    private Institution institution;

    /**
     * Addition to the institution to represent the source (that has a released date). 
     */
    private Date releasedDate;

    private Collection<Interaction> interactions;
    private Collection<Annotation> annotations;

    private transient Map<Experiment,Experiment> experiments;
    private transient Map<Interactor,Interactor> interactors;

    private Collection<Experiment> experimentsList = new ArrayList<Experiment>();
    private Collection<Interactor> interactorsList = new ArrayList<Interactor>();

    //////////////////
    // Constructors

    public IntactEntry() {
    }

    public IntactEntry(Collection<Interaction> interactions) {
        this.interactions = interactions;
    }

    /////////////////////////
    // Getters and Setters

    public Collection<Interaction> getInteractions() {
        if (interactions == null) {
            interactions = new ArrayList<Interaction>();
        }
        return interactions;
    }

    public void setInteractions(Collection<Interaction> interactions) {
        this.interactions = interactions;
    }

    /**
     * Convenience method to get the experiments - delegates the logic to the interactions
     *
     * @return Experiments
     */
    public Collection<Experiment> getExperiments() {
        if (experiments != null) {
            return experiments.keySet();

        }
        else if (!experimentsList.isEmpty()){
            return experimentsList;
        }

        experiments = new IdentityMap();

        for (Interaction interaction : getInteractions()) {
            for (Experiment experiment : IntactCore.ensureInitializedExperiments(interaction)) {
                experiments.put(experiment, null);
            }
        }

        return experiments.keySet();
    }

    /**
     * Convenience method to get the interactors - delegates the logic to the interactions
     *
     * @return Interactors
     */
    @Deprecated
    public Collection<Interactor> getInteractor() {
        return getInteractors();
    }

    /**
     * Convenience method to get the interactors - delegates the logic to the interactions
     *
     * @return Interactors
     */
    public Collection<Interactor> getInteractors() {
        if (interactors != null) {
            return interactors.keySet();
        }
        else if (!interactorsList.isEmpty()){
            return interactorsList;
        }

        interactors = new IdentityMap();

        for (Interaction interaction : getInteractions()) {
            for (Component comp : IntactCore.ensureInitializedParticipants(interaction)) {
                interactors.put(comp.getInteractor(), null);
            }

        }

        return interactors.keySet();
    }

    public Collection<Annotation> getAnnotations() {
        if (annotations == null) {
            annotations = new ArrayList<Annotation>();
        }
        return annotations;
    }

    public void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution( Institution institution ) {
        this.institution = institution;
    }

    public Date getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate( Date releasedDate ) {
        this.releasedDate = releasedDate;
    }

    public void clear(){
        if (annotations != null){
           annotations.clear();
        }

        if (interactions != null){
            interactions.clear();
        }

        experiments = null;
        interactors = null;
        institution = null;
        releasedDate = null;
        experimentsList.clear();
        interactorsList.clear();
    }

    /**
     * A list of experiments that we want to manually add to this entry
     * @return
     */
    public Collection<Experiment> getExperimentsList() {

        return experimentsList;
    }

    /**
     * A list of interactors that we want to manually add to this entry
     * @return
     */
    public Collection<Interactor> getInteractorsList() {

        return interactorsList;
    }
}