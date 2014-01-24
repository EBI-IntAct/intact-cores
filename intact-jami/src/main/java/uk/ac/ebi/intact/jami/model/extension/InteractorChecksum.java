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
 * Implementation of checksum for interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_molecule_checksum" )
public class InteractorChecksum extends AbstractIntactChecksum{

    private Interactor parent;

    public InteractorChecksum() {
    }

    public InteractorChecksum(CvTerm method, String value) {
        super(method, value);
    }

    @ManyToOne( targetEntity = IntactInteractor.class )
    @JoinColumn( name = "parent_ac" , referencedColumnName = "ac")
    @Target(IntactInteractor.class)
    public Interactor getParent() {
        return parent;
    }

    public void setParent(Interactor parent) {
        this.parent = parent;
    }
}
