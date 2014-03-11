package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface EntityDao<F extends AbstractIntactEntity> extends IntactBaseDao<F> {

    public F getByAc(String ac);

    public Collection<F> getByShortName(String value);

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

    public Collection<F> getByBiologicalRole(String typeName, String typeMI, int first, int max);

    public Collection<F> getByCausalRelationship(String effectName, String effectMI, String targetAc);

    public Collection<F> getByCausalRelationship(String targetAc);

    public Collection<F> getByInteractorAc(String ac, int first, int max);

    public Collection<F> getByCausalRelationType(String typeName, String typeMI);

    public Collection<F> getByCausalRelationshipTargetAc(String ac);
}
