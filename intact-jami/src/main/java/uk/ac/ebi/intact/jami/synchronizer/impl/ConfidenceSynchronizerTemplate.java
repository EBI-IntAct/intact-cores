package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/persister of confidenceClass
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ConfidenceSynchronizerTemplate<T extends Confidence, C extends AbstractIntactConfidence> extends AbstractIntactDbSynchronizer<T, C>
        implements ConfidenceSynchronizer<T,C> {

    private static final Log log = LogFactory.getLog(ConfidenceSynchronizerTemplate.class);

    public ConfidenceSynchronizerTemplate(SynchronizerContext context, Class<? extends C> confClass){
        super(context, confClass);
    }

    public C find(T object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        object.setType(getContext().getConfidenceTypeSynchronizer().synchronize(type, true));

        // check confidenceClass value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Confidence value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(C object) {
        return object.getAc();
    }

    @Override
    protected C instantiateNewPersistentInstance(T object, Class<? extends C> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getValue());
    }

    @Override
    protected void storeInCache(T originalObject, C persistentObject, C existingInstance) {
        // nothing to do
    }

    @Override
    protected C fetchObjectFromCache(T object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(T object) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<T, C>(this));
    }
}
