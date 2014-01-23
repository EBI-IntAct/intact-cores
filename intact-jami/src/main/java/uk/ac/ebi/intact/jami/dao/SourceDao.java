package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactSource;

import java.util.Collection;

/**
 * DAO factory for sources
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public interface SourceDao extends IntactBaseDao<IntactSource>{

    public IntactSource getByAc(String ac);

    public IntactSource getByShortLabel(String value);

    public Collection<IntactSource> getByShortLabelLike(String value);

    public Collection<IntactSource> getByShortLabelLike(String value, int firstResult, int maxResults);

    public Collection<IntactSource> getByShortLabelLike(String value, int firstResult, int maxResults, boolean orderAsc);

    public Collection<IntactSource> getByXref(String primaryId);

    public Collection<IntactSource> getByXrefLike(String primaryId);

    public Collection<IntactSource> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactSource> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactSource> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactSource> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactSource> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactSource> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactSource> getByAliasName(String name);

    public Collection<IntactSource> getByAliasTypeAndName(String typeName, String typeMI, String name);

    public IntactSource getByMIIdentifier(String primaryId);

    public IntactSource getByPARIdentifier(String primaryId);
}
