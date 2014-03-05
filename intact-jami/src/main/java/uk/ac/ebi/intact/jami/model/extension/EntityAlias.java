package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for entities and participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_alias" )
public class EntityAlias extends AbstractIntactAlias{

    protected EntityAlias() {
    }

    public EntityAlias(CvTerm type, String name) {
        super(type, name);
    }

    public EntityAlias(String name) {
        super(name);
    }
}
