package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for alias
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactAliasSynchronizer implements IntactDbSynchronizer<Alias> {

    private IntactDbSynchronizer<CvTerm> typeSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactAlias> aliasClass;

    private static final Log log = LogFactory.getLog(IntactAliasSynchronizer.class);

    public IntactAliasSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactAlias> aliasClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Alias synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (aliasClass == null){
            throw new IllegalArgumentException("Alias synchronizer needs a non null alias class");
        }
        this.aliasClass = aliasClass;
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.ALIAS_TYPE_OBJCLASS,
                this, null, null);
    }

    public IntactAliasSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactAlias> aliasClass, IntactDbSynchronizer<CvTerm> typeSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Alias synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (aliasClass == null){
            throw new IllegalArgumentException("Alias synchronizer needs a non null alias class");
        }
        this.aliasClass = aliasClass;
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.ALIAS_TYPE_OBJCLASS,
                this, null, null);
    }

    public Alias find(Alias object) throws FinderException {
        return null;
    }

    public Alias persist(Alias object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactAlias) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Alias object) throws FinderException, PersisterException, SynchronizerException {
         synchronizeProperties((AbstractIntactAlias)object);
    }

    public Alias synchronize(Alias object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.aliasClass)){
            AbstractIntactAlias newAlias = null;
            try {
                newAlias = this.aliasClass.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getName());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.aliasClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.aliasClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.aliasClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.aliasClass, e);
            }

            // synchronize properties
            synchronizeProperties(newAlias);
            if (persist){
                this.entityManager.persist(newAlias);
            }
            return newAlias;
        }
        else{
            AbstractIntactAlias intactType = (AbstractIntactAlias)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // synchronize properties
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    return this.entityManager.merge(intactType);
                }
                else{
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // synchronize properties
                synchronizeProperties(intactType);
                // persist alias
                if (persist){
                    this.entityManager.persist(intactType);
                }
                return intactType;
            }
            else{
                // synchronize properties
                synchronizeProperties(intactType);
                return intactType;
            }
        }
    }

    public void clearCache() {
        this.typeSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactAlias object) throws PersisterException, SynchronizerException {
        if (object.getType() != null){
            CvTerm type = object.getType();
            try {
                object.setType(typeSynchronizer.synchronize(type, true, true));
            } catch (FinderException e) {
                throw new IllegalStateException("Cannot persist the alias because could not synchronize its alias type.");
            }
        }
        // check alias name
        if (object.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
            log.warn("Alias name too long: "+object.getName()+", will be truncated to "+ IntactUtils.MAX_ALIAS_NAME_LEN+" characters.");
            object.setName(object.getName().substring(0, IntactUtils.MAX_ALIAS_NAME_LEN));
        }
    }
}
