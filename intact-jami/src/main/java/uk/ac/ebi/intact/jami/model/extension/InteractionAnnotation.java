package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_interaction_annot" )
public class InteractionAnnotation extends AbstractIntactAnnotation{

    private InteractionEvidence parent;

    public InteractionAnnotation() {
        super();
    }

    public InteractionAnnotation(CvTerm topic) {
        super(topic);
    }

    public InteractionAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactInteractionEvidence.class)
    public InteractionEvidence getParent() {
        return parent;
    }

    public void setParent(InteractionEvidence parent) {
        this.parent = parent;
    }
}
