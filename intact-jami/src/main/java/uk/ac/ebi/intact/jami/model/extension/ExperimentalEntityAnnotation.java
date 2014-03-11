package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for entities/participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_annotation" )
public class ExperimentalEntityAnnotation extends AbstractIntactAnnotation{

    public ExperimentalEntityAnnotation() {
        super();
    }

    public ExperimentalEntityAnnotation(CvTerm topic) {
        super(topic);
    }

    public ExperimentalEntityAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
