package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for user preferences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactPreferenceSynchronizer extends AbstractIntactDbSynchronizer<Preference, Preference> {

    private static final Log log = LogFactory.getLog(IntactPreferenceSynchronizer.class);

    public IntactPreferenceSynchronizer(EntityManager entityManager){
        super(entityManager, Preference.class);
    }

    public Preference find(Preference preference) throws FinderException {
        return null;
    }

    public void synchronizeProperties(Preference object) throws FinderException, PersisterException, SynchronizerException {
        // check key
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < object.getKey().length()){
            log.warn("Preference key too long: "+object.getKey()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            object.setKey(object.getKey().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(Preference object) {
        return object.getAc();
    }

    @Override
    protected Preference instantiateNewPersistentInstance(Preference object, Class<? extends Preference> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new Preference(object.getUser(), object.getKey(), object.getValue());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Preference, Preference>(this));
    }
}
