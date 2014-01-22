/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.model.Xref;

import java.util.Collection;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface XrefDao extends IntactBaseDao<Xref> {

    public Collection<T> getByPrimaryId(String primaryId);

    public Collection<T> getByPrimaryId(String primaryId, boolean ignoreCase);

    public Collection<T> getByPrimaryIdLike(String primaryId);

    public Collection<T> getByParentAc(String parentAc);

    public Collection<T> getByParentAc(String parentAc, boolean ignoreCase);

    public Collection<Xref> getByValue(String value);

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
