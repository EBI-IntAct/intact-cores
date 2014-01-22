package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Annotation;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;

import java.util.Collection;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface AnnotationDao extends IntactBaseDao<Annotation> {

    public Collection<Annotation> getByValue(String value);

    public Collection<Annotation> getByValueLike(String value);

    public Collection<Annotation> getByTopic(String topicName, String topicMI);

    public Collection<Annotation> getByTopicAndValue(String topicName, String topicMI, String value);

    public Collection<Annotation> getByValue(String value, Class<? extends AbstractIntactAnnotation> annotationClass);

    public Collection<Annotation> getByValueLike(String value, Class<? extends AbstractIntactAnnotation> annotationClass);

    public Collection<Annotation> getByTopic(String topicName, String topicMI, Class<? extends AbstractIntactAnnotation> annotationClass);

    public Collection<Annotation> getByTopicAndValue(String topicName, String topicMI, String value, Class<? extends AbstractIntactAnnotation> annotationClass);

    public Collection<Annotation> getByParentAc(String parentAc, Class<? extends AbstractIntactAnnotation> annotationClass);

    public Collection<Annotation> getByName(String name, Class<? extends AbstractIntactAnnotation> annotationClass);

    public Collection<Annotation> getByNameLike(String name, Class<? extends AbstractIntactAnnotation> aliasClass);

    public Collection<Annotation> getByType(String topicName, String topicMI, Class<? extends AbstractIntactAnnotation> aliasClass);

    public Collection<Annotation> getByTypeAndName(String name, String topicName, String topicMI, Class<? extends AbstractIntactAnnotation> aliasClass);
}
