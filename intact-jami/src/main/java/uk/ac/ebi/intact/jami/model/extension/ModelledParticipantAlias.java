package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for modelled entities and participants (used in complexes)
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_alias" )
public class ModelledParticipantAlias extends AbstractIntactAlias{

    public ModelledParticipantAlias() {
    }

    public ModelledParticipantAlias(CvTerm type, String name) {
        super(type, name);
    }

    public ModelledParticipantAlias(String name) {
        super(name);
    }
}
