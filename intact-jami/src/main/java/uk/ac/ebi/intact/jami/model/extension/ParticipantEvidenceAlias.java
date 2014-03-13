package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;

/**
 * Implementation of alias for entities and participants
 *
 * Note: for backward compatibility, experimental entity aliases and modelled entity aliases are in the same table.
 * In the future, we plan to have different tables and that is why we have different implementations of alias for experimental
 * and modelled entities. In the future, this class will not extend ModelledParticipantAlias but will extend AbstractIntactAlias
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
public class ParticipantEvidenceAlias extends ModelledParticipantAlias {

    protected ParticipantEvidenceAlias() {
    }

    public ParticipantEvidenceAlias(CvTerm type, String name) {
        super(type, name);
    }

    public ParticipantEvidenceAlias(String name) {
        super(name);
    }
}
