package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Interactor service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service
@Lazy
public class InteractorService implements IntactService<Interactor>{

    @Autowired
    private IntactDao intactDAO;
    private IntactQuery intactQuery;

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll() {
        if (this.intactQuery != null){
            return this.intactDAO.getInteractorBaseDao().countByQuery(this.intactQuery.getCountQuery(), this.intactQuery.getQueryParameters());
        }
        return this.intactDAO.getInteractorBaseDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<Interactor> iterateAll() {
        return new IntactQueryResultIterator<Interactor>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Interactor> fetchIntactObjects(int first, int max) {
        if (this.intactQuery != null){
            return new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getByQuery(intactQuery.getQuery(), intactQuery.getQueryParameters(), first, max));
        }
        return new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Interactor object) throws PersisterException, FinderException, SynchronizerException {
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getInteractorSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends Interactor> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Interactor interactor : objects){
            saveOrUpdate(interactor);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Interactor object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends Interactor> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Interactor interactor : objects){
            delete(interactor);
        }
    }

    public IntactQuery getIntactQuery() {
        return intactQuery;
    }

    public void setIntactQuery(IntactQuery intactQuery) {
        this.intactQuery = intactQuery;
    }
}
