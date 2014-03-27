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

/**
 * Finder/persister for user preferences
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Preference, Preference>(this));
    }
}
