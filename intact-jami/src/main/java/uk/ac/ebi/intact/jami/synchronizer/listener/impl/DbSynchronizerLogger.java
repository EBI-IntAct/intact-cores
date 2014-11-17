package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.synchronizer.listener.DbSynchronizerListener;

/**
 * This listener will log each db synchronizer events
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/11/14</pre>
 */

public class DbSynchronizerLogger implements DbSynchronizerListener{

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(DbSynchronizerLogger.class);

    @Override
    public void onPersisted(IntactPrimaryObject object) {
       log.info("Persisted object "+object.getClass().getCanonicalName() +": "+object.getAc()+", "+object.toString());
    }

    @Override
    public void onMerged(IntactPrimaryObject object, IntactPrimaryObject existingObject) {
        log.info("Merged object "+object.getClass().getCanonicalName() +": "+object.toString() +". Existing instance in database: "+existingObject.getAc());
    }

    @Override
    public void onTransientMergedWithDbInstance(IntactPrimaryObject object, IntactPrimaryObject existingObject) {
        log.info("Merged transient object with existing database object "+object.getClass().getCanonicalName() +": "+object.toString() +". Existing instance in database: "+existingObject.getAc());
    }

    @Override
    public void onReplacedWithDbInstance(IntactPrimaryObject object, IntactPrimaryObject existingObject) {
        log.info("Replaced transient object with existing db object "+object.getClass().getCanonicalName() +": "+object.toString() +". Existing instance in database: "+existingObject.getAc());
    }

    @Override
    public void onDeleted(IntactPrimaryObject object) {
        log.info("Deleted object "+object.getClass().getCanonicalName() +": "+object.getAc()+", "+object.toString());
    }
}
