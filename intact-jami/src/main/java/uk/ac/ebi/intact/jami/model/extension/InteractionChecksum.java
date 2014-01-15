package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Interaction;

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
@Table( name = "ia_interaction_checksum" )
public class InteractionChecksum extends AbstractIntactChecksum{

    private Interaction parent;

    public InteractionChecksum() {
    }

    public InteractionChecksum(CvTerm method, String value) {
        super(method, value);
    }

    @ManyToOne( targetEntity = IntactInteraction.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactInteraction.class)
    public Interaction getParent() {
        return parent;
    }

    public void setParent(Interaction parent) {
        this.parent = parent;
    }
}
