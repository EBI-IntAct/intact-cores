package uk.ac.ebi.intact.core.persister;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface CorePersister {
    ////////////////////////
    // Implement Persister
    @Transactional
    void saveOrUpdate( AnnotatedObject ao );

    PersisterStatistics getStatistics();

    boolean isUpdateWithoutAcEnabled();

    void setUpdateWithoutAcEnabled(boolean updateWithoutAcEnabled);

    boolean isStatisticsEnabled();

    void setStatisticsEnabled(boolean statisticsEnabled);

    public <T extends AnnotatedObject> T synchronize( T ao );

    void commit();

    void reload( AnnotatedObject ao );
}
