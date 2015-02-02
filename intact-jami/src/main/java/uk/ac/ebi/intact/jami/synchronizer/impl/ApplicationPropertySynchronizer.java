package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.meta.ApplicationProperty;
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

public class ApplicationPropertySynchronizer extends AbstractIntactDbSynchronizer<ApplicationProperty, ApplicationProperty> {

    private static final Log log = LogFactory.getLog(ApplicationProperty.class);

    public ApplicationPropertySynchronizer(SynchronizerContext context){
        super(context, ApplicationProperty.class);
    }

    public ApplicationProperty find(ApplicationProperty preference) throws FinderException {
        return null;
    }

    @Override
    public Collection<ApplicationProperty> findAll(ApplicationProperty object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(ApplicationProperty object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(ApplicationProperty object) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(ApplicationProperty object) {
        return object.getAc();
    }

    @Override
    protected ApplicationProperty instantiateNewPersistentInstance(ApplicationProperty object, Class<? extends ApplicationProperty> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new ApplicationProperty(object.getKey(), object.getValue());
    }

    @Override
    protected void storeInCache(ApplicationProperty originalObject, ApplicationProperty persistentObject, ApplicationProperty existingInstance) {
        // nothing to do
    }

    @Override
    protected ApplicationProperty fetchObjectFromCache(ApplicationProperty object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(ApplicationProperty object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(ApplicationProperty object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(ApplicationProperty object) {
        // nothing to do
    }

    @Override
    protected ApplicationProperty fetchMatchingObjectFromIdentityCache(ApplicationProperty object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(ApplicationProperty object) throws SynchronizerException, PersisterException, FinderException {
        // nothing to do
    }

    @Override
    protected void storeObjectInIdentityCache(ApplicationProperty originalObject, ApplicationProperty persistableObject) {
        // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(ApplicationProperty originalObject) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<ApplicationProperty, ApplicationProperty>(this));
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(ApplicationProperty existingInstance, ApplicationProperty originalObject) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do
    }
}
