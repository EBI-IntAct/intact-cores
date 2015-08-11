package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;

import java.util.Collection;

/**
 * participant evidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ModelledParticipantDao extends ParticipantDao<IntactModelledParticipant>{

    public Collection<IntactModelledParticipant> getByInteractionAc(String ac);

    public Collection<IntactModelledParticipant> getByCausalRelationship(String effectName, String effectMI, String targetAc);

    public Collection<IntactModelledParticipant> getByCausalRelationType(String typeName, String typeMI);

    public Collection<IntactModelledParticipant> getByCausalRelationshipTargetAc(String ac);
}
