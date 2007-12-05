/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Helper class to reduce the code needed to save or update an Annotated object.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(PersisterHelper.class);

    private PersisterHelper() {}

    public static void saveOrUpdate( IntactEntry... intactEntries ) throws PersisterException {
        for ( IntactEntry intactEntry : intactEntries ) {
            for ( Interaction interaction : intactEntry.getInteractions() ) {
                saveOrUpdate( interaction );
            }
        }
    }

    public static PersisterStatistics saveOrUpdate( AnnotatedObject... annotatedObjects ) throws PersisterException {
        boolean inTransaction = IntactContext.getCurrentInstance().getDataContext().isTransactionActive();

        if ( !inTransaction ) IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        CorePersister corePersister = new CorePersister();

        for ( AnnotatedObject ao : annotatedObjects ) {
            corePersister.synchronize( ao );
        }
        corePersister.commit();

        // we reload the annotated objects by its AC
        // note: if an object does not have one, it is probably a duplicate
        for ( AnnotatedObject ao : annotatedObjects ) {
            if (ao.getAc() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Trying to reload "+ao.getClass().getSimpleName()+" without AC. Probably a duplicate: "+ao);
                }
                DefaultFinder defaultFinder = new DefaultFinder();
                final String ac = defaultFinder.findAc(ao);

                if (ac == null) {
                    throw new PersisterException(ao.getClass().getSimpleName()+" without AC couldn't be reloaded because " +
                                                 "no equivalent object was found in the database: "+ao);
                }

                if (log.isDebugEnabled()) log.debug("\tFound AC: "+ac);

                ao.setAc(ac);
            }

            corePersister.reload( ao );
        }

        if ( !inTransaction ) corePersister.commitTransactionAndRollbackIfNecessary();

        return corePersister.getStatistics();

    }
}