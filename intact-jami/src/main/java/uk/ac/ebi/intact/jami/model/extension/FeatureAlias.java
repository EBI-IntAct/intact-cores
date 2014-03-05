package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_feature_alias" )
public class FeatureAlias extends AbstractIntactAlias{

    protected FeatureAlias() {
    }

    public FeatureAlias(CvTerm type, String name) {
        super(type, name);
    }

    public FeatureAlias(String name) {
        super(name);
    }
}
