package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.BioactiveEntity;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.BioactiveEntityDao;
import uk.ac.ebi.intact.jami.model.extension.IntactBioactiveEntity;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;

/**
 * Implementation of bioactiveEntityDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */
public class BioactiveEntityDaoImpl extends InteractorDaoImpl<BioactiveEntity,IntactBioactiveEntity> implements BioactiveEntityDao{
    public BioactiveEntityDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactBioactiveEntity.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<BioactiveEntity, IntactBioactiveEntity> getDbSynchronizer() {
        return getSynchronizerContext().getBioactiveEntitySynchronizer();
    }
}
