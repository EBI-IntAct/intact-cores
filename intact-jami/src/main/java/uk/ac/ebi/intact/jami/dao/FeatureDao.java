package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface FeatureDao<F extends AbstractIntactFeature> extends IntactBaseDao<F>{

    public F getByAc(String ac);

    public F getByShortName(String value);

    public Collection<F> getByShortNameLike(String value);

    public Collection<F> getByXref(String primaryId);

    public Collection<F> getByXrefLike(String primaryId);

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<F> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<F> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<F> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<F> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<F> getByAliasName(String name);

    public Collection<F> getByAliasTypeAndName(String typeName, String typeMI, String name);

    public Collection<F> getByAliasNameLike(String name);

    public Collection<F> getByAliasTypeAndNameLike(String typeName, String typeMI, String name);

    public Collection<F> getByFeatureType(String typeName, String typeMI);

    public Collection<F> getByInteractionEffect(String effectName, String effectMI);

    public Collection<F> getByInteractionDependency(String dependencyName, String dependencyMI);

    public Collection<F> getByInterproIdentifier(String primaryId);

    public Collection<F> getByParticipantAc(String ac);
}
