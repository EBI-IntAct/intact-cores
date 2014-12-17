package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Publication service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "publicationService")
@Lazy
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class PublicationService extends AbstractReleasableLifeCycleService<IntactPublication> implements IntactService<Publication>{

    private static final Logger LOGGER = Logger.getLogger("ComplexService");

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return getIntactDao().getPublicationDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll() {
        return new IntactQueryResultIterator<Publication>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(int first, int max) {
        return new ArrayList<Publication>(getIntactDao().getPublicationDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return getIntactDao().getPublicationDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Publication>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Publication>(getIntactDao().getPublicationDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<Publication>(getIntactDao().getPublicationDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Publication>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<Publication> results = new ArrayList<Publication>(getIntactDao().getPublicationDao().getAll("ac", first, max));
        initialiseLazyPublication(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Publication>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<Publication> results = new ArrayList<Publication>(getIntactDao().getPublicationDao().getByQuery(query, parameters, first, max));
        initialiseLazyPublication(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<Publication> results = new ArrayList<Publication>(getIntactDao().getPublicationDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyPublication(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Publication object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        // we can synchronize the complex with the database now
        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().synchronize(object, true);
        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Publication> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        for (Publication pub : objects){
            // we can synchronize the complex with the database now
            getIntactDao().getSynchronizerContext().getPublicationSynchronizer().synchronize(pub, true);
        }
        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Publication object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().delete(object);

        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Publication> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        for (Publication pub : objects){
            getIntactDao().getSynchronizerContext().getPublicationSynchronizer().delete(pub);
        }
        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public int replaceSource(IntactSource sourceInstitution, IntactSource destinationInstitution) {

        if (sourceInstitution.getAc() == null) {
            throw new IllegalArgumentException("Source institution needs to be present in the database so sourceInstitutionAc cannot be null ");
        }

        if (destinationInstitution.getAc() == null) {
            throw new IllegalArgumentException("Destination institution needs to be present in the database so destinationInstitutionAc cannot be null.");
        }

        return getIntactDao().getEntityManager().createQuery("update IntactPublication ao " +
                "set ao.source = :destInstitution " +
                "where ao.source.ac = :sourceInstitutionAc " +
                "and ao.source.ac <> :destInstitutionAc")
                .setParameter("sourceInstitutionAc", sourceInstitution.getAc())
                .setParameter("destInstitution", destinationInstitution)
                .setParameter("destInstitutionAc", destinationInstitution.getAc())
                .executeUpdate();
    }

    @Override
    protected IntactPublication loadReleasableByAc(String ac) {
        return getIntactDao().getPublicationDao().getByAc(ac);
    }

    @Override
    protected void updateReleasable(IntactPublication releasable) {
        try {
            getIntactDao().getPublicationDao().update(releasable);
        } catch (FinderException e) {
            LOGGER.log(Level.SEVERE, "Cannot update publication "+releasable.getAc(), e);
        } catch (SynchronizerException e) {
            LOGGER.log(Level.SEVERE, "Cannot update publication "+releasable.getAc(), e);
        } catch (PersisterException e) {
            LOGGER.log(Level.SEVERE, "Cannot update publication "+releasable.getAc(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public int replaceSource(IntactSource destinationInstitution, String createUser) {

        if (destinationInstitution.getAc() == null) {
            throw new IllegalArgumentException("Destination institution needs to be present in the database. " +
                    "Supplied institution does not have an AC: " + destinationInstitution);
        }

        return getIntactDao().getEntityManager().createQuery("update IntactPublication ao " +
                "set ao.source = :destInstitution " +
                "where ao.currentOwner = :creator " +
                "and ao.source.ac <> :destInstitutionAc")
                .setParameter("destInstitution", destinationInstitution)
                .setParameter("creator", createUser)
                .setParameter("destInstitutionAc", destinationInstitution.getAc())
                .executeUpdate();
    }

    @Override
    protected void registerListeners() {
        // nothing to do
    }

    private void initialiseLazyPublication(boolean loadLazyCollections, List<Publication> results) {
        if (loadLazyCollections){
            for (Publication pub : results){
                IntactUtils.initialisePublication((IntactPublication) pub);
            }
        }
    }
}
