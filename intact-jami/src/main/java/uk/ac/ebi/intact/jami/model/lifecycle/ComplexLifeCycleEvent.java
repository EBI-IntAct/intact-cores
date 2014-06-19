package uk.ac.ebi.intact.jami.model.lifecycle;

import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Complex lifecycle event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@Table( name = "ia_complex_lcycle_evt" )
public class ComplexLifeCycleEvent extends AbstractLifeCycleEvent {

    public ComplexLifeCycleEvent() {
    }

    public ComplexLifeCycleEvent(LifeCycleEventType event, User who, Date when, String note) {
        super(event, who, when, note);
    }

    /**
     * Date is set to current time.
     * @param event
     * @param who
     * @param note
     */
    public ComplexLifeCycleEvent(LifeCycleEventType event, User who, String note) {
        super(event, who, new Date(), note);
    }
}
