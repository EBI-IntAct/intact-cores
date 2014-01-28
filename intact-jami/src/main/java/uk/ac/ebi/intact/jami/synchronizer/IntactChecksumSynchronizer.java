package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
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
        this.methodSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public IntactChecksumSynchronizer(EntityManager entityManager, Class<? extends C> checksumClass, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer){
        super(entityManager, checksumClass);
        this.methodSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public C find(Checksum object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // method first
        CvTerm method = object.getMethod();
        object.setMethod(methodSynchronizer.synchronize(method, true, true));

        // check checksum value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Checksum value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        this.methodSynchronizer.clearCache();
    }

    @Override
    protected boolean isTransient(C object) {
        return object.getAc() == null;
    }

    @Override
    protected C instantiateNewPersistentInstance(Checksum object, Class<? extends C> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getMethod(), object.getValue());
    }
}
