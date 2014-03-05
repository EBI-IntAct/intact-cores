package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of xref for source/institution
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_source_xref" )
public class SourceXref extends AbstractIntactXref{

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
}
