package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.Organism;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of alias for organism
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_biosource_alias" )
public class OrganismAlias extends AbstractIntactAlias{

    private Organism parent;

    protected OrganismAlias() {
    }

    public OrganismAlias(CvTerm type, String name) {
        super(type, name);
    }

    public OrganismAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = IntactOrganism.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactOrganism.class)
    public Organism getParent() {
        return parent;
    }

    public void setParent(Organism parent) {
        this.parent = parent;
    }
}
