/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.UserContext;
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
            if ( log.isTraceEnabled() ) {
                log.trace( "Running @PrePersist/@PreUpdate on " + object );
            }
            Auditable auditable = (Auditable)object;

            final Date now = new Date();

            if (auditable.getCreated() == null) {
                auditable.setCreated(now);
            }
            auditable.setUpdated(now);

            String currentUser = "INTACT";

            if (IntactContext.currentInstanceExists() && IntactContext.getCurrentInstance().getSpringContext().containsBean("userContext")) {
                UserContext userContext = (UserContext) IntactContext.getCurrentInstance().getSpringContext().getBean("userContext");

                try {
                    if (userContext.getUserId() != null) {
                        currentUser = userContext.getUserId().toUpperCase();
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) log.debug("Problem getting current user as the userContext is not available: "+e.getMessage());
                }
            }

            if (auditable.getCreator() == null) {
                auditable.setCreator( currentUser );
            }
            auditable.setUpdator( currentUser );
        }
    }
}
