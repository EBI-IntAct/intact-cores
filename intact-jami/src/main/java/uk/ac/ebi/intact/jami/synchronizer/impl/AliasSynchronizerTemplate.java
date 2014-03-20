package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for alias
 *
 * It does not cache persisted aliases. It only synchronize the alias type (with persist = true) to make sure that the alias type
 * is persisted before so the alias can be persisted
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class AliasSynchronizerTemplate<A extends AbstractIntactAlias> extends AbstractIntactDbSynchronizer<Alias, A> implements AliasSynchronizer<A> {

    private static final Log log = LogFactory.getLog(AliasSynchronizerTemplate.class);

    public AliasSynchronizerTemplate(SynchronizerContext context, Class<? extends A> aliasClass){
        super(context, aliasClass);
    }

    public A find(Alias object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        if (object.getType() != null){
            CvTerm type = object.getType();
            object.setType(getContext().getAliasTypeSynchronizer().synchronize(type, true));
        }
    }

    public void clearCache() {
        // nothing to clear
    }

    @Override
    protected Object extractIdentifier(A object) {
        return object.getAc();
    }

    @Override
    protected A instantiateNewPersistentInstance(Alias object, Class<? extends A> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getName());
    }

    @Override
    protected void storeInCache(Alias originalObject, A persistentObject, A existingInstance) {
        // nothing to do
    }

    @Override
    protected A fetchObjectFromCache(Alias object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Alias object) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Alias, A>(this));
    }
}
