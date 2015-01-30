package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * Finder/persister for annotations
 *
 * It does not cache persisted annotations. It only synchronize the annotation topic (with persist = true) to make sure that the annotation topic
 * is persisted before so the annotation can be persisted
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

    @Override
    public Collection<A> findAll(Annotation object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(Annotation object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        // topic first
        CvTerm topic = object.getTopic();
        object.setTopic(getContext().getTopicSynchronizer().synchronize(topic, true));
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
    protected boolean containsObjectInstance(Annotation object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Annotation object) {
        // nothing to do
    }

    @Override
    protected A fetchMatchingObjectFromIdentityCache(Annotation object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(A object) throws SynchronizerException, PersisterException, FinderException {
        // topic first
        CvTerm topic = object.getTopic();
        object.setTopic(getContext().getTopicSynchronizer().convertToPersistentObject(topic));
    }

    @Override
    protected void storeObjectInIdentityCache(Annotation originalObject, A persistableObject) {
        // nothing to do
    }

    @Override
    protected boolean isObjectPartiallyInitialised(Annotation originalObject) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Annotation, A>(this));
    }
}
