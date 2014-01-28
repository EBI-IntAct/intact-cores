package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
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

public class IntactAnnotationsSynchronizer<A extends AbstractIntactAnnotation> extends AbstractIntactDbSynchronizer<Annotation, A> {

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> topicSynchronizer;

    private static final Log log = LogFactory.getLog(IntactAnnotationsSynchronizer.class);

    public IntactAnnotationsSynchronizer(EntityManager entityManager, Class<? extends A> annotationClass){
        super(entityManager, annotationClass);
        if (annotationClass.isAssignableFrom(CvTermAnnotation.class)){
            this.topicSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS,
                    null, (IntactAnnotationsSynchronizer<CvTermAnnotation>)this, null);
        }
        else{
            this.topicSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        }
    }

    public IntactAnnotationsSynchronizer(EntityManager entityManager, Class<? extends A> annotationClass, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer){
        super(entityManager, annotationClass);
        if (annotationClass.isAssignableFrom(CvTermAnnotation.class)){
            this.topicSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS,
                    null, (IntactAnnotationsSynchronizer<CvTermAnnotation>)this, null);
        }
        else{
            this.topicSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        }
    }

    public A find(Annotation object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        // topic first
        CvTerm topic = object.getTopic();
        object.setTopic(topicSynchronizer.synchronize(topic, true));

        // check annotation value
        if (object.getValue() != null && object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Annotation value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        this.topicSynchronizer.clearCache();
    }

    @Override
    protected Object extractIdentifier(A object) {
        return object.getAc();
    }

    @Override
    protected A instantiateNewPersistentInstance(Annotation object, Class<? extends A> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return  intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getTopic(), object.getValue());
    }
}
