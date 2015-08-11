package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for features that are part of a modelled interaction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_feature_alias" )
public class ModelledFeatureAlias extends AbstractIntactAlias{

    protected ModelledFeatureAlias() {
    }

    public ModelledFeatureAlias(CvTerm type, String name) {
        super(type, name);
    }

    public ModelledFeatureAlias(String name) {
        super(name);
    }
}
