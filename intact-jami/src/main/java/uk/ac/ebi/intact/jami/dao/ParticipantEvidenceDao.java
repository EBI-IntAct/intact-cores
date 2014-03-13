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

public interface ParticipantEvidenceDao<P extends IntactParticipantEvidence> extends ParticipantDao<P>{

    public Collection<P> getByExperimentalRole(String typeName, String typeMI, int first, int max);

    public Collection<P> getByExperimentalPreparation(String name, String mi, int first, int max);

    public Collection<P> getByDetectionMethod(String name, String mi, int first, int max);

    public Collection<P> getByExpressedInTaxid(String taxid, int first, int max);

    public Collection<P> getByExpressedInAc(String ac, int first, int max);

    public Collection<P> getByConfidence(String typeName, String typeMI, String value, int first, int max);

    public Collection<P> getByInteractionAc(String ac);
}
