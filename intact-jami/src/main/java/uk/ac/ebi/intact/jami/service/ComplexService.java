package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Complex;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.*;

/**
 * Complex service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service
@Lazy
public class ComplexService implements IntactService<Complex>{

    @Autowired
    private IntactDao intactDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll() {
        return this.intactDAO.getComplexDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<Complex> iterateAll() {
        return new IntactQueryResultIterator<Complex>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Complex> fetchIntactObjects(int first, int max) {
        return new ArrayList<Complex>(this.intactDAO.getComplexDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getComplexDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<Complex> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Complex>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Complex> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Complex>(this.intactDAO.getComplexDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Complex object) throws PersisterException, FinderException, SynchronizerException {
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getComplexSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends Complex> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Complex interaction : objects){
            saveOrUpdate(interaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Complex object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getComplexSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends Complex> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Complex interaction : objects){
            delete(interaction);
        }
    }
}
