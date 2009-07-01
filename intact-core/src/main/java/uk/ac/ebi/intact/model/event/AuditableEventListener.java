/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Auditable;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * This listeners automatically updates the audit information (user and dates modification columns) for any
 * object that contains these attributes.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
public class AuditableEventListener {

    private static final Log log = LogFactory.getLog( AuditableEventListener.class );

    @PrePersist
    @PreUpdate
    public void prePersist(Object object) {
        if (object instanceof Auditable) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Running @PrePersist/@PreUpdate on " + object );
            }
            Auditable auditable = (Auditable)object;

            final Date now = new Date();
            
            if (auditable.getCreated() == null) {
                auditable.setCreated(now);
            }
            auditable.setUpdated(now);

            // Note: in this method we cannot assume that there is an instance of IntactContext running,
            // as it could be called during IntAct initialization (IntactConfigurator)

            String currentUser = "INTACT";

            if (IntactContext.currentInstanceExists()) {
                currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId().toUpperCase();
            }

            if (auditable.getCreator() == null) {
                auditable.setCreator( currentUser );
            }
            auditable.setUpdator( currentUser );
        }
    }

    
}
