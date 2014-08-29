/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.model.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.context.UserContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * This listeners automatically updates the audit information (user and dates modification columns) for any
 * object that contains these attributes.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public class AuditableEventListener {

    private static final Log log = LogFactory.getLog( AuditableEventListener.class );

    @PrePersist
    @PreUpdate
    public void prePersist(Auditable auditable) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Running @PrePersist/@PreUpdate on " + auditable );
        }

        final Date now = new Date();

        if (auditable.getCreated() == null) {
            auditable.setCreated(now);
        }
        auditable.setUpdated(now);

        String currentUser = "INTACT";
        UserContext userContext = auditable.getLocalUserContext();
        if (userContext == null){
            userContext = ApplicationContextProvider.getBean("jamiUserContext", UserContext.class);
        }

        if (userContext != null && userContext.getUserId() != null) {
            currentUser = userContext.getUserId().toUpperCase();
        }

        if (auditable.getCreator() == null) {
            auditable.setCreator( currentUser );
        }
        auditable.setUpdator( currentUser );
    }
}
