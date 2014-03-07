package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_annotation" )
public class InteractionAnnotation extends AbstractIntactAnnotation{

    public InteractionAnnotation() {
        super();
    }

    public InteractionAnnotation(CvTerm topic) {
        super(topic);
    }

    public InteractionAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
