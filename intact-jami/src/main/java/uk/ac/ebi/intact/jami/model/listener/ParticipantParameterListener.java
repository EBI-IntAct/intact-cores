package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.model.extension.ExperimentalEntityParameter;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to participant parameter object pre update/persist/load events
 * and reset experiment
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */

public class ParticipantParameterListener {

    @PrePersist
    @PreUpdate
    @PostLoad
    public void prepareExperiment(ExperimentalEntityParameter param){

        ExperimentalEntity part = param.getParent();
        if (part != null && part instanceof ParticipantEvidence){
            InteractionEvidence interaction = ((ParticipantEvidence)part).getInteraction();
            if (interaction != null){
                param.setExperiment(interaction.getExperiment());
            }
            else{
                param.setExperiment(null);
            }
        }
        else{
            param.setExperiment(null);
        }
    }
}
