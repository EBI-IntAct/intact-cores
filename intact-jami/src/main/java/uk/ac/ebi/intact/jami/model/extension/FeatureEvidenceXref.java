package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of xref for features that are part of interaction evidences
 *
 * Note: for backward compatibility, experimental feature xrefs and modelled feature xrefs are in the same table.
 * In the future, we plan to have different tables and that is why we have different implementations of Xref for experimental
 * and modelled feature. In the future, this class will not extend ModelledFeatureXref but will extend AbstractIntactXref
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table(name = "ia_feature_xref")
public class FeatureEvidenceXref extends AbstractIntactXref{

    protected FeatureEvidenceXref() {
    }

    public FeatureEvidenceXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public FeatureEvidenceXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public FeatureEvidenceXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public FeatureEvidenceXref(CvTerm database, String id) {
        super(database, id);
    }
}
