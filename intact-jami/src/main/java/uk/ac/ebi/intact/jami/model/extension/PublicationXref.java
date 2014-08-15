package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of xref for publications
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_publication_xref" )
public class PublicationXref extends AbstractIntactXref{

    protected PublicationXref() {
    }

    public PublicationXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public PublicationXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public PublicationXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public PublicationXref(CvTerm database, String id) {
        super(database, id);
    }
}
