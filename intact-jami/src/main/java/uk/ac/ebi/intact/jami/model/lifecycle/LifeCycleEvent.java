package uk.ac.ebi.intact.jami.model.lifecycle;

import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.model.user.User;

import java.util.Date;

/**
 * Interface for IntAct lyfecycle event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface LifeCycleEvent extends Auditable{

    public LifeCycleEventType getEvent();

    public void setEvent( LifeCycleEventType event );

    public User getWho();

    public void setWho( User who );

    public Date getWhen();

    public void setWhen( Date when );

    public String getNote();

    public void setNote( String note );
}
