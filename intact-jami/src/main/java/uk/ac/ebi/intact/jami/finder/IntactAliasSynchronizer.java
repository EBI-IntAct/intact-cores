package uk.ac.ebi.intact.jami.finder;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
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

public class IntactAliasSynchronizer implements IntactDbSynchronizer<Alias>{

    private IntactDbSynchronizer<CvTerm> typeSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactAlias> aliasClass;

    public IntactAliasSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactAlias> aliasClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Alias synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (aliasClass == null){
            throw new IllegalArgumentException("Alias synchronizer needs a non null alias class");
        }
        this.aliasClass = aliasClass;
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager);
    }

    public Alias find(Alias object) throws FinderException {
        return null;
    }

    public Alias persist(Alias object) throws FinderException, PersisterException, SynchronizerException {
        AbstractIntactAlias intactAlias = synchronizeProperties((AbstractIntactAlias) object);
        this.entityManager.persist(intactAlias);
        return intactAlias;
    }

    public void synchronizeProperties(Alias object) throws FinderException, PersisterException, SynchronizerException {
         synchronizeProperties((AbstractIntactAlias)object);
    }

    public Alias synchronize(Alias object) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.aliasClass)){
            Alias newAlias = null;
            try {
                newAlias = this.aliasClass.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getName());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return newAlias;
        }
        else{
            AbstractIntactAlias intactType = (AbstractIntactAlias)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                return this.entityManager.merge(intactType);
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                CvTerm newTopic = findOrPersist(intactType, objClass);
                this.persistedObjects.put(newTopic, newTopic);
                return newTopic;
            }
            else{
                this.persistedObjects.put(cv, cv);
                return cv;
            }
        }
    }

    public void clearCache() {
        this.typeSynchronizer.clearCache();
    }

    protected AbstractIntactAlias synchronizeProperties(AbstractIntactAlias object) throws PersisterException, SynchronizerException {
        AbstractIntactAlias intactAlias = (AbstractIntactAlias)object;
        if (intactAlias.getType() != null){
            CvTerm type = intactAlias.getType();
            try {
                intactAlias.setType(typeSynchronizer.synchronize(type));
            } catch (FinderException e) {
                throw new IllegalStateException("Cannot persist the alias because could not synchronize its alias type.");
            }
        }
        // check alias name
        if (intactAlias.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
            intactAlias.setName(intactAlias.getName().substring(0,IntactUtils.MAX_ALIAS_NAME_LEN));
        }
        return intactAlias;
    }
}
