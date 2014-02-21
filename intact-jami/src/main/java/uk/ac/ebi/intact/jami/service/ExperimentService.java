package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Experiment service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service
@Lazy
public class ExperimentService implements IntactService<Experiment>{

    @Autowired
    private IntactDao intactDAO;
    private IntactQuery intactQuery;

    @Transactional(propagation = Propagation.REQUIRED)
    public long countAll() {
        if (this.intactQuery != null){
            return this.intactDAO.getExperimentDao().countByQuery(this.intactQuery.getCountQuery(), this.intactQuery.getQueryParameters());
        }
        return this.intactDAO.getExperimentDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Iterator<Experiment> iterateAll() {
        return new IntactQueryResultIterator<Experiment>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Experiment> fetchIntactObjects(int first, int max) {
        if (this.intactQuery != null){
            return new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getByQuery(intactQuery.getQuery(), intactQuery.getQueryParameters(), first, max));
        }
        return new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Experiment object) throws PersisterException, FinderException, SynchronizerException {
        Publication pub = object.getPublication();
        // create publication first in the database if not done
        if (pub != null){
            // transcient publication to persist first
            if (!(pub instanceof IntactCuratedPublication)
                    || intactDAO.getCuratedPublicationDao().isTransient((IntactCuratedPublication)pub)){
                IntactCuratedPublication intactCuratedPub = new IntactCuratedPublication();
                PublicationCloner.copyAndOverridePublicationProperties(pub, intactCuratedPub);

                intactDAO.getCuratedPublicationDao().persist(intactCuratedPub);
                intactCuratedPub.addExperiment(object);
            }
        }
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getExperimentSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends Experiment> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Experiment exp : objects){
            saveOrUpdate(exp);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Experiment object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends Experiment> objects) throws SynchronizerException, PersisterException, FinderException {
        for (Experiment exp : objects){
            delete(exp);
        }
    }

    public IntactQuery getIntactQuery() {
        return intactQuery;
    }

    public void setIntactQuery(IntactQuery intactQuery) {
        this.intactQuery = intactQuery;
    }
}
