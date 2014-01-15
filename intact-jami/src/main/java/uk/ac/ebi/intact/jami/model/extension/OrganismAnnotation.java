package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Organism;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for organism
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_biosource_annot" )
public class OrganismAnnotation extends AbstractIntactAnnotation{

    private Organism parent;

    public OrganismAnnotation() {
        super();
    }

    public OrganismAnnotation(CvTerm topic) {
        super(topic);
    }

    public OrganismAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }

    @ManyToOne( targetEntity = IntactOrganism.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactOrganism.class)
    public Organism getParent() {
        return parent;
    }

    public void setParent(Organism parent) {
        this.parent = parent;
    }
}
