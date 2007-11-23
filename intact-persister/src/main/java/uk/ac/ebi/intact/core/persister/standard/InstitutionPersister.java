package uk.ac.ebi.intact.core.persister.standard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.BehaviourType;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterUnexpectedException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionPersister extends AbstractAnnotatedObjectPersister<Institution>
{
    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(InstitutionPersister.class);

    private static ThreadLocal<InstitutionPersister> instance = new ThreadLocal<InstitutionPersister>() {
        @Override
        protected InstitutionPersister initialValue() {
            return new InstitutionPersister();
        }
    };

    public static InstitutionPersister getInstance() {
        return instance.get();
    }

    @Override
    protected void saveOrUpdateAttributes(Institution intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);
    }

     @Override
    protected Institution syncAttributes(Institution intactObject) {
        return super.syncAttributes(intactObject);
    }

    @Override
    protected Institution fetchFromDataSource(Institution intactObject) {

        if (intactObject.getAc() != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getInstitutionDao().getByAc(intactObject.getAc());
        }

        InstitutionXref idXref = null;

        for (InstitutionXref xref : intactObject.getXrefs()) {
            if (CvObjectUtils.getPsiMiIdentityXref(xref.getCvDatabase()).getPrimaryId()
                    .equals(CvDatabase.PSI_MI_MI_REF)) {
                idXref = xref;
            }
        }

        if (idXref != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getInstitutionDao().getByXref(idXref.getPrimaryId());
        }

        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getInstitutionDao().getByShortLabel(intactObject.getShortLabel());
    }

    @Override
    protected BehaviourType syncedAndCandidateAreEqual(Institution synced, Institution candidate) {
        if (synced == null) {
            return BehaviourType.NEW;
        }

        return BehaviourType.IGNORE;
    }

    @Override
    protected boolean update(Institution objectToUpdate, Institution existingObject) throws PersisterException {
        throw new UnsupportedOperationException();
    }
}