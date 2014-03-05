package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_controlledvocab_annot" )
public class CvTermAnnotation extends AbstractIntactAnnotation{

    public CvTermAnnotation() {
        super();
    }

    public CvTermAnnotation(CvTerm topic) {
        super(topic);
    }

    public CvTermAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
