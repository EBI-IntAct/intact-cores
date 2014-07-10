package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.ModelledInteraction;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.*;

/**
 * Modelled interaction service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "modelledInteractionService")
@Lazy
@EnableTransactionManagement
@Configuration
public class ModelledInteractionService implements IntactService<ModelledInteraction>{

    @Autowired
    @Qualifier("intactDao")
    private IntactDao intactDAO;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getComplexDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledInteraction> iterateAll() {
        return new IntactQueryResultIterator<ModelledInteraction>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(int first, int max) {
        return new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getComplexDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledInteraction> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<ModelledInteraction>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(ModelledInteraction object) throws PersisterException, FinderException, SynchronizerException {
        Complex complex;
        if (!(object instanceof Complex)){
            complex = new IntactComplex(object.getShortName() != null ? object.getShortName() : "unknown");
            InteractorCloner.copyAndOverrideBasicComplexPropertiesWithModelledInteractionProperties(object, complex);
        }
        else{
            complex = (Complex)object;
        }
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getComplexSynchronizer().synchronize(complex, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends ModelledInteraction> objects) throws SynchronizerException, PersisterException, FinderException {
        for (ModelledInteraction interaction : objects){
            saveOrUpdate(interaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(ModelledInteraction object) throws PersisterException, FinderException, SynchronizerException {
        if (object instanceof Complex){
            this.intactDAO.getSynchronizerContext().getComplexSynchronizer().delete((Complex)object);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends ModelledInteraction> objects) throws SynchronizerException, PersisterException, FinderException {
        for (ModelledInteraction interaction : objects){
            delete(interaction);
        }
    }
}
