package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of xref for modelled entities/participants (used in complexes)
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_xref" )
public class ModelledParticipantXref extends AbstractIntactXref{

    public ModelledParticipantXref() {
    }

    public ModelledParticipantXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public ModelledParticipantXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public ModelledParticipantXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public ModelledParticipantXref(CvTerm database, String id) {
        super(database, id);
    }
}
