package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledEntity;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.utils.CvTermUtils;

import javax.persistence.*;
import java.util.Collection;

/**
 * Intact implementation of modelled feature
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@DiscriminatorValue("modelled")
public class IntactModelledFeature extends AbstractIntactFeature<ModelledEntity, ModelledFeature> implements ModelledFeature{

    public IntactModelledFeature(ModelledParticipant participant) {
        super(CvTermUtils.createBiologicalFeatureType());
        setParticipant(participant);
    }

    public IntactModelledFeature(ModelledParticipant participant, String shortName, String fullName) {
        super(shortName, fullName, CvTermUtils.createBiologicalFeatureType());
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
        super(CvTermUtils.createBiologicalFeatureType());
    }

    public IntactModelledFeature(String shortName, String fullName) {
        super(shortName, fullName, CvTermUtils.createBiologicalFeatureType());
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
}
