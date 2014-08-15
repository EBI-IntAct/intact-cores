package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for experiments
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_annotation" )
public class ExperimentAnnotation extends AbstractIntactAnnotation{

    protected ExperimentAnnotation() {
        super();
    }

    public ExperimentAnnotation(CvTerm topic) {
        super(topic);
    }

    public ExperimentAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
