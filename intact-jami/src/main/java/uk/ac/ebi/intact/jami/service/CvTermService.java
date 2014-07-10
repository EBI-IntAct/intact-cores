package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.*;

/**
 * Cv term service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "cvTermService")
@Lazy
@EnableTransactionManagement
@Configuration
public class CvTermService implements IntactService<CvTerm>{

    @Autowired
    @Qualifier("intactDao")
    private IntactDao intactDAO;
    private String objClass;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getCvTermDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<CvTerm> iterateAll() {
        return new IntactQueryResultIterator<CvTerm>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<CvTerm> fetchIntactObjects(int first, int max) {
        return new ArrayList<CvTerm>(this.intactDAO.getCvTermDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getCvTermDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<CvTerm> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<CvTerm>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<CvTerm> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<CvTerm>(this.intactDAO.getCvTermDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(CvTerm object) throws PersisterException, FinderException, SynchronizerException {
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getCvSynchronizer(this.objClass).synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends CvTerm> objects) throws SynchronizerException, PersisterException, FinderException {
        for (CvTerm cv : objects){
            saveOrUpdate(cv);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(CvTerm object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getCvSynchronizer(this.objClass).delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends CvTerm> objects) throws SynchronizerException, PersisterException, FinderException {
        for (CvTerm cv : objects){
            delete(cv);
        }
    }

    public String getObjClass() {
        return objClass;
    }

    public void setObjClass(String objClass) {
        this.objClass = objClass;
    }
}
