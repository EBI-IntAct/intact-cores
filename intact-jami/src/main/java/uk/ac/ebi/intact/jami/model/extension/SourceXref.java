package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Source;

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
@Table( name = "ia_source_xref" )
public class SourceXref extends AbstractIntactXref{

    private Source parent;

    public SourceXref() {
    }

    public SourceXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public SourceXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public SourceXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public SourceXref(CvTerm database, String id) {
        super(database, id);
    }

    @ManyToOne( targetEntity = IntactSource.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactSource.class)
    public Source getParent() {
        return parent;
    }

    public void setParent(Source parent) {
        this.parent = parent;
    }
}
