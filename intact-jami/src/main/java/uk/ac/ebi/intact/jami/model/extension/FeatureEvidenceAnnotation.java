package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for features that are part of an interaction evidence
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_annotation" )
public class FeatureEvidenceAnnotation extends AbstractIntactAnnotation{

    protected FeatureEvidenceAnnotation() {
        super();
    }

    public FeatureEvidenceAnnotation(CvTerm topic) {
        super(topic);
    }

    public FeatureEvidenceAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
