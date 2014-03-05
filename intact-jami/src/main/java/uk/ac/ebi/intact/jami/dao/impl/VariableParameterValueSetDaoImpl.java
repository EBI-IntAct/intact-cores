package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValueSet;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.VariableParameterValueSetDao;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValueSet;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.VariableParameterValueSetSynchronizer;

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
public class VariableParameterValueSetDaoImpl extends AbstractIntactBaseDao<VariableParameterValueSet, IntactVariableParameterValueSet> implements VariableParameterValueSetDao {

    public VariableParameterValueSetDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactVariableParameterValueSet.class, entityManager, context);
    }

    public Collection<IntactVariableParameterValueSet> getByInteractionAc(String parentAc) {
        Query query = getEntityManager().createQuery("select v from IntactInteractionEvidence i join i.variableParameterValues v " +
                "where i.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public IntactDbSynchronizer<VariableParameterValueSet, IntactVariableParameterValueSet> getDbSynchronizer() {
        return getSynchronizerContext().getVariableParameterValueSetSynchronizer();
    }
}
