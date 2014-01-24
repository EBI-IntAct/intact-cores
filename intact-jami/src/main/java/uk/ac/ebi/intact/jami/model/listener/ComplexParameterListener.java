package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.ComplexParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;

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

public class ComplexParameterListener {

    @PrePersist
    @PreUpdate
    @PostLoad
    public void prepareExperiment(ComplexParameter param){

        if (param.getParent() instanceof IntactComplex){
            IntactComplex parent = (IntactComplex)param.getParent();
            if (!parent.getExperiments().isEmpty()){
                param.setExperiment(parent.getExperiments().iterator().next());
            }
        }
    }
}
