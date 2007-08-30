package uk.ac.ebi.intact.core.persister.standard;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.Persister;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionPersister implements Persister<Institution>
{
    private static ThreadLocal<InstitutionPersister> instance = new ThreadLocal<InstitutionPersister>() {
        @Override
        protected InstitutionPersister initialValue() {
            return new InstitutionPersister();
        }
    };

    public static InstitutionPersister getInstance() {
        return instance.get();
    }

    public void saveOrUpdate(Institution intactObject) throws PersisterException
    {
        syncIfTransient(intactObject);
    }

    public Institution syncIfTransient(Institution objToSync) {
        if (objToSync.getShortLabel().equals(IntactContext.getCurrentInstance().getInstitution().getShortLabel())) {
            return IntactContext.getCurrentInstance().getInstitution();
        }

        if (PersisterContext.getInstance().contains(objToSync)) {
            return PersisterContext.getInstance().get(objToSync);
        } else {
            Institution institution = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getInstitutionDao().getByShortLabel(objToSync.getShortLabel());

            if (institution == null) {
                institution = objToSync;
                PersisterContext.getInstance().addToPersist(objToSync);
            }

            return institution;
        }
    }

    public void commit()
    {
        EntryPersister.getInstance().commit();
    }

}
