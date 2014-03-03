package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/synchronizer for Checksum
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ChecksumSynchronizerTemplate<C extends AbstractIntactChecksum> extends AbstractIntactDbSynchronizer<Checksum, C>
implements ChecksumSynchronizer<C>{
    private static final Log log = LogFactory.getLog(ChecksumSynchronizer.class);

    public ChecksumSynchronizerTemplate(SynchronizerContext context, Class<? extends C> checksumClass){
        super(context, checksumClass);
    }

    public C find(Checksum object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // method first
        CvTerm method = object.getMethod();
        object.setMethod(getContext().getTopicSynchronizer().synchronize(method, true));

        // check checksum value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Checksum value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
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
    protected C instantiateNewPersistentInstance(Checksum object, Class<? extends C> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getMethod(), object.getValue());
    }

    @Override
    protected void storeInCache(Checksum originalObject, C persistentObject, C existingInstance) {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Checksum, C>(this));
    }
}
