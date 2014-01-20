package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    private psidev.psi.mi.jami.model.Entity parent;

    protected EntityAlias() {
    }

    public EntityAlias(CvTerm type, String name) {
        super(type, name);
    }

    public EntityAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = AbstractIntactEntity.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac")
    @Target(AbstractIntactEntity.class)
    public psidev.psi.mi.jami.model.Entity getParent() {
        return parent;
    }

    public void setParent(psidev.psi.mi.jami.model.Entity parent) {
        this.parent = parent;
    }
}
