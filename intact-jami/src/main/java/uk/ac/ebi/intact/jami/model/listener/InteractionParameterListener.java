package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
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
    public void prepareExperiment(IntactInteractionEvidence interaction){

        if (interaction.areParametersInitialized()){
            Experiment exp = interaction.getExperiment();

            for (Parameter param : interaction.getParameters()){
                if (param instanceof InteractionEvidenceParameter){
                    InteractionEvidenceParameter interParam = (InteractionEvidenceParameter)param;
                    interParam.setExperiment(exp);
                }
            }
        }
    }
}
