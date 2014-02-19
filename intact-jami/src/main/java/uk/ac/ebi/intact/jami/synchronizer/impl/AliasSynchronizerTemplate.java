package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for alias
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
        // check alias name
        if (object.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
            log.warn("Alias name too long: "+object.getName()+", will be truncated to "+ IntactUtils.MAX_ALIAS_NAME_LEN+" characters.");
            object.setName(object.getName().substring(0, IntactUtils.MAX_ALIAS_NAME_LEN));
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Alias, A>(this));
    }
}
