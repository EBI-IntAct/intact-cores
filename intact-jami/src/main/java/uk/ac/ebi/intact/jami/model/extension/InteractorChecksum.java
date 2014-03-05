package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of checksum for interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_molecule_checksum" )
public class InteractorChecksum extends AbstractIntactChecksum{

    public InteractorChecksum() {
    }

    public InteractorChecksum(CvTerm method, String value) {
        super(method, value);
    }
}
