package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Interaction;
import psidev.psi.mi.jami.model.Interactor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_interactor_annot" )
public class InteractorAnnotation extends AbstractIntactAnnotation{

    private Interactor parent;

    public InteractorAnnotation() {
        super();
    }

    public InteractorAnnotation(CvTerm topic) {
        super(topic);
    }

    public InteractorAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }

    @ManyToOne( targetEntity = IntactInteractor.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactInteractor.class)
    public Interactor getParent() {
        return parent;
    }

    public void setParent(Interactor parent) {
        this.parent = parent;
    }
}
