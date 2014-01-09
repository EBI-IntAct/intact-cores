package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.NamedFeature;

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

    private NamedFeature parent;

    protected FeatureAlias() {
    }

    public FeatureAlias(CvTerm type, String name) {
        super(type, name);
    }

    public FeatureAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = IntactFeature.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactFeature.class)
    public NamedFeature getParent() {
        return parent;
    }

    public void setParent(NamedFeature parent) {
        this.parent = parent;
    }
}
