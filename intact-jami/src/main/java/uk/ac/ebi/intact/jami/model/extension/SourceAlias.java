package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Source;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of alias for source/institution
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_institution_alias" )
public class SourceAlias extends AbstractIntactAlias{

    private Source parent;

    protected SourceAlias() {
    }

    public SourceAlias(CvTerm type, String name) {
        super(type, name);
    }

    public SourceAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = IntactSource.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactSource.class)
    public Source getParent() {
        return parent;
    }

    public void setParent(Source parent) {
        this.parent = parent;
    }
}
