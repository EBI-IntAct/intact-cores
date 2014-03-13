package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidencePool;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipantPool;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ModelledParticipantPoolDao extends ModelledParticipantDao<IntactModelledParticipantPool>{

    public Collection<IntactParticipantEvidencePool> getByType(String typeName, String typeMI);
}
