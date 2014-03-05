package uk.ac.ebi.intact.jami.model;

import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Publication lifecycle event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@DiscriminatorValue("publication")
public class PublicationLifecycleEvent extends AbstractLifecycleEvent {

    public PublicationLifecycleEvent() {
    }

    public PublicationLifecycleEvent(CvTerm event, User who, Date when, String note) {
        super(event, who, when, note);
    }

    /**
     * Date is set to current time.
     * @param event
     * @param who
     * @param note
     */
    public PublicationLifecycleEvent(CvTerm event, User who, String note) {
        super( event, who, new Date(), note );
    }
}
