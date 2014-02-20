package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.ModelledEntity;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
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
public class ModelledEntityDaoImpl<T extends ModelledEntity, F extends IntactModelledEntity> extends EntityDaoImpl<T, F>
        implements ModelledEntityDao<F> {

    public ModelledEntityDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<F>)IntactModelledEntity.class, entityManager, context);
    }

    public ModelledEntityDaoImpl(Class<F> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new ModelledEntitySynchronizerTemplate<T, F>(new DefaultSynchronizerContext(getEntityManager()), getEntityClass()));
    }
}
