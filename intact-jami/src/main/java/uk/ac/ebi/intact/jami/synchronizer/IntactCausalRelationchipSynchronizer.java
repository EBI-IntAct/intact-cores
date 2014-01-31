package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for causal relationship
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactCausalRelationchipSynchronizer extends AbstractIntactDbSynchronizer<CausalRelationship, IntactCausalRelationship> {

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer;

    private static final Log log = LogFactory.getLog(IntactCausalRelationchipSynchronizer.class);

    public IntactCausalRelationchipSynchronizer(EntityManager entityManager){
        super(entityManager, IntactCausalRelationship.class);
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public IntactCausalRelationchipSynchronizer(EntityManager entityManager, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer){
        super(entityManager, IntactCausalRelationship.class);
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public IntactCausalRelationship find(CausalRelationship object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCausalRelationship object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize relation type
        CvTerm type = object.getRelationType();
        object.setRelationType(typeSynchronizer.synchronize(type, true));
    }

    public void clearCache() {
        this.typeSynchronizer.clearCache();
    }

    @Override
    protected Object extractIdentifier(IntactCausalRelationship object) {
        return object.getId();
    }

    @Override
    protected IntactCausalRelationship instantiateNewPersistentInstance(CausalRelationship object, Class<? extends IntactCausalRelationship> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IntactCausalRelationship(object.getRelationType(), object.getTarget());
    }
}
