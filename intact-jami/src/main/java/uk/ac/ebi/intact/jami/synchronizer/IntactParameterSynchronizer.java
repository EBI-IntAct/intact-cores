package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
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

public class IntactParameterSynchronizer<P extends AbstractIntactParameter> extends AbstractIntactDbSynchronizer<Parameter, P>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer;

    private static final Log log = LogFactory.getLog(IntactParameterSynchronizer.class);

    public IntactParameterSynchronizer(EntityManager entityManager, Class<? extends P> paramClass){
        super(entityManager, paramClass);
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.PARAMETER_TYPE_OBJCLASS);
        this.unitSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.UNIT_OBJCLASS);
    }

    public IntactParameterSynchronizer(EntityManager entityManager, Class<? extends P> paramClass,
                                       IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer){
        super(entityManager, paramClass);
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.PARAMETER_TYPE_OBJCLASS);
        this.unitSynchronizer = unitSynchronizer != null ? unitSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.UNIT_OBJCLASS);
    }

    public P find(Parameter object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(P object) throws FinderException, PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        object.setType(typeSynchronizer.synchronize(type, true));

        // check unit
        if (object.getUnit() != null){
            CvTerm unit = object.getUnit();
            object.setUnit(typeSynchronizer.synchronize(unit, true));
        }
    }

    public void clearCache() {
        this.typeSynchronizer.clearCache();
        this.unitSynchronizer.clearCache();
    }

    @Override
    protected Object extractIdentifier(P object) {
        return object.getAc();
    }

    @Override
    protected P instantiateNewPersistentInstance(Parameter object, Class<? extends P> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, ParameterValue.class, CvTerm.class, BigDecimal.class).newInstance(object.getType(), object.getValue(), object.getUnit(), object.getUncertainty());
    }
}
