package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.meta.DbInfo;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * Finder/persister for dbinfo
 * It does not cache dbinfo.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class DbInfoSynchronizer extends AbstractIntactDbSynchronizer<DbInfo, DbInfo> {

    private static final Log log = LogFactory.getLog(DbInfoSynchronizer.class);

    public DbInfoSynchronizer(SynchronizerContext context){
        super(context, DbInfo.class);
    }

    public DbInfo find(DbInfo preference) throws FinderException {
        return null;
    }

    @Override
    public Collection<DbInfo> findAll(DbInfo object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(DbInfo object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(DbInfo object) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(DbInfo object) {
        return object.getKey();
    }

    @Override
    protected DbInfo instantiateNewPersistentInstance(DbInfo object, Class<? extends DbInfo> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DbInfo info = new DbInfo(object.getKey(), object.getValue());
        info.setCreator(object.getCreator());
        info.setCreated(object.getCreated());
        info.setUpdated(object.getUpdated());
        info.setUpdator(object.getUpdator());
        return info;
    }

    @Override
    protected void storeInCache(DbInfo originalObject, DbInfo persistentObject, DbInfo existingInstance) {
        // nothing to do
    }

    @Override
    protected DbInfo fetchObjectFromCache(DbInfo object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(DbInfo object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(DbInfo object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(DbInfo object) {
        // nothing to do
    }

    @Override
    protected DbInfo fetchMatchingObjectFromIdentityCache(DbInfo object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(DbInfo object) throws SynchronizerException, PersisterException, FinderException {
        // nothing to do
    }

    @Override
    protected void storeObjectInIdentityCache(DbInfo originalObject, DbInfo persistableObject) {
        // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(DbInfo originalObject) {
        return false;
    }

    @Override
    protected void resetObjectIdentity(DbInfo intactObject) {
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<DbInfo, DbInfo>(this));
    }
}
