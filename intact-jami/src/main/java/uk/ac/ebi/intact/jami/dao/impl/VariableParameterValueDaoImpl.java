package uk.ac.ebi.intact.jami.dao.impl;

import org.omg.DynamicAny._DynValueStub;
import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValue;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.VariableParameterValueDao;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.VariableParameterValueSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of alias dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class VariableParameterValueDaoImpl extends AbstractIntactBaseDao<VariableParameterValue, IntactVariableParameterValue> implements VariableParameterValueDao {

    public VariableParameterValueDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactVariableParameterValue.class, entityManager, context);
    }

    public Collection<IntactVariableParameterValue> getByParameterAc(String parentAc) {
        Query query = getEntityManager().createQuery("select v from IntactVariableParameterValue v " +
                "join v.variableParameter as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> getDbSynchronizer() {
        return getSynchronizerContext().getVariableParameterValueSynchronizer();
    }
}