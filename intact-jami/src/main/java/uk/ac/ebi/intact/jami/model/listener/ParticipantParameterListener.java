package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.model.extension.ParticipantEvidenceParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;

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
    public void prepareExperiment(IntactParticipantEvidence part){

        if (part.areParametersInitialized()){
            Experiment exp = part.getInteraction() != null ? part.getInteraction().getExperiment() : null;

            for (Parameter param : part.getParameters()){
                if (param instanceof ParticipantEvidenceParameter){
                    ParticipantEvidenceParameter interParam = (ParticipantEvidenceParameter)param;
                    interParam.setExperiment(exp);
                }
            }
        }
    }
}
