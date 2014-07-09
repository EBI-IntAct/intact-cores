package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.ModelledConfidence;
import psidev.psi.mi.jami.model.ModelledParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ComplexDao extends InteractorDao<IntactComplex>{

    public Collection<IntactComplex> getByInteractionType(String typeName, String typeMI, int first, int max);

    public Collection<IntactComplex> getByLifecycleEvent(String evtName, int first, int max);

    public Collection<IntactComplex> getByStatus(String statusName, int first, int max);

    public Collection<IntactComplex> getByConfidence(String typeName, String typeMI, String value);

    /*public Collection<IntactComplex> getByCooperativeEffectAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactComplex> getByCooperativeEffectAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactComplex> getByCooperativeEffectAffectedInteractionAc(String ac);

    public Collection<IntactComplex> getByCooperativeEffectOutcome(String name, String mi);

    public Collection<IntactComplex> getByCooperativeEffectResponse(String name, String mi);

    public Collection<IntactComplex> getByAllostericMoleculeAc(String ac);

    public Collection<IntactComplex> getByAllosteryMechanism(String name, String mi);

    public Collection<IntactComplex> getByAllosteryType(String name, String mi);

    public Collection<IntactComplex> getByAllostericMoleculeEffectorAc(String ac);

    public Collection<IntactComplex> getByAllostericFeatureModificationEffectorAc(String ac);*/

    public Collection<IntactComplex> getByParameterType(String typeName, String typeMI);

    public Collection<IntactComplex> getByParameterUnit(String unitName, String unitMI);

    public Collection<IntactComplex> getByParameterTypeAndUnit(String typeName, String typeMI, String unitName, String unitMI);

    /*public Collection<IntactComplex> getByCooperativityEvidenceMethod(String methodName, String methodMI);

    public Collection<IntactComplex> getByCooperativityEvidencePublicationPubmed(String pubmed);

    public Collection<IntactComplex> getByCooperativityEvidencePublicationDoi(String doi);

    public Collection<IntactComplex> getByCooperativityEvidencePublicationAc(String ac);*/

    public Collection<LifeCycleEvent> getLifeCycleEventsForComplex(String ac);

    public long countParticipantsForComplex(String ac);

    public Collection<ModelledConfidence> getConfidencesForComplex(String ac);

    public Collection<ModelledParameter> getParametersForComplex(String ac);
}
