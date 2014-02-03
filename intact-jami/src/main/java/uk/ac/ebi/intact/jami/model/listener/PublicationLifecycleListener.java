package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.PostLoad;

/**
 * This listener listen to Publication object pre update/persist/load events
 * and set released date, source accordingly to existing lifecycle annotations
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */

public class PublicationLifecycleListener {

    @PostLoad
    public void postLoad(IntactPublication pub){

        if (pub.getReleasedDate() == null && !pub.getLifecycleEvents().isEmpty()){
            for (LifeCycleEvent evt : pub.getLifecycleEvents()){
                if (evt.getEvent().getShortName().equals(IntactUtils.RELEASED_STATUS)){
                    pub.setReleasedDate(evt.getWhen());
                    break;
                }
            }
        }
    }
}
