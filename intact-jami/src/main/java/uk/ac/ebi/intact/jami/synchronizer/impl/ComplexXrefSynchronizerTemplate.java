package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.ComplexGOXref;
import uk.ac.ebi.intact.jami.model.extension.InteractorXref;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.Collection;
import java.util.Collections;

/**
 * Finder/persister for interactor xrefs
 * It does not cache persisted xrefs. It only synchronize the xref database and qualifier (with persist = true) to make sure that the database and qualifier
 * are persisted before so the xref can be persisted
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ComplexXrefSynchronizerTemplate extends XrefSynchronizerTemplate<InteractorXref> {

    private static final Log log = LogFactory.getLog(CvTermSynchronizer.class);

    public ComplexXrefSynchronizerTemplate(SynchronizerContext context){
        super(context, InteractorXref.class);

    }

    @Override
    public Collection<InteractorXref> findAll(Xref object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(Xref object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(InteractorXref object) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(object);
        if (object instanceof ComplexGOXref){
            // prepare ECO code
            prepareEvidenceType((ComplexGOXref)object, true);
        }
    }

    protected void prepareEvidenceType(ComplexGOXref object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (object.getEvidenceType() != null){
            CvTerm qualifier = object.getEvidenceType();
            object.setEvidenceType(enableSynchronization ?
                    getContext().getDatabaseSynchronizer().synchronize(qualifier, true) :
                    getContext().getDatabaseSynchronizer().convertToPersistentObject(qualifier));
        }
    }

    @Override
    protected void resetObjectIdentity(InteractorXref intactObject) {
        intactObject.setAc(null);
    }

    @Override
    protected void convertPersistableProperties(InteractorXref object) throws SynchronizerException, PersisterException, FinderException {
        super.convertPersistableProperties(object);
        if (object instanceof ComplexGOXref){
            // prepare ECO code
            prepareEvidenceType((ComplexGOXref)object, false);
        }
    }
}
