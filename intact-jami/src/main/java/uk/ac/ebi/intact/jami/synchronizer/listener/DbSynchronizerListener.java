package uk.ac.ebi.intact.jami.synchronizer.listener;

import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;

import java.util.EventListener;

/**
 * Listener which listens to synchronization events
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/11/14</pre>
 */

public interface DbSynchronizerListener extends EventListener {

    public void onPersisted(IntactPrimaryObject object);

    public void onMerged(IntactPrimaryObject object, IntactPrimaryObject existingObject);

    public void onTransientMergedWithDbInstance(IntactPrimaryObject object, IntactPrimaryObject existingObject);

    public void onReplacedWithDbInstance(IntactPrimaryObject object, IntactPrimaryObject existingObject);

    public void onDeleted(IntactPrimaryObject object);
}
