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

public interface ModelledParticipantDao<P extends IntactModelledParticipant> extends ParticipantDao<P>{

    public Collection<P> getByInteractionAc(String ac);

    public Collection<P> getByCausalRelationship(String effectName, String effectMI, String targetAc);

    public Collection<P> getByCausalRelationType(String typeName, String typeMI);

    public Collection<P> getByCausalRelationshipTargetAc(String ac);
}
