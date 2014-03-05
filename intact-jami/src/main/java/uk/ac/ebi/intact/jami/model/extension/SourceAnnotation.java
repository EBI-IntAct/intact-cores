package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for source/institution
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_institution_annot" )
public class SourceAnnotation extends AbstractIntactAnnotation{

    public SourceAnnotation() {
        super();
    }

    public SourceAnnotation(CvTerm topic) {
        super(topic);
    }

    public SourceAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
