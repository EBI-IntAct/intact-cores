package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParameter;
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

public class IntactParameterSynchronizer implements IntactDbSynchronizer<Parameter>{
    private IntactDbSynchronizer<CvTerm> typeSynchronizer;
    private IntactDbSynchronizer<CvTerm> unitSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactParameter> parameterClass;

    private static final Log log = LogFactory.getLog(IntactParameterSynchronizer.class);

    public IntactParameterSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactParameter> paramClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Parameter synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (paramClass == null){
            throw new IllegalArgumentException("Parameter synchronizer needs a non null parameter class");
        }
        this.parameterClass = paramClass;
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.PARAMETER_TYPE_OBJCLASS);
        this.unitSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.UNIT_OBJCLASS);
    }

    public IntactParameterSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactParameter> paramClass,
                                       IntactDbSynchronizer<CvTerm> typeSynchronizer, IntactDbSynchronizer<CvTerm> unitSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Parameter synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (paramClass == null){
            throw new IllegalArgumentException("Parameter synchronizer needs a non null parameter class");
        }
        this.parameterClass = paramClass;
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.PARAMETER_TYPE_OBJCLASS);
        this.unitSynchronizer = unitSynchronizer != null ? unitSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.UNIT_OBJCLASS);
    }

    public Parameter find(Parameter object) throws FinderException {
        return null;
    }

    public Parameter persist(Parameter object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactParameter) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Parameter object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactParameter)object);
    }

    public Parameter synchronize(Parameter object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.parameterClass)){
            AbstractIntactParameter newParam = null;
            try {
                newParam = this.parameterClass.getConstructor(CvTerm.class, ParameterValue.class, CvTerm.class, BigDecimal.class).newInstance(object.getType(), object.getValue(), object.getUnit(), object.getUncertainty());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.parameterClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.parameterClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.parameterClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.parameterClass, e);
            }

            // synchronize properties
            synchronizeProperties(newParam);
            if (persist){
                this.entityManager.persist(newParam);
            }
            return newParam;
        }
        else{
            AbstractIntactParameter intactType = (AbstractIntactParameter)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // synchronize properties
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    return this.entityManager.merge(intactType);
                }
                else{
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // synchronize properties
                synchronizeProperties(intactType);
                // persist alias
                if (persist){
                    this.entityManager.persist(intactType);
                }
                return intactType;
            }
            else{
                // synchronize properties
                synchronizeProperties(intactType);
                return intactType;
            }
        }
    }

    public void clearCache() {
        this.typeSynchronizer.clearCache();
        this.unitSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactParameter object) throws PersisterException, SynchronizerException, FinderException {
        // type first
        CvTerm type = object.getType();
        object.setType(typeSynchronizer.synchronize(type, true, true));

        // check unit
        if (object.getUnit() != null){
            CvTerm unit = object.getUnit();
            object.setUnit(typeSynchronizer.synchronize(unit, true, true));
        }
    }
}
