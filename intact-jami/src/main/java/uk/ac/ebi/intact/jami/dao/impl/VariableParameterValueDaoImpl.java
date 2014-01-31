package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.AliasDao;
import uk.ac.ebi.intact.jami.dao.VariableParameterValueDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.synchronizer.IntactAliasSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.IntactVariableParameterValueSetSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.IntactVariableParameterValueSynchronizer;

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
@Repository
public class VariableParameterValueDaoImpl extends AbstractIntactBaseDao<VariableParameterValue, IntactVariableParameterValue> implements VariableParameterValueDao {

    public VariableParameterValueDaoImpl() {
        super(IntactVariableParameterValue.class);
    }

    public VariableParameterValueDaoImpl(EntityManager entityManager) {
        super(IntactVariableParameterValue.class, entityManager);
    }

    public Collection<IntactVariableParameterValue> getByParameterAc(String parentAc) {
        Query query = getEntityManager().createQuery("select v from IntactVariableParameterValue v " +
                "join v.variableParameter as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactVariableParameterValueSynchronizer(getEntityManager()));
    }
}