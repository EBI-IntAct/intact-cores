package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for features that are part of an interaction evidence
 *
 * Note: for backward compatibility, experimental feature aliases and modelled feature aliases are in the same table.
 * In the future, we plan to have different tables and that is why we have different implementations of alias for experimental
 * and modelled features.
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_feature_alias" )
public class FeatureEvidenceAlias extends AbstractIntactAlias{

    public FeatureEvidenceAlias() {
    }

    public FeatureEvidenceAlias(CvTerm type, String name) {
        super(type, name);
    }

    public FeatureEvidenceAlias(String name) {
        super(name);
    }
}
