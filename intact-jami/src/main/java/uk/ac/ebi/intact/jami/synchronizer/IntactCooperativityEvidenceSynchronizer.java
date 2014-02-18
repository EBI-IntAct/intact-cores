package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CooperativityEvidence;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.CooperativityEvidenceCloner;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCooperativityEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactCvTermSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Default finder/synchronizer for cooperativity evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactCooperativityEvidenceSynchronizer extends AbstractIntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer;
    private IntactDbSynchronizer<Publication, IntactPublication> publicationSynchronizer;

    private static final Log log = LogFactory.getLog(IntactCooperativityEvidenceSynchronizer.class);

    public IntactCooperativityEvidenceSynchronizer(EntityManager entityManager){
        super(entityManager, IntactCooperativityEvidence.class);
    }

    public IntactCooperativityEvidence find(CooperativityEvidence object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCooperativityEvidence object) throws FinderException, PersisterException, SynchronizerException {
        // publication first
        Publication pub = object.getPublication();
        object.setPublication(getPublicationSynchronizer().synchronize(pub, true));

        // check evidence methodse
        prepareEvidenceMethods(object);
    }

    public void clearCache() {
        getMethodSynchronizer().clearCache();
        getPublicationSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getMethodSynchronizer() {
        if (this.methodSynchronizer == null){
            this.methodSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);
        }
        return methodSynchronizer;
    }

    public void setMethodSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer) {
        this.methodSynchronizer = methodSynchronizer;
    }

    public IntactDbSynchronizer<Publication, IntactPublication> getPublicationSynchronizer() {
        if (this.publicationSynchronizer == null){
            this.publicationSynchronizer = new IntactPublicationSynchronizer(getEntityManager());
        }
        return publicationSynchronizer;
    }

    public void setPublicationSynchronizer(IntactDbSynchronizer<Publication, IntactPublication> publicationSynchronizer) {
        this.publicationSynchronizer = publicationSynchronizer;
    }

    protected void prepareEvidenceMethods(IntactCooperativityEvidence object) throws PersisterException, FinderException, SynchronizerException {

        if (object.areEvidenceMethodsInitialized()){
            Collection<CvTerm> parametersToPersist = new ArrayList<CvTerm>(object.getEvidenceMethods());
            for (CvTerm param : parametersToPersist){
                CvTerm expParam = getMethodSynchronizer().synchronize(param, true);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getEvidenceMethods().remove(param);
                    object.getEvidenceMethods().add(expParam);
                }
            }
        }
    }

    @Override
    protected Object extractIdentifier(IntactCooperativityEvidence object) {
        return object.getId();
    }

    @Override
    protected IntactCooperativityEvidence instantiateNewPersistentInstance(CooperativityEvidence object, Class<? extends IntactCooperativityEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactCooperativityEvidence ev = new IntactCooperativityEvidence(object.getPublication());
        CooperativityEvidenceCloner.copyAndOverrideCooperativityEvidenceProperties(object, ev);
        return ev;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<CooperativityEvidence, IntactCooperativityEvidence>(this));
    }
}
