package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Finder/persister for roles
 *
 * It does cache the persisted roles using a HashMap.
 * It retrieves existing roles based on the role name.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class RoleSynchronizer extends AbstractIntactDbSynchronizer<Role, Role> {

    private static final Log log = LogFactory.getLog(RoleSynchronizer.class);
    private Map<Role, Role> persistedRoles;

    public RoleSynchronizer(SynchronizerContext context){
        super(context, Role.class);
        this.persistedRoles = new HashMap<Role, Role>();
    }

    public Role find(Role object) throws FinderException {
        Query query;
        if (object == null){
            return null;
        }
        else if (this.persistedRoles.containsKey(object)){
            return this.persistedRoles.get(object);
        }
        // we have a simple roles. Only check its taxid
        else {
            query = getEntityManager().createQuery("select r from Role r " +
                    "where r.name = :name");
            query.setParameter("name", object.getName().trim().toUpperCase());
        }
        Collection<Role> roles = query.getResultList();
        if (roles.size() == 1){
            return roles.iterator().next();
        }
        else if (roles.size() > 1){
            throw new FinderException("The role "+object + " can match "+roles.size()+" roles in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    public void synchronizeProperties(Role object) throws FinderException, PersisterException, SynchronizerException {
        // nothing to do here
    }

    public void clearCache() {
        this.persistedRoles.clear();
    }

    @Override
    protected Object extractIdentifier(Role object) {
        return object.getAc();
    }

    @Override
    protected Role instantiateNewPersistentInstance(Role object, Class<? extends Role> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new Role(object.getName());
    }

    @Override
    protected void storeInCache(Role originalObject, Role persistentObject, Role existingInstance) {
        if (existingInstance != null){
            this.persistedRoles.put(originalObject, existingInstance);
        }
        else{
            this.persistedRoles.put(originalObject, persistentObject);
        }
    }

    @Override
    protected Role fetchObjectFromCache(Role object) {
        return this.persistedRoles.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(Role object) {
        return this.persistedRoles.containsKey(object);
    }

    @Override
    protected boolean isObjectAlreadyConvertedToPersistableInstance(Role object) {
        return false;
    }

    @Override
    protected Role fetchMatchingPersistableObject(Role object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(Role object) throws SynchronizerException, PersisterException, FinderException {
         // nothing to do
    }

    @Override
    protected void storePersistableObjectInCache(Role originalObject, Role persistableObject) {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Role, Role>(this));
    }
}
