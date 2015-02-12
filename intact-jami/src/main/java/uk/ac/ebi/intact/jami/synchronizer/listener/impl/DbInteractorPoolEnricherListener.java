package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.InteractorPoolEnricherListener;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.InteractorSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.InteractorUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.List;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbInteractorPoolEnricherListener extends DbInteractorEnricherListener<InteractorPool> implements InteractorPoolEnricherListener {

    public DbInteractorPoolEnricherListener(SynchronizerContext context, InteractorSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(InteractorPool object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        if (getInteractorUpdates().containsKey(object)){
            InteractorUpdates updates = getInteractorUpdates().get(object);
            if (!updates.getAddedInteractors().isEmpty()){

                List<Interactor> synchronizedInteractors = IntactEnricherUtils.synchronizeInteractorsToEnrich(updates.getAddedInteractors(),
                        getDbSynchronizer());
                object.removeAll(updates.getAddedInteractors());
                for (Interactor obj : synchronizedInteractors){
                    if (!object.contains(obj)){
                        object.add(obj);
                    }
                }
            }
        }
    }

    @Override
    protected void processOtherUpdates(InteractorPool object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        if (getInteractorUpdates().containsKey(object)){
            InteractorUpdates updates = getInteractorUpdates().get(object);
            if (!updates.getAddedInteractors().isEmpty()){

                List<Interactor> synchronizedInteractors = IntactEnricherUtils.synchronizeInteractorsToEnrich(updates.getAddedInteractors(),
                        getDbSynchronizer());
                object.removeAll(updates.getAddedInteractors());
                for (Interactor obj : synchronizedInteractors){
                    if (!object.contains(obj)){
                        object.add(obj);
                    }
                }
            }
        }
    }

    @Override
    public void onAddedInteractor(InteractorPool interactors, Interactor interactor) {
        if (getInteractorUpdates().containsKey(interactor)){
            getInteractorUpdates().get(interactor).getAddedInteractors().add(interactor);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedInteractors().add(interactor);
            getInteractorUpdates().put(interactor, updates);
        }
    }

    @Override
    public void onRemovedInteractor(InteractorPool interactors, Interactor interactor) {
        // nothing to do
    }
}
