package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactEntry;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface CorePersister {

    PersisterStatistics saveOrUpdate( AnnotatedObject... annotatedObjects ) throws PersisterException;

    PersisterStatistics saveOrUpdate( AnnotatedObject ao );

    PersisterStatistics saveOrUpdate( IntactEntry... intactEntries ) throws PersisterException;

    PersisterStatistics saveOrUpdateInNewTransaction(AnnotatedObject... annotatedObjects ) throws PersisterException;

    PersisterStatistics getStatistics();

    boolean isUpdateWithoutAcEnabled();

    void setUpdateWithoutAcEnabled(boolean updateWithoutAcEnabled);

    boolean isStatisticsEnabled();

    void setStatisticsEnabled(boolean statisticsEnabled);

    public <T extends AnnotatedObject> T synchronize( T ao );

    void commit();

    void reload( AnnotatedObject ao );
}
