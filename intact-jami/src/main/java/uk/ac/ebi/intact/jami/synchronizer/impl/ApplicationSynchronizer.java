package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.meta.Application;
import uk.ac.ebi.intact.jami.model.meta.ApplicationProperty;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Finder/persister for application persistent details
 *
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class ApplicationSynchronizer extends AbstractIntactDbSynchronizer<Application, Application> {

    private static final Log log = LogFactory.getLog(ApplicationSynchronizer.class);

    public ApplicationSynchronizer(SynchronizerContext context){
        super(context, Application.class);
    }

    public Application find(Application user) throws FinderException {
        return null;
    }

    @Override
    public Collection<Application> findAll(Application user) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(Application user) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(Application object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize application properties
        prepareApplicationProperties(object, true);
    }

    public void clearCache() {
        // nothing to do
    }
    protected void prepareApplicationProperties(Application intactUser, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactUser.areApplicationPropertiesInitialised()){
            List<ApplicationProperty> propertiesToPersist = new ArrayList<ApplicationProperty>(intactUser.getProperties());
            intactUser.getProperties().clear();
            for (ApplicationProperty pref : propertiesToPersist){
                // do not persist or merge preferences because of cascades
                ApplicationProperty userPref = enableSynchronization ?
                        getContext().getApplicationPropertySynchronizer().synchronize(pref, false) :
                        getContext().getApplicationPropertySynchronizer().convertToPersistentObject(pref);
                // we have a different instance because needed to be synchronized
                intactUser.addProperty(userPref);
            }
        }
    }

    @Override
    protected Object extractIdentifier(Application object) {
        return object.getAc();
    }

    @Override
    protected Application instantiateNewPersistentInstance(Application object, Class<? extends Application> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Application user = new Application(object.getKey(), object.getDescription());
        user.getProperties().addAll(object.getProperties());
        return user;
    }

    @Override
    protected void storeInCache(Application originalObject, Application persistentObject, Application existingInstance) {
        // nothing to do here
    }

    @Override
    protected Application fetchObjectFromCache(Application object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Application object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(Application object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Application object) {
         // nothing to do
    }

    @Override
    protected Application fetchMatchingObjectFromIdentityCache(Application object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(Application object) throws SynchronizerException, PersisterException, FinderException {
        // synchronize preferences
        prepareApplicationProperties(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(Application originalObject, Application persistableObject) {
         // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(Application originalObject) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Application, Application>(this));
    }
}
