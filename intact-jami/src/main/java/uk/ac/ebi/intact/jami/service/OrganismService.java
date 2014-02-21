package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Organism service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service
@Lazy
public class OrganismService implements IntactService<Organism>{

    @Autowired
    private IntactDao intactDAO;
    private IntactQuery intactQuery;

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll() {
        if (this.intactQuery != null){
            return this.intactDAO.getOrganismDao().countByQuery(this.intactQuery.getCountQuery(), this.intactQuery.getQueryParameters());
        }
        return this.intactDAO.getOrganismDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<Organism> iterateAll() {
        return new IntactQueryResultIterator<Organism>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Organism> fetchIntactObjects(int first, int max) {
        if (this.intactQuery != null){
            return new ArrayList<Organism>(this.intactDAO.getOrganismDao().getByQuery(intactQuery.getQuery(), intactQuery.getQueryParameters(), first, max));
        }
        return new ArrayList<Organism>(this.intactDAO.getOrganismDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Organism object) throws PersisterException, FinderException, SynchronizerException {
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getOrganismSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends Organism> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Organism org : objects){
            saveOrUpdate(org);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Organism object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends Organism> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Organism org : objects){
            delete(org);
        }
    }

    public IntactQuery getIntactQuery() {
        return intactQuery;
    }

    public void setIntactQuery(IntactQuery intactQuery) {
        this.intactQuery = intactQuery;
    }
}
