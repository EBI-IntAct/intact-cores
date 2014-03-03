package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for xrefs
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class XrefSynchronizerTemplate<X extends AbstractIntactXref> extends AbstractIntactDbSynchronizer<Xref, X> implements XrefSynchronizer<X> {

    private static final Log log = LogFactory.getLog(CvTermSynchronizer.class);

    public XrefSynchronizerTemplate(SynchronizerContext context, Class<? extends X> xrefClass){
        super(context, xrefClass);

    }

    public X find(Xref object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(X object) throws FinderException, PersisterException, SynchronizerException {
        // database first
        CvTerm db = object.getDatabase();
        object.setDatabase(getContext().getDatabaseSynchronizer().synchronize(db, true));

        // check primaryId
        if (object.getId().length() > IntactUtils.MAX_ID_LEN){
            log.warn("Xref id too long: "+object.getId()+", will be truncated to "+ IntactUtils.MAX_ID_LEN+" characters.");
            object.setId(object.getId().substring(0, IntactUtils.MAX_ID_LEN));
        }
        // check secondaryId
        if (object.getSecondaryId() != null && object.getSecondaryId().length() > IntactUtils.MAX_SECONDARY_ID_LEN){
            log.warn("Xref secondaryId too long: "+object.getSecondaryId()+", will be truncated to "+ IntactUtils.MAX_SECONDARY_ID_LEN+" characters.");
            object.setSecondaryId(object.getSecondaryId().substring(0, IntactUtils.MAX_SECONDARY_ID_LEN));
        }
        // check version
        if (object.getVersion() != null && object.getVersion().length() > IntactUtils.MAX_DB_RELEASE_LEN){
            log.warn("Xref version too long: "+object.getVersion()+", will be truncated to "+ IntactUtils.MAX_DB_RELEASE_LEN+" characters.");
            object.setVersion(object.getVersion().substring(0, IntactUtils.MAX_DB_RELEASE_LEN));
        }
        // check qualifier
        if (object.getQualifier() != null){
            CvTerm qualifier = object.getQualifier();
            object.setQualifier(getContext().getQualifierSynchronizer().synchronize(qualifier, true));
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(X object) {
        return object.getAc();
    }

    @Override
    protected X instantiateNewPersistentInstance(Xref object, Class<? extends X> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class, String.class, CvTerm.class).newInstance(object.getDatabase(), object.getId(), object.getVersion(), object.getQualifier());
    }

    @Override
    protected void storeInCache(Xref originalObject, X persistentObject, X existingInstance) {
        // nothing to do
    }

    @Override
    protected X fetchObjectFromCache(Xref object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Xref object) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Xref, X>(this));
    }
}
