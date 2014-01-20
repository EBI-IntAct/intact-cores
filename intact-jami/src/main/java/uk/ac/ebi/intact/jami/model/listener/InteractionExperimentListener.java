package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to interaction evidence object pre update/persist/load events
 * and set the experiment property when it is necessary
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class InteractionExperimentListener {

    @PrePersist
    @PreUpdate
    public void preUpdateAndPrePersist(IntactInteractionEvidence intactEntity){
        if (intactEntity.getExperiment() == null && !intactEntity.getExperiments().isEmpty()){
            intactEntity.getExperiments().clear();
        }
        else if (intactEntity.getExperiment() != null && !intactEntity.getExperiments().contains(intactEntity.getExperiment())){
            intactEntity.getExperiments().clear();
            intactEntity.getExperiments().add(intactEntity.getExperiment());
        }
    }

    @PostLoad
    public void postLoad(IntactInteractionEvidence intactEntity){
        if (intactEntity.getExperiment() == null && !intactEntity.getExperiments().isEmpty()){
            intactEntity.setExperiment(intactEntity.getExperiments().iterator().next());
        }
    }
}
