package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.*;

/**
 * Publication service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "publicationService")
@Lazy
@EnableTransactionManagement
@Configuration
public class PublicationService implements IntactService<Publication>{

    @Autowired
    @Qualifier("intactDao")
    private IntactDao intactDAO;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getPublicationDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll() {
        return new IntactQueryResultIterator<Publication>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(int first, int max) {
        return new ArrayList<Publication>(this.intactDAO.getPublicationDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getPublicationDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Publication>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Publication>(this.intactDAO.getPublicationDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Publication object) throws PersisterException, FinderException, SynchronizerException {
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getPublicationSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Publication> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Publication pub : objects){
            saveOrUpdate(pub);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Publication object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getPublicationSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Publication> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Publication pub : objects){
            delete(pub);
        }
    }
}
