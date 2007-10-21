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
    public Institution syncIfTransient(Institution intactObject) {
        if (PersisterContext.getInstance().contains(intactObject)) {
            return intactObject;
        }

        Institution institution = fetchFromDataSource(intactObject);

        if (institution != null) {
            return institution;
        }

        institution = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInstitutionDao().getByShortLabel(intactObject.getShortLabel());

        if (institution == null) {
            PersisterContext.getInstance().addToPersist(intactObject);

            for (InstitutionXref xref : intactObject.getXrefs()) {
                if (log.isDebugEnabled()) log.debug("\tSaving database: "+xref.getCvDatabase().getShortLabel());
                try {
                    CvObjectPersister.getInstance().saveOrUpdate(xref.getCvDatabase());
                } catch (PersisterException e) {
                    throw new PersisterUnexpectedException(e);
                }

                CvXrefQualifier qualifier = xref.getCvXrefQualifier();
                if (qualifier != null) {
                    if (log.isDebugEnabled()) log.debug("\tSaving qualifier: "+qualifier.getShortLabel());
                    try {
                        CvObjectPersister.getInstance().saveOrUpdate(qualifier);
                    } catch (PersisterException e) {
                        throw new PersisterUnexpectedException(e);
                    }
                }
            }

            for (InstitutionAlias alias : intactObject.getAliases()) {
                CvAliasType aliasType = alias.getCvAliasType();
                if (aliasType != null) {
                    if (log.isDebugEnabled()) log.debug("\tSaving aliasType: "+aliasType.getShortLabel());
                    try {
                        CvObjectPersister.getInstance().saveOrUpdate(aliasType);
                    } catch (PersisterException e) {
                        throw new PersisterUnexpectedException(e);
                    }
                }
            }

            for (Annotation annotation : intactObject.getAnnotations()) {
                CvTopic topic = annotation.getCvTopic();
                if (topic != null) {
                    if (log.isDebugEnabled()) log.debug("\tSaving topic: "+topic.getShortLabel());
                    try {
                        CvObjectPersister.getInstance().saveOrUpdate(topic);
                    } catch (PersisterException e) {
                        throw new PersisterUnexpectedException(e);
                    }
                }
            }

            institution = intactObject;
        }

        return institution;
    }

    @Override
    protected Institution fetchFromDataSource(Institution intactObject) {

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