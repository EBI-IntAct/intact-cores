package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CooperativeEffect;
import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for cooperative effects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_cooperative_effect_annot" )
public class CooperativeEffectAnnotation extends AbstractIntactAnnotation{

    private CooperativeEffect parent;

    public CooperativeEffectAnnotation() {
        super();
    }

    public CooperativeEffectAnnotation(CvTerm topic) {
        super(topic);
    }

    public CooperativeEffectAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }

    @ManyToOne( targetEntity = AbstractIntactCooperativeEffect.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "id" )
    @Target(AbstractIntactCooperativeEffect.class)
    public CooperativeEffect getParent() {
        return parent;
    }

    public void setParent(CooperativeEffect parent) {
        this.parent = parent;
    }
}
