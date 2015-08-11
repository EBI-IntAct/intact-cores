package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;

import java.util.Collection;

/**
 * participant evidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ParticipantEvidenceDao extends ParticipantDao<IntactParticipantEvidence>{

    public Collection<IntactParticipantEvidence> getByExperimentalRole(String typeName, String typeMI, int first, int max);

    public Collection<IntactParticipantEvidence> getByExperimentalPreparation(String name, String mi, int first, int max);

    public Collection<IntactParticipantEvidence> getByDetectionMethod(String name, String mi, int first, int max);

    public Collection<IntactParticipantEvidence> getByExpressedInTaxid(String taxid, int first, int max);

    public Collection<IntactParticipantEvidence> getByExpressedInAc(String ac, int first, int max);

    public Collection<IntactParticipantEvidence> getByConfidence(String typeName, String typeMI, String value, int first, int max);

    public Collection<IntactParticipantEvidence> getByInteractionAc(String ac);

    public Collection<IntactParticipantEvidence> getByParameterType(String typeName, String typeMI);

    public Collection<IntactParticipantEvidence> getByParameterUnit(String unitName, String unitMI);

    public Collection<IntactParticipantEvidence> getByParameterTypeAndUnit(String typeName, String typeMI, String unitName, String unitMI);

    public Collection<IntactParticipantEvidence> getByCausalRelationship(String effectName, String effectMI, String targetAc, boolean isExperimental);

    public Collection<IntactParticipantEvidence> getByCausalRelationType(String typeName, String typeMI);

    public Collection<IntactParticipantEvidence> getByCausalRelationshipTargetAc(String ac, boolean isExperimental);

    public Collection<Confidence> getConfidencesForParticipant(String ac);

    public Collection<Parameter> getParametersForParticipant(String ac);

    public int countConfidencesForParticipant(String ac);

    public int countParametersForParticipant(String ac);

    public int countExperimentalPreparationsForParticipant(String ac);

    public int countIdentificationMethodsForParticipant(String ac);

    public int countParticipantsByExpressedInOrganism(String organismAc);
}
