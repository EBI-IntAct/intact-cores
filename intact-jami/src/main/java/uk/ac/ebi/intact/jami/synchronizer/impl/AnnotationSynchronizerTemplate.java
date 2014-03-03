package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for annotations
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class AnnotationSynchronizerTemplate<A extends AbstractIntactAnnotation> extends AbstractIntactDbSynchronizer<Annotation, A> implements AnnotationSynchronizer<A> {

    private static final Log log = LogFactory.getLog(AnnotationSynchronizerTemplate.class);

    public AnnotationSynchronizerTemplate(SynchronizerContext context, Class<? extends A> annotationClass){
        super(context, annotationClass);
    }

    public A find(Annotation object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        // topic first
        CvTerm topic = object.getTopic();
        object.setTopic(getContext().getTopicSynchronizer().synchronize(topic, true));

        // check annotation value
        if (object.getValue() != null && object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Annotation value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        // nothing to clear
    }

    @Override
    protected Object extractIdentifier(A object) {
        return object.getAc();
    }

    @Override
    protected A instantiateNewPersistentInstance(Annotation object, Class<? extends A> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return  intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getTopic(), object.getValue());
    }

    @Override
    protected void storeInCache(Annotation originalObject, A persistentObject, A existingInstance) {
        // ntohing to do
    }

    @Override
    protected A fetchObjectFromCache(Annotation object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Annotation object) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Annotation, A>(this));
    }
}
