package uk.ac.ebi.intact.jami.synchronizer;

import uk.ac.ebi.intact.jami.model.lifecycle.AbstractLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;

/**
 * Interface for lifecycle event synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface LifecycleEventSynchronizer<T extends AbstractLifeCycleEvent> extends IntactDbSynchronizer<LifeCycleEvent, T>{

}
