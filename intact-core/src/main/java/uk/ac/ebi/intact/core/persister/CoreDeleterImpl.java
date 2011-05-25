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
            if (log.isDebugEnabled()) log.debug("Deleting: " + DebugUtil.intactObjectToString(intactObject, false));

             IntactObject ioToRemove = intactObject;
             AnnotatedObject parent = AnnotatedObjectUtils.findParent(intactObject);

             if (parent != null) {
                if (!AnnotatedObjectUtils.isChildrenInitialized(parent, intactObject)) {
                    // reload if detached
                    ioToRemove = intactContext.getDaoFactory().getEntityManager().find(intactObject.getClass(), intactObject.getAc());
                    parent = intactContext.getDaoFactory().getEntityManager().find(parent.getClass(), parent.getAc());
                 }

                 AnnotatedObjectUtils.removeChild(parent, ioToRemove);

//                 intactContext.getDaoFactory().getEntityManager().merge(parent);
             }
             else{
                 if (!isAnnotatedObjectDetachedFromOtherObjects(intactObject)){
                      throw new IllegalArgumentException("The " + intactObject.getClass().getSimpleName() + ", AC = " + intactObject.getAc() + " cannot be deleted because is still used in other objects in the database.");
                 }
             }

             if (!intactContext.getDaoFactory().getEntityManager().contains(ioToRemove)) {
                 ioToRemove = intactContext.getDaoFactory().getEntityManager().find(intactObject.getClass(), intactObject.getAc());
             }
             intactContext.getDaoFactory().getEntityManager().remove(ioToRemove);
        }
    }

    /**
     * Checks if the parent collection that contains children of the child type passed is initialized.
     * Can be used as a safety check before executing the removeChild() method.
     *
     * @param child
     */
    private boolean isAnnotatedObjectDetachedFromOtherObjects(IntactObject child) {
        if (child instanceof Interactor) {

            if (intactContext.getDaoFactory().getInteractorDao().countComponentsForInteractorWithAc(child.getAc()) > 0){
                return false;
            }

            return true;
        } else if (child instanceof BioSource) {

            if (intactContext.getDaoFactory().getInteractorDao().getByBioSourceAc(child.getAc()).size() > 0){
                return false;
            }

            if (intactContext.getDaoFactory().getComponentDao().getByExpressedIn(child.getAc()).size() > 0){
                return false;
            }

            if (intactContext.getDaoFactory().getExperimentDao().getByHostOrganism(child.getAc()).size() > 0){
                return false;
            }

            return true;
        }
        return true;
    }
}
