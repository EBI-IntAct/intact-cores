package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ParameterValue;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Intact implementation of feature evidence parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@Entity
@Table(name = "ia_feature_parameter")
public class FeatureEvidenceParameter extends AbstractIntactParameter{

    protected FeatureEvidenceParameter() {
        super();
    }

    public FeatureEvidenceParameter(CvTerm type, ParameterValue value) {
        super(type, value);
    }

    public FeatureEvidenceParameter(CvTerm type, ParameterValue value, CvTerm unit) {
        super(type, value, unit);
    }

    public FeatureEvidenceParameter(CvTerm type, ParameterValue value, CvTerm unit, BigDecimal uncertainty) {
        super(type, value, unit, uncertainty);
    }

    public FeatureEvidenceParameter(CvTerm type, ParameterValue value, BigDecimal uncertainty) {
        super(type, value, uncertainty);
    }

    public FeatureEvidenceParameter(CvTerm type, String value) throws IllegalParameterException {
        super(type, value);
    }

    public FeatureEvidenceParameter(CvTerm type, String value, CvTerm unit) throws IllegalParameterException {
        super(type, value, unit);
    }
}
