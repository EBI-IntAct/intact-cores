package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for annotations
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactAnnotationsSynchronizer implements IntactDbSynchronizer<Annotation> {

    private IntactDbSynchronizer<CvTerm> topicSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactAnnotation> annotationClass;

    private static final Log log = LogFactory.getLog(IntactAnnotationsSynchronizer.class);

    public IntactAnnotationsSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactAnnotation> annotationClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Annotation synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (annotationClass == null){
            throw new IllegalArgumentException("Annotation synchronizer needs a non null annotation class");
        }
        this.annotationClass = annotationClass;
        this.topicSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS,
                null, this, null);
    }

    public IntactAnnotationsSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactAnnotation> annotationClass, IntactDbSynchronizer<CvTerm> typeSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Annotation synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (annotationClass == null){
            throw new IllegalArgumentException("Annotation synchronizer needs a non null annotation class");
        }
        this.annotationClass = annotationClass;
        this.topicSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS,
                null, this, null);
    }

    public Annotation find(Annotation object) throws FinderException {
        return null;
    }

    public Annotation persist(Annotation object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactAnnotation) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Annotation object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactAnnotation)object);
    }

    public Annotation synchronize(Annotation object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.annotationClass)){
            AbstractIntactAnnotation newAnnot = null;
            try {
                newAnnot = this.annotationClass.getConstructor(CvTerm.class, String.class).newInstance(object.getTopic(), object.getValue());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            }

            // synchronize properties
            synchronizeProperties(newAnnot);
            if (persist){
                this.entityManager.persist(newAnnot);
            }
            return newAnnot;
        }
        else{
            AbstractIntactAnnotation intactType = (AbstractIntactAnnotation)object;
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
        this.topicSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactAnnotation object) throws PersisterException, SynchronizerException {
        // topic first
        CvTerm topic = object.getTopic();
        try {
            object.setTopic(topicSynchronizer.synchronize(topic, true, true));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the annotation because could not synchronize its annotation type.");
        }
        // check annotation value
        if (object.getValue() != null && object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Annotation value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }
}
