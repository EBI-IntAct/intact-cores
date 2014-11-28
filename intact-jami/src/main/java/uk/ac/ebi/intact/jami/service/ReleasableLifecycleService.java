package uk.ac.ebi.intact.jami.service;

import uk.ac.ebi.intact.jami.model.extension.IntactSource;

/**
 * Service interface for intact basic services
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */

public interface ReleasableLifecycleService {

    /**
     * This method will add new status and lifecyle events to a newly created releasable with a specific ac
     */
    public void createNewReleasable(String ac, String comment, String userLogin );

    /**
     * This method will assign a releasable to a specific curator
     */
    public void assignReleasableToCurator(String ac, String curatorLogin, String userLogin);

    /**
     * This method will claim ownership of a specific releasable
     */
    public void claimReleasableOwnership(String ac, String userLogin);

    /**
     * This method will reserve a new releasable
     */
    public void reserveReleasable(String ac, String comment, String userLogin);

    /**
     * This method will start curation of releasable
     */
    public void startReleasableCuration(String ac, String userLogin);

    /**
     * This method will unassign a specific releasable
     */
    public void unassignReleasable(String ac, String comment, String userLogin);

    /**
     * This method will put a releasable as ready for checking
     */
    public void readyForCheckingReleasable(String ac, String comment, String userLogin);

    /**
     * This method will accept a releasable
     */
    public void acceptReleasable(String ac, String comment, String userLogin);

    /**
     * This method will reject releasable
     */
    public void rejectReleasable(String ac, String comment, String userLogin);

    /**
     * This method will revert a releasable back to curation
     */
    public void revertReleasableFromReadForChecking(String ac, String userLogin);

    /**
     * This method will put a releasable as ready for release
     */
    public void readyForRelease(String ac, String message, String userLogin);

    /**
     * This method will remove on-hold from releasable
     */
    public void removeOnHoldFromReleasable(String ac, String message, String loginUser);

    /**
     * This method will release the releasable
     */
    public void release(String ac, String message, String userLogin);

    /**
     * This method will put a releasable on-hold
     */
    public void putReleasableOnHoldFromReadyForRelease(String ac, String message, String userLogin);

    /**
     * This method will revert the releasable back to ready for checking
     */
    public void revertReleasableToReadyForChecking(String ac, String loginUser);

    /**
     * This method will put a released releasable on hold
     */
    public void moveReleasableFromReleasedToOnHold(String ac, String message, String loginUser);

    public int replaceSource(IntactSource sourceInstitution, IntactSource destinationInstitution);

    public int replaceSource(IntactSource destinationInstitution, String createUser);
}
