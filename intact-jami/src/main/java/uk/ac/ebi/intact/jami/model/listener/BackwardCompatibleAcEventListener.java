/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.model.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.BackwardCompatibleObjectWithAc;
import uk.ac.ebi.intact.jami.model.IntactAcGenerator;
import uk.ac.ebi.intact.jami.model.context.IntactContext;
import uk.ac.ebi.intact.jami.model.sequence.SequenceManager;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listeners automatically updates the audit information (user and dates modification columns) for any
 * object that contains these attributes.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public class BackwardCompatibleAcEventListener {

    private static final Log log = LogFactory.getLog( BackwardCompatibleAcEventListener.class );

    @PrePersist
    @PreUpdate
    public void prePersist(BackwardCompatibleObjectWithAc objectWithAc) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Running @PrePersist/@PreUpdate on " + objectWithAc );
        }

        SequenceManager sequenceManager = ApplicationContextProvider.getBean(SequenceManager.class);
        if (sequenceManager != null){
            String prefix="UNK";
            IntactContext intactContext = ApplicationContextProvider.getBean(IntactContext.class);
            if (intactContext != null) {
                prefix = intactContext.getConfig().getAcPrefix();
            }

            Long seq_id = sequenceManager.getNextValueForSequence(IntactAcGenerator.INTACT_AC_SEQUENCE_NAME);
            if (seq_id != null){

                String id = prefix + "-" + seq_id;

                log.trace( "Assigning Id: " + id );
                objectWithAc.setAc(id);
            }
            else{
                log.trace( "Could not assign any backward compatible id as the sequence "+IntactAcGenerator.INTACT_AC_SEQUENCE_NAME+" does not exist." );
            }
        }
        log.trace( "Could not assign any backward compatible id as no SequenceManager defined in the SPRING application context." );
    }
}
