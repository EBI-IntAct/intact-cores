package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.lifecycle.ComplexBCLifecycleEventListener;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Complex service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "complexService")
@Lazy
public class ComplexService extends AbstractReleasableLifeCycleService<IntactComplex> implements IntactService<Complex>{
    private static final Logger LOGGER = Logger.getLogger("ComplexService");

    private ComplexBCLifecycleEventListener complexBCListener = new ComplexBCLifecycleEventListener();

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return getIntactDao().getComplexDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Complex> iterateAll() {
        return new IntactQueryResultIterator<Complex>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(int first, int max) {
        return new ArrayList<Complex>(getIntactDao().getComplexDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return getIntactDao().getComplexDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Complex> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Complex>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Complex>(getIntactDao().getComplexDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<Complex>(getIntactDao().getComplexDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Complex> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Complex>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<Complex> results = new ArrayList<Complex>(getIntactDao().getComplexDao().getAll("ac", first, max));
        initialiseLazyComplex(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Complex> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Complex>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<Complex> results = new ArrayList<Complex>(getIntactDao().getComplexDao().getByQuery(query, parameters, first, max));
        initialiseLazyComplex(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<Complex> results = new ArrayList<Complex>(getIntactDao().getComplexDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyComplex(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Complex object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        // we can synchronize the complex with the database now
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().synchronize(object, true);
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public int replaceSource(IntactSource sourceInstitution, IntactSource destinationInstitution) {

        if (sourceInstitution.getAc() == null) {
            throw new IllegalArgumentException("Source institution needs to be present in the database so sourceInstitutionAc cannot be null ");
        }

        if (destinationInstitution.getAc() == null) {
            throw new IllegalArgumentException("Destination institution needs to be present in the database so destinationInstitutionAc cannot be null.");
        }

        return getIntactDao().getEntityManager().createQuery("update IntactComplex ao " +
                "set ao.source = :destInstitution " +
                "where ao.source.ac = :sourceInstitutionAc " +
                "and ao.source.ac <> :destInstitutionAc")
                .setParameter("sourceInstitutionAc", sourceInstitution.getAc())
                .setParameter("destInstitution", destinationInstitution)
                .setParameter("destInstitutionAc", destinationInstitution.getAc())
                .executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public int replaceInstitution(IntactSource destinationInstitution, String createUser) {

        if (destinationInstitution.getAc() == null) {
            throw new IllegalArgumentException("Destination institution needs to be present in the database. " +
                    "Supplied institution does not have an AC: " + destinationInstitution);
        }

        return getIntactDao().getEntityManager().createQuery("update IntactComplex ao " +
                "set ao.source = :destInstitution " +
                "where ao.currentOwner = :creator " +
                "and ao.source.ac <> :destInstitutionAc")
                .setParameter("destInstitution", destinationInstitution)
                .setParameter("creator", createUser)
                .setParameter("destInstitutionAc", destinationInstitution.getAc())
                .executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Complex> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
        for (Complex interaction : objects){
            // we can synchronize the complex with the database now
            getIntactDao().getSynchronizerContext().getComplexSynchronizer().synchronize(interaction, true);
        }
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Complex object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().delete(object);
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Complex> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
        for (Complex interaction : objects){
            getIntactDao().getSynchronizerContext().getComplexSynchronizer().delete(interaction);
        }
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().flush();
    }

    @Override
    protected IntactComplex loadReleasableByAc(String ac) {
        return getIntactDao().getComplexDao().getByAc(ac);
    }

    @Override
    protected void updateReleasable(IntactComplex releasable) {
        try {
            getIntactDao().getComplexDao().update(releasable);
        } catch (FinderException e) {
            LOGGER.log(Level.SEVERE, "Cannot update complex "+releasable.getAc(), e);
        } catch (SynchronizerException e) {
            LOGGER.log(Level.SEVERE, "Cannot update complex " + releasable.getAc(), e);
        } catch (PersisterException e) {
            LOGGER.log(Level.SEVERE, "Cannot update complex " + releasable.getAc(), e);
        }
    }

    @Override
    protected void registerListeners() {
        getLifecycleManager().registerListener(this.complexBCListener);
    }

    private void initialiseLazyComplex(boolean loadLazyCollections, List<Complex> results) {
        if (loadLazyCollections){
            for (Complex complex : results){
                IntactUtils.initialiseComplex((IntactComplex) complex);
            }
        }
    }
}
