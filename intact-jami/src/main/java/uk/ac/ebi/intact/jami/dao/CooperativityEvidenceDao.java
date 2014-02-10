package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactCooperativityEvidence;

import java.util.Collection;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface CooperativityEvidenceDao extends IntactBaseDao<IntactCooperativityEvidence> {

    public Collection<IntactCooperativityEvidence> getByMethod(String methodName, String methodMI);

    public Collection<IntactCooperativityEvidence> getByPublicationPubmed(String pubmed);

    public Collection<IntactCooperativityEvidence> getByPublicationDoi(String doi);

    public Collection<IntactCooperativityEvidence> getByPublicationAc(String ac);

    public Collection<IntactCooperativityEvidence> getByCooperativeEffectId(Long id);
}
