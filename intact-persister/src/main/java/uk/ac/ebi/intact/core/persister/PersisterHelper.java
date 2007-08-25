package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.*;
import uk.ac.ebi.intact.model.*;

/**
 * Helper class to reduce the code needed to save or update an Intact object
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper
{
    private PersisterHelper() {}

    public static void saveOrUpdate(IntactEntry ... intactEntries) throws PersisterException {
        for (IntactEntry intactEntry : intactEntries) {
            boolean inTransaction = IntactContext.getCurrentInstance().getDataContext().isTransactionActive();

            if (!inTransaction) IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            EntryPersister.getInstance().saveOrUpdate(intactEntry);
            EntryPersister.getInstance().commit();

            if (!inTransaction) commitTransactionAndRollbackIfNecessary();
        }
    }

    public static void saveOrUpdate(IntactObject ... intactObjects) throws PersisterException {
        boolean inTransaction = IntactContext.getCurrentInstance().getDataContext().isTransactionActive();

        if (!inTransaction) IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        for (IntactObject intactObject : intactObjects) {
            Persister persister = persisterFor(intactObject.getClass());
            persister.saveOrUpdate(intactObject);
        }
        PersisterContext.getInstance().persistAll();

        if (!inTransaction) commitTransactionAndRollbackIfNecessary();

    }

    protected static Persister persisterFor(Class<? extends IntactObject> clazz) {
        if (BioSource.class.isAssignableFrom(clazz)) {
            return BioSourcePersister.getInstance();
        } else if (Component.class.isAssignableFrom(clazz)) {
            return ComponentPersister.getInstance();
        } else if (CvObject.class.isAssignableFrom(clazz)) {
            return CvObjectPersister.getInstance();
        } else if (Experiment.class.isAssignableFrom(clazz)) {
            return ExperimentPersister.getInstance();
        } else if (Feature.class.isAssignableFrom(clazz)) {
            return FeaturePersister.getInstance();
        } else if (Institution.class.isAssignableFrom(clazz)) {
            return InstitutionPersister.getInstance();
        } else if (Interaction.class.isAssignableFrom(clazz)) {
            return InteractionPersister.getInstance();
        } else if (Interactor.class.isAssignableFrom(clazz)) {
            return InteractorPersister.getInstance();
        } else if (Publication.class.isAssignableFrom(clazz)) {
            return PublicationPersister.getInstance();
        } else {
            throw new IllegalStateException("Persister for this class not found: "+clazz.getName());
        }
    }

    private static void commitTransactionAndRollbackIfNecessary() throws PersisterException {
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
        catch (IntactTransactionException e) {
            try {
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCurrentTransaction().rollback();
            }
            catch (IntactTransactionException e1) {
                throw new PersisterException(e1);
            }
        }
    }
}
