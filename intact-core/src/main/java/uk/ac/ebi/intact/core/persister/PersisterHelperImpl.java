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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.annotations.IntactFlushMode;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;

import javax.persistence.FlushModeType;

/**
 * Helper class to reduce the code needed to save or update an Annotated object.
 *
 * @deprecated Use the CorePersister instead
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Deprecated
public class PersisterHelperImpl implements PersisterHelper {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(PersisterHelperImpl.class);

    public PersisterHelperImpl() {
    }

    @Deprecated
    public static void saveOrUpdate( IntactEntry... intactEntries ) throws PersisterException {
        IntactContext.getCurrentInstance().getPersisterHelper().save(intactEntries);
    }

    @Deprecated
    public static PersisterStatistics saveOrUpdate( AnnotatedObject... annotatedObjects ) throws PersisterException {
        return IntactContext.getCurrentInstance().getPersisterHelper().save(annotatedObjects);
    }

    @Deprecated
    public void save( IntactEntry... intactEntries ) throws PersisterException {
        for ( IntactEntry intactEntry : intactEntries ) {
            for ( Interaction interaction : intactEntry.getInteractions() ) {
                save( interaction );
            }
        }
    }

    @Transactional
    @IntactFlushMode(FlushModeType.COMMIT)
    @Deprecated
    public PersisterStatistics save( AnnotatedObject... annotatedObjects ) throws PersisterException {
        CorePersister corePersister = getCorePersister();
        corePersister.getStatistics().reset();
        corePersister.saveOrUpdate(annotatedObjects);

        return corePersister.getStatistics();

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Deprecated
    public PersisterStatistics saveInNewTransaction(AnnotatedObject... annotatedObjects ) throws PersisterException {
        return save(annotatedObjects);
    }

    @Deprecated
    public CorePersister getCorePersister() {
        return IntactContext.getCurrentInstance().getCorePersister();
    }
}