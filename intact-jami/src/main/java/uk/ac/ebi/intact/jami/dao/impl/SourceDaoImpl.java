package uk.ac.ebi.intact.jami.dao.impl;

import uk.ac.ebi.intact.jami.dao.SourceDao;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

import java.util.Collection;

/**
 * Implementation of source Dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public class SourceDaoImpl extends AbstractIntactBaseDao<IntactSource> implements SourceDao{
    public IntactSource getByAc(String ac, boolean prefetchXrefs) {
        return null;
    }

    public IntactSource getByShortLabel(String value) {
        return null;
    }

    public Collection<IntactSource> getByShortLabelLike(String value) {
        return null;
    }

    public Collection<IntactSource> getByShortLabelLike(String value, int firstResult, int maxResults) {
        return null;
    }

    public Collection<IntactSource> getByShortLabelLike(String value, int firstResult, int maxResults, boolean orderAsc) {
        return null;
    }

    public Collection<IntactSource> getByXref(String primaryId) {
        return null;
    }

    public Collection<IntactSource> getByXrefLike(String primaryId) {
        return null;
    }

    public Collection<IntactSource> getByXref(String dbName, String dbMI, String primaryId) {
        return null;
    }

    public Collection<IntactSource> getByXrefLike(String dbName, String dbMI, String primaryId) {
        return null;
    }

    public Collection<IntactSource> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        return null;
    }

    public Collection<IntactSource> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        return null;
    }

    public Collection<IntactSource> getByAnnotationTopic(String topicName, String topicMI) {
        return null;
    }

    public Collection<IntactSource> getByAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        return null;
    }

    public Collection<IntactSource> getByAliasName(String name) {
        return null;
    }

    public Collection<IntactSource> getByAliasTypeAndName(String typeName, String typeMI, String name) {
        return null;
    }

    public IntactSource getByMIIdentifier(String primaryId) {
        return null;
    }

    public IntactSource getByPARIdentifier(String primaryId) {
        return null;
    }
}
