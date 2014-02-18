package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * default finder/Synchronizer for parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactParameterSynchronizer<T extends Parameter, P extends AbstractIntactParameter> extends AbstractIntactDbSynchronizer<T, P>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer;

    private static final Log log = LogFactory.getLog(IntactParameterSynchronizer.class);

    public IntactParameterSynchronizer(EntityManager entityManager, Class<? extends P> paramClass){
        super(entityManager, paramClass);
    }

    public P find(T object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(P object) throws FinderException, PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        object.setType(getTypeSynchronizer().synchronize(type, true));

        // check unit
        if (object.getUnit() != null){
            CvTerm unit = object.getUnit();
            object.setUnit(getUnitSynchronizer().synchronize(unit, true));
        }
    }

    public void clearCache() {
        getTypeSynchronizer().clearCache();
        getUnitSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getTypeSynchronizer() {
        if (this.typeSynchronizer == null){
            this.typeSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.PARAMETER_TYPE_OBJCLASS);
        }
        return typeSynchronizer;
    }

    public void setTypeSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer) {
        this.typeSynchronizer = typeSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getUnitSynchronizer() {
        if (this.unitSynchronizer == null){
            this.unitSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.UNIT_OBJCLASS);
        }
        return unitSynchronizer;
    }

    public void setUnitSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer) {
        this.unitSynchronizer = unitSynchronizer;
    }

    @Override
    protected Object extractIdentifier(P object) {
        return object.getAc();
    }

    @Override
    protected P instantiateNewPersistentInstance(T object, Class<? extends P> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, ParameterValue.class, CvTerm.class, BigDecimal.class).newInstance(object.getType(), object.getValue(), object.getUnit(), object.getUncertainty());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<T, P>(this));
    }
}
