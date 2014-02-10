package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;

import java.util.Collection;

/**
 * Cooperative effect DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface CooperativeEffectDao<I extends AbstractIntactCooperativeEffect> extends IntactBaseDao<I> {

    public Collection<I> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<I> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<I> getByAffectedInteractionAc(String ac);

    public Collection<I> getByComplexAc(String ac);

    public Collection<I> getByOutcome(String name, String mi);

    public Collection<I> getByResponse(String name, String mi);
}
