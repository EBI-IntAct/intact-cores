package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for features that are part of complexes
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_annotation" )
public class ModelledFeatureAnnotation extends AbstractIntactAnnotation{

    public ModelledFeatureAnnotation() {
        super();
    }

    public ModelledFeatureAnnotation(CvTerm topic) {
        super(topic);
    }

    public ModelledFeatureAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
