package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.AbstractMIEnricher;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

/**
 * This merger will always ignore the updates in obj1 and keep the loaded object from the database.
 * If the object loaded from the database is null, it will return the firs object.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactDbMergerIgnoringLocalObject<I,T extends Auditable> extends AbstractMIEnricher<I> implements IntactDbMerger<I,T>{

    private IntactDbSynchronizer<I,T> dbSynchronizer;

    public IntactDbMergerIgnoringLocalObject(IntactDbSynchronizer<I, T> dbSynchronizer){
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The db synchronizer is required.");
        }
        this.dbSynchronizer = dbSynchronizer;
    }

    /**
     * This merger will always ignore the updates in obj1 and keep the loaded object from the database.
     * If the object loaded from the database is null, it will return the firs object.
     * @param obj1 : first object
     * @param obj2 : second object loaded from the database
     * @return
     */
    public T merge(T obj1, T obj2) {
        return obj2 != null ? obj2 : obj1;
    }

    public IntactDbSynchronizer<I, T> getDbSynchronizer() {
        return this.dbSynchronizer;
    }

    public void enrich(I objectToEnrich, I objectSource) throws EnricherException {
        // do nothing
    }

    @Override
    public I find(I objectToEnrich) throws EnricherException {
        try {
            return (I)this.dbSynchronizer.find(objectToEnrich);
        } catch (FinderException e) {
            throw new EnricherException("Cannot find the object in IntAct database", e);
        }
    }

    @Override
    protected void onEnrichedVersionNotFound(I objectToEnrich) throws EnricherException {
         // nothing to do
    }
}
