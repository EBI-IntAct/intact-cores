package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Entity;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for causal relationship
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class CausalRelationchipSynchronizer extends AbstractIntactDbSynchronizer<CausalRelationship, IntactCausalRelationship>{


    public CausalRelationchipSynchronizer(SynchronizerContext context){
        super(context, IntactCausalRelationship.class);
    }

    public IntactCausalRelationship find(CausalRelationship object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCausalRelationship object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize relation type
        CvTerm type = object.getRelationType();
        object.setRelationType(getContext().getTopicSynchronizer().synchronize(type, true));

        // synchronize target
        Entity target = object.getTarget();
        object.setTarget(getContext().getEntitySynchronizer().synchronize(target, false));
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(IntactCausalRelationship object) {
        return object.getId();
    }

    @Override
    protected IntactCausalRelationship instantiateNewPersistentInstance(CausalRelationship object, Class<? extends IntactCausalRelationship> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IntactCausalRelationship(object.getRelationType(), object.getTarget());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<CausalRelationship, IntactCausalRelationship>(this));
    }
}
