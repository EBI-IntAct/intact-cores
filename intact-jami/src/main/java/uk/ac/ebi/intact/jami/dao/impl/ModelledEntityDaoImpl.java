package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.ModelledEntity;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ModelledEntityDao;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntity;
import uk.ac.ebi.intact.jami.synchronizer.impl.ModelledEntitySynchronizerTemplate;

import javax.persistence.EntityManager;

/**
 * Implementation of modelled entity dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class ModelledEntityDaoImpl<T extends ModelledEntity, F extends IntactModelledEntity> extends EntityDaoImpl<T, F>
        implements ModelledEntityDao<F> {

    protected ModelledEntityDaoImpl() {
        super((Class<F>)IntactModelledEntity.class);
    }

    public ModelledEntityDaoImpl(Class<F> entityClass) {
        super(entityClass);
    }

    public ModelledEntityDaoImpl(Class<F> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new ModelledEntitySynchronizerTemplate<T, F>(new DefaultSynchronizerContext(getEntityManager()), getEntityClass()));
    }
}
