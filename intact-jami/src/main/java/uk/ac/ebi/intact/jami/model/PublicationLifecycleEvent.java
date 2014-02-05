package uk.ac.ebi.intact.jami.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.*;
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

    private Publication publication;

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

    @ManyToOne( targetEntity = IntactPublication.class, fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "publication_ac", referencedColumnName = "ac")
    @ForeignKey(name="FK_LIFECYCLE_EVENT_PUBLICATION")
    @Target(IntactPublication.class)
    public Publication getParent() {
        return publication;
    }

    public void setParent( Publication publication ) {
        this.publication = publication;
    }
}
