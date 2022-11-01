package uk.ac.ebi.intact.core.persister;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CoreDeleterImpl implements CoreDeleter {

    private static final Log log = LogFactory.getLog(CoreDeleterImpl.class);

    private IntactContext intactContext;

    public CoreDeleterImpl() {
        intactContext = IntactContext.getCurrentInstance();
    }

    public CoreDeleterImpl(IntactContext intactContext) {
        this.intactContext = intactContext;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(IntactObject intactObject) {
         if (intactObject.getAc() != null) {
            if (log.isDebugEnabled()) {
                log.debug("Deleting: " + DebugUtil.intactObjectToString(intactObject, false));
            }

             IntactObject ioToRemove = intactObject;
             AnnotatedObject parent = AnnotatedObjectUtils.findParent(intactObject);

             if (parent != null) {
                if (!AnnotatedObjectUtils.isChildrenInitialized(parent, intactObject)) {
                    // reload if detached
                    ioToRemove = intactContext.getDaoFactory().getEntityManager().find(intactObject.getClass(), intactObject.getAc());
                    parent = intactContext.getDaoFactory().getEntityManager().find(parent.getClass(), parent.getAc());
                 }

                 AnnotatedObjectUtils.removeChild(parent, ioToRemove);
             }
             else{
                 checkAnnotatedObjectDetachedFromOtherObjects(intactObject);
             }

             if (!intactContext.getDaoFactory().getEntityManager().contains(ioToRemove)) {
                 ioToRemove = intactContext.getDaoFactory().getEntityManager().find(intactObject.getClass(), intactObject.getAc());
             }

             if (ioToRemove != null){
                 intactContext.getDaoFactory().getEntityManager().remove(ioToRemove);
             }
        }
    }

    /**
     * Checks if the parent collection that contains children of the child type passed is initialized.
     * Can be used as a safety check before executing the removeChild() method.
     *
     * @param child
     */
    private void checkAnnotatedObjectDetachedFromOtherObjects(IntactObject child) {
        if (child instanceof Interactor) {
            int countOfParticipants = intactContext.getDaoFactory().getInteractorDao().countComponentsForInteractorWithAc(child.getAc());
            if (countOfParticipants > 0){
                throw new IntactObjectDeleteException("The interactor " + child.getAc() + " cannot be deleted because it is still used in " + countOfParticipants + " participants.");
            }

        } else if (child instanceof BioSource) {
            int countOfInteractors = intactContext.getDaoFactory().getInteractorDao().getByBioSourceAc(child.getAc()).size();
            if ( countOfInteractors > 0){
                throw new IntactObjectDeleteException("The bioSource " + child.getAc() + " cannot be deleted because it is still used in " + countOfInteractors + " interactors.");
            }

            int countOfExpressedIn = intactContext.getDaoFactory().getComponentDao().getByExpressedIn(child.getAc()).size();
            if (countOfExpressedIn > 0){
                throw new IntactObjectDeleteException("The bioSource " + child.getAc() + " cannot be deleted because it is still used in " + countOfExpressedIn + " participants as expressed in organism.");
            }

            int countExperiments = intactContext.getDaoFactory().getExperimentDao().getByHostOrganism(child.getAc()).size();
            if (countExperiments > 0){
                throw new IntactObjectDeleteException("The bioSource " + child.getAc() + " cannot be deleted because it is still used in " + countExperiments + " experiments.");
            }
        }
    }
}
