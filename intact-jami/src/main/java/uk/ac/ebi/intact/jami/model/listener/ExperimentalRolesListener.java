package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactExperimentalEntity;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to experimental entity object pre update/persist/load events
 * and set the experimental role property when it is necessary
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class ExperimentalRolesListener {

    @PrePersist
    @PreUpdate
    public void preUpdateAndPrePersist(AbstractIntactExperimentalEntity intactEntity){
        if (intactEntity.getExperimentalRole() == null && !intactEntity.getExperimentalRoles().isEmpty()){
            intactEntity.getExperimentalRoles().clear();
        }
        else if (intactEntity.getExperimentalRole() != null && !intactEntity.getExperimentalRoles().contains(intactEntity.getExperimentalRole())){
            intactEntity.getExperimentalRoles().clear();
            intactEntity.getExperimentalRoles().add(intactEntity.getExperimentalRole());
        }
    }

    @PostLoad
    public void postLoad(AbstractIntactExperimentalEntity intactEntity){
        if (intactEntity.getExperimentalRole() == null && !intactEntity.getExperimentalRoles().isEmpty()){
            intactEntity.setExperimentalRole(intactEntity.getExperimentalRoles().iterator().next());
        }
    }
}
