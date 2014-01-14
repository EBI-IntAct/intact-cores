package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.AllostericEffectorType;
import psidev.psi.mi.jami.model.FeatureModificationEffector;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.MoleculeEffector;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Intact implementation of feature modification effector
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Embeddable
public class IntactFeatureModificationEffector implements FeatureModificationEffector{

    private ModelledFeature feature;

    protected IntactFeatureModificationEffector(){

    }

    public IntactFeatureModificationEffector(ModelledFeature feature){
        if (feature == null){
            throw new IllegalArgumentException("The feature of a FeatureModificationEffector cannot be null.");
        }
        this.feature = feature;
    }

    @ManyToOne( targetEntity = IntactModelledFeature.class )
    @JoinColumn( name = "feature_effector_ac" )
    @Target(IntactModelledFeature.class)
    public ModelledFeature getFeatureModification() {
        return feature;
    }

    public void setFeature(ModelledFeature feature) {
        if (feature == null){
            throw new IllegalArgumentException("The feature of a FeatureModificationEffector cannot be null.");
        }
        this.feature = feature;
    }

    @Transient
    public AllostericEffectorType getEffectorType() {
        return AllostericEffectorType.feature_modification;
    }

    @Override
    public String toString() {
        return "feature modification effector: " + feature.toString();
    }
}
