package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/synchronizer for Checksum
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactChecksumSynchronizer implements IntactDbSynchronizer<Checksum>{
    private IntactDbSynchronizer<CvTerm> methodSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactChecksum> checksumClass;

    private static final Log log = LogFactory.getLog(IntactChecksumSynchronizer.class);

    public IntactChecksumSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactChecksum> checksumClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Checksum synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (checksumClass == null){
            throw new IllegalArgumentException("Checksum synchronizer needs a non null checksum class");
        }
        this.checksumClass = checksumClass;
        this.methodSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public IntactChecksumSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactChecksum> checksumClass, IntactDbSynchronizer<CvTerm> typeSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Checksum synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (checksumClass == null){
            throw new IllegalArgumentException("Checksum synchronizer needs a non null checksum class");
        }
        this.checksumClass = checksumClass;
        this.methodSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public Checksum find(Checksum object) throws FinderException {
        return null;
    }

    public Checksum persist(Checksum object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactChecksum) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Checksum object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactChecksum)object);
    }

    public Checksum synchronize(Checksum object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.checksumClass)){
            AbstractIntactChecksum newChecksum = null;
            try {
                newChecksum = this.checksumClass.getConstructor(CvTerm.class, String.class).newInstance(object.getMethod(), object.getValue());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.checksumClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.checksumClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.checksumClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.checksumClass, e);
            }

            // synchronize properties
            synchronizeProperties(newChecksum);
            if (persist){
                this.entityManager.persist(newChecksum);
            }
            return newChecksum;
        }
        else{
            AbstractIntactChecksum intactType = (AbstractIntactChecksum)object;
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
        this.methodSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactChecksum object) throws PersisterException, SynchronizerException, FinderException {
        // method first
        CvTerm method = object.getMethod();
        object.setMethod(methodSynchronizer.synchronize(method, true, true));

        // check checksum value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Checksum value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }
}
