package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to feature evidence object pre update/persist/load events
 * and set the identification method property when it is necessary
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class FeatureDetectionMethodListener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactFeatureEvidence intactFeature) {
        if (intactFeature.getDetectionMethods().isEmpty() && intactFeature.getFeatureIdentification() != null){
            intactFeature.setFeatureIdentification(null);
        }
        else if (!intactFeature.getDetectionMethods().isEmpty()){
            if (intactFeature.getFeatureIdentification() == null || !intactFeature.getDetectionMethods().contains(intactFeature.getFeatureIdentification())){
                intactFeature.setFeatureIdentification(intactFeature.getDetectionMethods().iterator().next());
            }
        }
    }

    @PostLoad
    public void postLoad(IntactFeatureEvidence intactFeature) {
        if (intactFeature.getFeatureIdentification() != null && !intactFeature.getDetectionMethods().contains(intactFeature.getFeatureIdentification())){
            intactFeature.getDetectionMethods().add(intactFeature.getFeatureIdentification());
        }
    }
}
