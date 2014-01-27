package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/persister of confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactConfidenceSynchronizer implements IntactDbSynchronizer<Confidence>{
    private IntactDbSynchronizer<CvTerm> typeSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactConfidence> confidence;

    private static final Log log = LogFactory.getLog(IntactConfidenceSynchronizer.class);

    public IntactConfidenceSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactConfidence> confClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Confidence synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (confClass == null){
            throw new IllegalArgumentException("Confidence synchronizer needs a non null confidence class");
        }
        this.confidence = confClass;
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
    }

    public IntactConfidenceSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactConfidence> confClass, IntactDbSynchronizer<CvTerm> typeSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Confidence synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (confClass == null){
            throw new IllegalArgumentException("Confidence synchronizer needs a non null confidence class");
        }
        this.confidence = confClass;
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
    }

    public Confidence find(Confidence object) throws FinderException {
        return null;
    }

    public Confidence persist(Confidence object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactConfidence) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Confidence object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactConfidence)object);
    }

    public Confidence synchronize(Confidence object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.confidence)){
            AbstractIntactConfidence newConfidence = null;
            try {
                newConfidence = this.confidence.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getValue());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.confidence, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.confidence, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.confidence, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.confidence, e);
            }

            // synchronize properties
            synchronizeProperties(newConfidence);
            if (persist){
                this.entityManager.persist(newConfidence);
            }
            return newConfidence;
        }
        else{
            AbstractIntactConfidence intactType = (AbstractIntactConfidence)object;
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

    protected void synchronizeProperties(AbstractIntactConfidence object) throws PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        try {
            object.setType(typeSynchronizer.synchronize(type, true, true));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the confidence because could not synchronize its confidence type.");
        }
        // check confidence value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Confidence value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }
}
