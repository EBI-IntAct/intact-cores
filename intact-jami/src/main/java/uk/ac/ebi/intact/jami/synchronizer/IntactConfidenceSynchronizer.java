package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/persister of confidenceClass
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactConfidenceSynchronizer<T extends Confidence, C extends AbstractIntactConfidence> extends AbstractIntactDbSynchronizer<T, C>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer;

    private static final Log log = LogFactory.getLog(IntactConfidenceSynchronizer.class);

    public IntactConfidenceSynchronizer(EntityManager entityManager, Class<? extends C> confClass){
        super(entityManager, confClass);
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
    }

    public C find(T object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        object.setType(getTypeSynchronizer().synchronize(type, true));

        // check confidenceClass value
        if (object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Confidence value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        getTypeSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getTypeSynchronizer() {
        if (this.typeSynchronizer == null){
            this.typeSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        }
        return typeSynchronizer;
    }

    public void setTypeSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer) {
        this.typeSynchronizer = typeSynchronizer;
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<T, C>(this));
    }
}
