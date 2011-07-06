package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Controlled vocabulary aimed at describing the status of a publication in the IntAct curation pipeline.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvLifecycleEvent" )
@EditorTopic
public class CvLifecycleEvent extends CvDagObject implements Editable {

    public static final String EVENT_MI = "PL:0002";
    public static final String EVENT = "lifecycle event";

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public CvLifecycleEvent() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvLifecycleEvent instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvLifecycleEvent
     * @param owner      The Institution which owns this CvLifecycleEvent
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvLifecycleEvent( Institution owner, String shortLabel ) {
        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }
}
