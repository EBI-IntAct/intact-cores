package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_feature_annot" )
public class FeatureAnnotation extends AbstractIntactAnnotation{

    public FeatureAnnotation() {
        super();
    }

    public FeatureAnnotation(CvTerm topic) {
        super(topic);
    }

    public FeatureAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
