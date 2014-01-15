package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of xref for entities/participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_xref" )
public class EntityXref extends AbstractIntactXref{

    private psidev.psi.mi.jami.model.Entity parent;

    public EntityXref() {
    }

    public EntityXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public EntityXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public EntityXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public EntityXref(CvTerm database, String id) {
        super(database, id);
    }

    @ManyToOne( targetEntity = IntactEntity.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactEntity.class)
    public psidev.psi.mi.jami.model.Entity getParent() {
        return parent;
    }

    public void setParent(psidev.psi.mi.jami.model.Entity parent) {
        this.parent = parent;
    }
}
