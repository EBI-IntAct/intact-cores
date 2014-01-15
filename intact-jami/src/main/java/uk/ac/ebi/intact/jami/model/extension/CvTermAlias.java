package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of alias for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_controlledvocab_alias" )
public class CvTermAlias extends AbstractIntactAlias{

    private CvTerm parent;

    protected CvTermAlias() {
    }

    public CvTermAlias(CvTerm type, String name) {
        super(type, name);
    }

    public CvTermAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = IntactCvTerm.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getParent() {
        return parent;
    }

    public void setParent(CvTerm parent) {
        this.parent = parent;
    }
}
