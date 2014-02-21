package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Cv term service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service
@Lazy
public class CvTermService implements IntactService<CvTerm>{

    @Autowired
    private IntactDao intactDAO;
    private IntactQuery intactQuery;
    private String objClass;

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll() {
        if (this.intactQuery != null){
            return this.intactDAO.getCvTermDao().countByQuery(this.intactQuery.getCountQuery(), this.intactQuery.getQueryParameters());
        }
        return this.intactDAO.getCvTermDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<CvTerm> iterateAll() {
        return new IntactQueryResultIterator<CvTerm>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CvTerm> fetchIntactObjects(int first, int max) {
        if (this.intactQuery != null){
            return new ArrayList<CvTerm>(this.intactDAO.getCvTermDao().getByQuery(intactQuery.getQuery(), intactQuery.getQueryParameters(), first, max));
        }
        return new ArrayList<CvTerm>(this.intactDAO.getCvTermDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(CvTerm object) throws PersisterException, FinderException, SynchronizerException {
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getCvSynchronizer(this.objClass).synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends CvTerm> objects) throws SynchronizerException, PersisterException, FinderException {
        for (CvTerm cv : objects){
            saveOrUpdate(cv);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(CvTerm object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getCvSynchronizer(this.objClass).delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends CvTerm> objects) throws SynchronizerException, PersisterException, FinderException {
        for (CvTerm cv : objects){
            delete(cv);
        }
    }

    public IntactQuery getIntactQuery() {
        return intactQuery;
    }

    public void setIntactQuery(IntactQuery intactQuery) {
        this.intactQuery = intactQuery;
    }

    public String getObjClass() {
        return objClass;
    }

    public void setObjClass(String objClass) {
        this.objClass = objClass;
    }
}
