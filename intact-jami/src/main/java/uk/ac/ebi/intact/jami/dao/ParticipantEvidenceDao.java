package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;

import java.util.Collection;

/**
 * participant evidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ParticipantEvidenceDao<P extends IntactParticipantEvidence> extends ExperimentalEntityDao<P>{

    public Collection<P> getByInteractionAc(String ac);
}
