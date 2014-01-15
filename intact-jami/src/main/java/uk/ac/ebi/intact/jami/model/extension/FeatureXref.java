package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Feature;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of xref for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_feature_xref" )
public class FeatureXref extends AbstractIntactXref{

    private Feature parent;

    public FeatureXref() {
    }

    public FeatureXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public FeatureXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public FeatureXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public FeatureXref(CvTerm database, String id) {
        super(database, id);
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
