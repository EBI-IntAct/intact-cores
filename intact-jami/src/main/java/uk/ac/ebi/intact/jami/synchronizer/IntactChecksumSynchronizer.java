package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
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

public class IntactChecksumSynchronizer<C extends AbstractIntactChecksum> extends AbstractIntactDbSynchronizer<Checksum, C>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer;

    private static final Log log = LogFactory.getLog(IntactChecksumSynchronizer.class);

    public IntactChecksumSynchronizer(EntityManager entityManager, Class<? extends C> checksumClass){
        super(entityManager, checksumClass);
    }

    public C find(Checksum object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // method first
        CvTerm method = object.getMethod();
        object.setMethod(getMethodSynchronizer().synchronize(method, true));

        // check checksum value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Checksum value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        getMethodSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getMethodSynchronizer() {
        if (this.methodSynchronizer == null){
            this.methodSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);
        }
        return methodSynchronizer;
    }

    public void setMethodSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer) {
        this.methodSynchronizer = methodSynchronizer;
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<Checksum, C>(this));
    }
}
