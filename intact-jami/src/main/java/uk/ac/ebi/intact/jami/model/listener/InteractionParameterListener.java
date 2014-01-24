package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.InteractionEvidenceParameter;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to interaction parameter object pre update/persist/load events
 * and reset experiment
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */

public class InteractionParameterListener {

    @PrePersist
    @PreUpdate
    @PostLoad
    public void prepareExperiment(InteractionEvidenceParameter param){

        if (param.getParent() != null){
            param.setExperiment(param.getParent().getExperiment());
        }
    }
}
