package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for interactor
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_interactor_alias" )
public class InteractorAlias extends AbstractIntactAlias{

    public InteractorAlias() {
    }

    public InteractorAlias(CvTerm type, String name) {
        super(type, name);
    }

    public InteractorAlias(String name) {
        super(name);
    }
}
