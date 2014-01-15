package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for entities/participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_annot" )
public class EntityAnnotation extends AbstractIntactAnnotation{

    private psidev.psi.mi.jami.model.Entity parent;

    public EntityAnnotation() {
        super();
    }

    public EntityAnnotation(CvTerm topic) {
        super(topic);
    }

    public EntityAnnotation(CvTerm topic, String value) {
        super(topic, value);
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
