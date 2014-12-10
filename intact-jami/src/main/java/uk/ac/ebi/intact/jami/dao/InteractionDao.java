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

    /**
     * Retrieves those interactions that contain the interactors with the provided primary IDs.<br/>
     * The search is exact (only those interactions where the number of components equals the number
     * of passed primaryIDs)
     * When searching self interactions, if you pass only one primaryId it will get those interactions that only
     * contain only one component (exactComponents has to be true - otherwise the method would return all the interactions
     * where that primaryID is found). In the case where an interaction contains two or more components with the same interactor,
     * you should pass to the method as many -repeated- primaryID as components contain the interaction.
     * @param primaryIds the number of primaryIDs to search
     * @return the interactions for those primaryIDs
     *
     */
    public Collection<IntactInteractionEvidence> getByInteractorsPrimaryId(String... primaryIds);

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

    public int countParticipantsForInteraction(String ac);

    public Collection<Xref> getXrefsForInteraction(String ac);

    public Collection<Annotation> getAnnotationsForInteraction(String ac);

    public Collection<Confidence> getConfidencesForInteraction(String ac);

    public Collection<Parameter> getParametersForInteraction(String ac);

    public int countConfidencesForInteraction(String ac);

    public int countParametersForInteraction(String ac);

    public int countXrefsForInteraction(String ac);

    public int countAnnotationsForInteraction(String ac);

    public int countVariableParameterValuesSetsForInteraction(String ac);

}
