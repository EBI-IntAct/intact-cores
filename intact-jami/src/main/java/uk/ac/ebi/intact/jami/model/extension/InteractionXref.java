package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of xref for interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_interaction_xref" )
public class InteractionXref extends AbstractIntactXref{

    private InteractionEvidence parent;

    public InteractionXref() {
    }

    public InteractionXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public InteractionXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public InteractionXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public InteractionXref(CvTerm database, String id) {
        super(database, id);
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "parent_ac" , referencedColumnName = "ac")
    @Target(IntactInteractionEvidence.class)
    public InteractionEvidence getParent() {
        return parent;
    }

    public void setParent(InteractionEvidence parent) {
        this.parent = parent;
    }
}
