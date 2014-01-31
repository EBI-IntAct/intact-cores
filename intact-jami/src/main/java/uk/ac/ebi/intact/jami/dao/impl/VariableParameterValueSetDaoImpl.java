package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.VariableParameterValueSet;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.AliasDao;
import uk.ac.ebi.intact.jami.dao.VariableParameterValueSetDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValueSet;
import uk.ac.ebi.intact.jami.synchronizer.IntactAliasSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.IntactVariableParameterValueSetSynchronizer;

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
public class VariableParameterValueSetDaoImpl extends AbstractIntactBaseDao<VariableParameterValueSet, IntactVariableParameterValueSet> implements VariableParameterValueSetDao {

    public VariableParameterValueSetDaoImpl() {
        super(IntactVariableParameterValueSet.class);
    }

    public VariableParameterValueSetDaoImpl(EntityManager entityManager) {
        super(IntactVariableParameterValueSet.class, entityManager);
    }

    public Collection<IntactVariableParameterValueSet> getByInteractionAc(String parentAc) {
        Query query = getEntityManager().createQuery("select v from IntactVariableParameterValueSet v " +
                "join v.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactVariableParameterValueSetSynchronizer(getEntityManager()));
    }
}
