package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativityEvidenceCloner;
import uk.ac.ebi.intact.jami.model.extension.*;
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
        this.methodSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.publicationSynchronizer = new IntactPublicationSynchronizer(entityManager);
    }

    public IntactCooperativityEvidenceSynchronizer(EntityManager entityManager,
                                                   IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer,
                                                   IntactDbSynchronizer<Publication, IntactPublication> publicationSynchronizer){
        super(entityManager, IntactCooperativityEvidence.class);
        this.methodSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.publicationSynchronizer = publicationSynchronizer != null ? publicationSynchronizer : new IntactPublicationSynchronizer(entityManager);
    }

    public IntactCooperativityEvidence find(CooperativityEvidence object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCooperativityEvidence object) throws FinderException, PersisterException, SynchronizerException {
        // publication first
        Publication pub = object.getPublication();
        object.setPublication(publicationSynchronizer.synchronize(pub, true));

        // check evidence methodse
        prepareEvidenceMethods(object);
    }

    public void clearCache() {
        this.methodSynchronizer.clearCache();
        this.publicationSynchronizer.clearCache();
    }

    protected void prepareEvidenceMethods(IntactCooperativityEvidence object) throws PersisterException, FinderException, SynchronizerException {

        if (object.areEvidenceMethodsInitialized()){
            Collection<CvTerm> parametersToPersist = new ArrayList<CvTerm>(object.getEvidenceMethods());
            for (CvTerm param : parametersToPersist){
                CvTerm expParam = this.methodSynchronizer.synchronize(param, true);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getEvidenceMethods().remove(param);
                    object.getEvidenceMethods().add(param);
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
}
