package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.PolymerDao;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;

/**
 * Implementation of polymerDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */
public class PolymerDaoImpl<T extends Polymer, P extends IntactPolymer> extends InteractorDaoImpl<T,P> implements PolymerDao<P>{

    public PolymerDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<P>)IntactPolymer.class, entityManager, context);
    }

    public PolymerDaoImpl(Class<P> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    public String getSequenceByPolymerAc(String ac) {
        IntactPolymer polymer = getEntityManager().find(IntactPolymer.class, ac);
        if (polymer == null){
            return null;
        }
        return polymer.getSequence();
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getPolymerSynchronizer();
    }
}
