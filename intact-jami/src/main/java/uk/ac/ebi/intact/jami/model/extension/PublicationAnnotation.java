package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.model.Publication;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for publication
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_publication_annot" )
public class PublicationAnnotation extends AbstractIntactAnnotation{

    private Publication parent;

    public PublicationAnnotation() {
        super();
    }

    public PublicationAnnotation(CvTerm topic) {
        super(topic);
    }

    public PublicationAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }

    @ManyToOne( targetEntity = IntactPublication.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactPublication.class)
    public Publication getParent() {
        return parent;
    }

    public void setParent(Publication parent) {
        this.parent = parent;
    }
}
