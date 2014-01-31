package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for alias
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactAliasSynchronizer<A extends AbstractIntactAlias> extends AbstractIntactDbSynchronizer<Alias, A> {

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer;

    private static final Log log = LogFactory.getLog(IntactAliasSynchronizer.class);

    public IntactAliasSynchronizer(EntityManager entityManager, Class<? extends A> aliasClass){
        super(entityManager, aliasClass);
        if (aliasClass.isAssignableFrom(CvTermAlias.class)){
            this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.ALIAS_TYPE_OBJCLASS,
                    (IntactAliasSynchronizer<CvTermAlias>)this, null, null);
        }
        else{
            this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.ALIAS_TYPE_OBJCLASS);
        }
    }

    public IntactAliasSynchronizer(EntityManager entityManager, Class<? extends A> aliasClass, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer){
        super(entityManager, aliasClass);
        if (aliasClass.isAssignableFrom(CvTermAlias.class)){
            this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.ALIAS_TYPE_OBJCLASS,
                    (IntactAliasSynchronizer<CvTermAlias>)this, null, null);
        }
        else{
            this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.ALIAS_TYPE_OBJCLASS);
        }

    }

    public A find(Alias object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        if (object.getType() != null){
            CvTerm type = object.getType();
            object.setType(typeSynchronizer.synchronize(type, true));
        }
        // check alias name
        if (object.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
            log.warn("Alias name too long: "+object.getName()+", will be truncated to "+ IntactUtils.MAX_ALIAS_NAME_LEN+" characters.");
            object.setName(object.getName().substring(0, IntactUtils.MAX_ALIAS_NAME_LEN));
        }
    }

    public void clearCache() {
        this.typeSynchronizer.clearCache();
    }

    @Override
    protected Object extractIdentifier(A object) {
        return object.getAc();
    }

    @Override
    protected A instantiateNewPersistentInstance(Alias object, Class<? extends A> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getName());
    }
}
