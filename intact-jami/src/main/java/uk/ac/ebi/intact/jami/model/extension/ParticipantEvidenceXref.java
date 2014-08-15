package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of xref for entities/participants
 *
 * Note: for backward compatibility, experimental entity xrefs and modelled entity xrefs are in the same table.
 * In the future, we plan to have different tables and that is why we have different implementations of Xref for experimental
 * and modelled entities. In the future, this class will not extend ModelledParticipantXref but will extend AbstractIntactXref
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_xref" )
public class ParticipantEvidenceXref extends AbstractIntactXref {

    protected ParticipantEvidenceXref() {
    }

    public ParticipantEvidenceXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public ParticipantEvidenceXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public ParticipantEvidenceXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public ParticipantEvidenceXref(CvTerm database, String id) {
        super(database, id);
    }
}
