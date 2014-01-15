package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Feature;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to feature object pre update/persist/load events
 * and set the binds property when it is necessary
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class LinkedFeatureListener {

    @PrePersist
    @PreUpdate
    public void prePersist(AbstractIntactFeature intactFeature) {
        if (intactFeature.getLinkedFeatures().isEmpty() && intactFeature.getBinds() != null){
            intactFeature.setBinds(null);
        }
        else if (!intactFeature.getLinkedFeatures().isEmpty()){
            if (intactFeature.getBinds() == null || !intactFeature.getLinkedFeatures().contains(intactFeature.getBinds())){
                intactFeature.setBinds((Feature) intactFeature.getLinkedFeatures().iterator().next());
            }
        }
    }

    @PostLoad
    public void postLoad(AbstractIntactFeature intactFeature) {
        if (intactFeature.getBinds() != null && !intactFeature.getLinkedFeatures().contains(intactFeature.getBinds())){
            intactFeature.getLinkedFeatures().add(intactFeature.getBinds());
        }
    }
}
