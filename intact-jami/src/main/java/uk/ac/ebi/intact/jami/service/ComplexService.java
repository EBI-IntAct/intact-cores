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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    private IntactQuery intactQuery;

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll() {
        if (this.intactQuery != null){
            return this.intactDAO.getComplexDao().countByQuery(this.intactQuery.getCountQuery(), this.intactQuery.getQueryParameters());
        }
        return this.intactDAO.getComplexDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<Complex> iterateAll() {
        return new IntactQueryResultIterator<Complex>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Complex> fetchIntactObjects(int first, int max) {
        if (this.intactQuery != null){
            return new ArrayList<Complex>(this.intactDAO.getComplexDao().getByQuery(intactQuery.getQuery(), intactQuery.getQueryParameters(), first, max));
        }
        return new ArrayList<Complex>(this.intactDAO.getComplexDao().getAll("ac", first, max));
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

    public IntactQuery getIntactQuery() {
        return intactQuery;
    }

    public void setIntactQuery(IntactQuery intactQuery) {
        this.intactQuery = intactQuery;
    }
}