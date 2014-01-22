package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;

import java.util.Collection;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface AnnotationDao<A extends AbstractIntactAnnotation> extends IntactBaseDao<A> {

    public Collection<A> getByValue(String value);

    public Collection<A> getByValueLike(String value);

    public Collection<A> getByTopic(String topicName, String topicMI);

    public Collection<A> getByTopicAndValue(String topicName, String topicMI, String value);

    public Collection<A> getByTopicAndValueLike(String topicName, String topicMI, String value);

    public Collection<A> getByParentAc(String ac);
}
