package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for xrefs
 * It does not cache persisted xrefs. It only synchronize the xref database and qualifier (with persist = true) to make sure that the database and qualifier
 * are persisted before so the xref can be persisted
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
        prepareDatabase(object, true);

        // check qualifier
        prepareQualifier(object, true);
    }

    protected void prepareQualifier(X object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (object.getQualifier() != null){
            CvTerm qualifier = object.getQualifier();
            object.setQualifier(enableSynchronization ?
                    getContext().getQualifierSynchronizer().synchronize(qualifier, true) :
                    getContext().getQualifierSynchronizer().convertToPersistentObject(qualifier));
        }
    }

    protected void prepareDatabase(X object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        CvTerm db = object.getDatabase();
        object.setDatabase(enableSynchronization ?
                getContext().getDatabaseSynchronizer().synchronize(db, true) :
                getContext().getDatabaseSynchronizer().convertToPersistentObject(db));
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
    protected boolean isObjectAlreadyConvertedToPersistableInstance(Xref object) {
        return false;
    }

    @Override
    protected X fetchMatchingPersistableObject(Xref object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(X object) throws SynchronizerException, PersisterException, FinderException {
        // database first
        prepareDatabase(object, false);

        // check qualifier
        prepareQualifier(object, false);
    }

    @Override
    protected void storePersistableObjectInCache(Xref originalObject, X persistableObject) {
         // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<Xref, X>(this));
    }
}
