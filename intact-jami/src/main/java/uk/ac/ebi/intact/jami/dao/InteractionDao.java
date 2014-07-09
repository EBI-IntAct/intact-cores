package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface InteractionDao extends IntactBaseDao<IntactInteractionEvidence>{

    public IntactInteractionEvidence getByAc(String ac);

    public IntactInteractionEvidence getByShortName(String value);

    public Collection<IntactInteractionEvidence> getByShortNameLike(String value);

    public Collection<IntactInteractionEvidence> getByXref(String primaryId);

    public Collection<IntactInteractionEvidence> getByXrefLike(String primaryId);

    public Collection<IntactInteractionEvidence> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactInteractionEvidence> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactInteractionEvidence> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactInteractionEvidence> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactInteractionEvidence> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactInteractionEvidence> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactInteractionEvidence> getByInteractionType(String typeName, String typeMI, int first, int max);

    public Collection<IntactInteractionEvidence> getByExperimentAc(String ac);

    public Collection<IntactInteractionEvidence> getByConfidence(String typeName, String typeMI, String value);

    public Collection<IntactInteractionEvidence> getByParameterType(String typeName, String typeMI);

    public Collection<IntactInteractionEvidence> getByParameterUnit(String unitName, String unitMI);

    public Collection<IntactInteractionEvidence> getByParameterTypeAndUnit(String typeName, String typeMI, String unitName, String unitMI);

    public long countParticipantsForInteraction(String ac);

    public Collection<Xref> getXrefsForInteraction(String ac);

    public Collection<Annotation> getAnnotationsForInteraction(String ac);

    public Collection<Confidence> getConfidencesForInteraction(String ac);

    public Collection<Parameter> getParametersForInteraction(String ac);
}
