package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * Finder/persister for user preferences
 * It does not cache persisted preferences.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class PreferenceSynchronizer extends AbstractIntactDbSynchronizer<Preference, Preference> {

    private static final Log log = LogFactory.getLog(PreferenceSynchronizer.class);

    public PreferenceSynchronizer(SynchronizerContext context){
        super(context, Preference.class);
    }

    public Preference find(Preference preference) throws FinderException {
        return null;
    }

    @Override
    public Collection<Preference> findAll(Preference object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(Preference object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(Preference object) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(Preference object) {
        return object.getAc();
    }

    @Override
    protected Preference instantiateNewPersistentInstance(Preference object, Class<? extends Preference> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new Preference(object.getKey(), object.getValue());
    }

    @Override
    protected void storeInCache(Preference originalObject, Preference persistentObject, Preference existingInstance) {
        // nothing to do
    }

    @Override
    protected Preference fetchObjectFromCache(Preference object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Preference object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(Preference object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Preference object) {
        // nothing to do
    }

    @Override
    protected Preference fetchMatchingObjectFromIdentityCache(Preference object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(Preference object) throws SynchronizerException, PersisterException, FinderException {
        // nothing to do
    }

    @Override
    protected void storeObjectInIdentityCache(Preference originalObject, Preference persistableObject) {
        // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(Preference originalObject) {
        return false;
    }

    @Override
    protected void resetObjectIdentifier(Preference intactObject) {
        intactObject.setAc(null);
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Preference, Preference>(this));
    }
}
