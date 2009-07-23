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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.annotations.IntactFlushMode;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

/**
 * Helper class to reduce the code needed to save or update an Annotated object.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class PersisterHelper {

    @PersistenceContext(unitName = "intact-core-default")
    private EntityManager entityManager;

    @Autowired
    private ApplicationContext springContext;

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(PersisterHelper.class);

    public PersisterHelper() {}

    @Deprecated
    public static void saveOrUpdate( IntactEntry... intactEntries ) throws PersisterException {
        IntactContext.getCurrentInstance().getPersisterHelper().save(intactEntries);
    }

    @Deprecated
    public static PersisterStatistics saveOrUpdate( AnnotatedObject... annotatedObjects ) throws PersisterException {
        return IntactContext.getCurrentInstance().getPersisterHelper().save(annotatedObjects);
    }

    public void save( IntactEntry... intactEntries ) throws PersisterException {
        for ( IntactEntry intactEntry : intactEntries ) {
            for ( Interaction interaction : intactEntry.getInteractions() ) {
                save( interaction );
            }
        }
    }


    @Transactional
    @IntactFlushMode(FlushModeType.COMMIT)
    public PersisterStatistics save( AnnotatedObject... annotatedObjects ) throws PersisterException {

        CorePersister corePersister = getCorePersister();

        corePersister.getStatistics().reset();

            for (AnnotatedObject ao : annotatedObjects) {
                corePersister.synchronize(ao);
            }
        corePersister.commit();

        // we reload the annotated objects by its AC
        // note: if an object does not have one, it is probably a duplicate
        for ( AnnotatedObject ao : annotatedObjects ) {
            corePersister.reload( ao );
        }
        
        final PersisterStatistics stats = corePersister.getStatistics();

        if (log.isDebugEnabled()) log.debug(stats);

        return stats;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PersisterStatistics saveInNewTransaction(AnnotatedObject... annotatedObjects ) throws PersisterException {
        return save(annotatedObjects);
    }

    public CorePersister getCorePersister() {
        return (CorePersister) springContext.getBean("corePersister");
    }
}