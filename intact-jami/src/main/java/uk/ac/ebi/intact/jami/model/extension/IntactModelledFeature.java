package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Collection;

/**
 * Intact implementation of modelled feature
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@DiscriminatorValue("modelled")
public class IntactModelledFeature extends AbstractIntactFeature<ModelledEntity, ModelledFeature> implements ModelledFeature{

    public IntactModelledFeature(ModelledParticipant participant) {
        super();
        setParticipant(participant);
    }

    public IntactModelledFeature(ModelledParticipant participant, String shortName, String fullName) {
        super(shortName, fullName);
        setParticipant(participant);
    }

    public IntactModelledFeature(ModelledParticipant participant, CvTerm type) {
        super(type);
        setParticipant(participant);
    }

    public IntactModelledFeature(ModelledParticipant participant, String shortName, String fullName, CvTerm type) {
        super(shortName, fullName, type);
        setParticipant(participant);
    }

    public IntactModelledFeature() {
        super();
    }

    public IntactModelledFeature(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public IntactModelledFeature(CvTerm type) {
        super(type);
    }

    public IntactModelledFeature(String shortName, String fullName, CvTerm type) {
        super(shortName, fullName, type);
    }

    @Override
    @ManyToOne(targetEntity = IntactModelledParticipant.class)
    @JoinColumn( name = "component_ac", referencedColumnName = "ac" )
    @Target(IntactModelledParticipant.class)
    public ModelledEntity getParticipant() {
        return super.getParticipant();
    }

    @Override
    @ManyToMany( targetEntity = IntactModelledFeature.class)
    @JoinTable(
            name="ia_feature2linkedfeature",
            joinColumns = @JoinColumn( name="feature_ac"),
            inverseJoinColumns = @JoinColumn( name="linkedfeature_ac")
    )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getLinkedFeatures() {
        return super.getLinkedFeatures();
    }

    @Override
    @ManyToOne(targetEntity = IntactModelledFeature.class)
    @JoinColumn( name = "linkedfeature_ac", referencedColumnName = "ac" )
    @Target(IntactModelledFeature.class)
    public ModelledFeature getBinds() {
        return super.getBinds();
    }

    @Override
    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "featuretype_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getType() {
        if (super.getType() == null){
            super.setType(IntactUtils.createMIFeatureType(Feature.BIOLOGICAL_FEATURE, Feature.BIOLOGICAL_FEATURE_MI));
        }
        return super.getType();
    }
}
