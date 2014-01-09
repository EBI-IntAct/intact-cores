package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of xref for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_controlledvocab_xref" )
public class CvTermXref extends AbstractIntactXref{

    private CvTerm parent;

    public CvTermXref() {
    }

    public CvTermXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public CvTermXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public CvTermXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public CvTermXref(CvTerm database, String id) {
        super(database, id);
    }

    @ManyToOne( targetEntity = IntactCvTerm.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getParent() {
        return parent;
    }

    public void setParent(CvTerm parent) {
        this.parent = parent;
    }
}
