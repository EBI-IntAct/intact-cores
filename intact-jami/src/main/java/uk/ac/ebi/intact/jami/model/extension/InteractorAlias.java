package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Interactor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of alias for interactor
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_molecule_alias" )
public class InteractorAlias extends AbstractIntactAlias{

    private Interactor parent;

    protected InteractorAlias() {
    }

    public InteractorAlias(CvTerm type, String name) {
        super(type, name);
    }

    public InteractorAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = IntactInteractor.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactInteractor.class)
    public Interactor getParent() {
        return parent;
    }

    public void setParent(Interactor parent) {
        this.parent = parent;
    }
}
