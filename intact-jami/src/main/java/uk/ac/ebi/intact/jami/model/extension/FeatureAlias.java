package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Feature;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    private Feature parent;

    protected FeatureAlias() {
    }

    public FeatureAlias(CvTerm type, String name) {
        super(type, name);
    }

    public FeatureAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = AbstractIntactFeature.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(AbstractIntactFeature.class)
    public Feature getParent() {
        return parent;
    }

    public void setParent(Feature parent) {
        this.parent = parent;
    }
}
