package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.model.listener.FeatureDetectionMethodListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of feature evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@DiscriminatorValue("evidence")
@EntityListeners(value = {FeatureDetectionMethodListener.class})
public class IntactFeatureEvidence extends AbstractIntactFeature<ParticipantEvidence,FeatureEvidence> implements FeatureEvidence{

    private Collection<CvTerm> detectionMethods;
    private CvTerm identificationMethod;

    public IntactFeatureEvidence(ParticipantEvidence participant) {
        super();
        setParticipant(participant);
    }

    public IntactFeatureEvidence(ParticipantEvidence participant, String shortName, String fullName) {
        super(shortName, fullName);
        setParticipant(participant);
    }

    public IntactFeatureEvidence(ParticipantEvidence participant, CvTerm type) {
        super(type);
        setParticipant(participant);
    }

    public IntactFeatureEvidence(ParticipantEvidence participant, String shortName, String fullName, CvTerm type) {
        super(shortName, fullName, type);
        setParticipant(participant);
    }

    public IntactFeatureEvidence() {
        super();
    }

    public IntactFeatureEvidence(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public IntactFeatureEvidence(CvTerm type) {
        super(type);
    }

    public IntactFeatureEvidence(String shortName, String fullName, CvTerm type) {
        super(shortName, fullName, type);
    }

    @ManyToMany( targetEntity = IntactCvTerm.class)
    @JoinTable(
            name="ia_feature2method",
            joinColumns = @JoinColumn( name="feature_ac"),
            inverseJoinColumns = @JoinColumn( name="method_ac")
    )
    @Target(IntactCvTerm.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    public Collection<CvTerm> getDetectionMethods() {
        if (detectionMethods == null){
            initialiseDetectionMethods();
        }
        return this.detectionMethods;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "identification_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    /**
     * @deprecated only for intact-core backward compatibility. Use detectionMethods instead
     */
    @Deprecated
    public CvTerm getFeatureIdentification() {
        return identificationMethod;
    }

    /**
     * @deprecated only for intact-core backward compatibility. Use detectionMethods instead
     */
    @Deprecated
    public void setFeatureIdentification( CvTerm cvFeatureIdentification ) {
        this.identificationMethod = cvFeatureIdentification;
    }

    @Override
    @ManyToOne(targetEntity = IntactParticipantEvidence.class)
    @JoinColumn( name = "component_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    public ParticipantEvidence getParticipant() {
        return super.getParticipant();
    }

    @Override
    public void setParticipant(ParticipantEvidence participant) {
        super.setParticipant(participant);
    }

    @Override
    @ManyToMany( targetEntity = IntactFeatureEvidence.class)
    @JoinTable(
            name="ia_feature2linkedfeature",
            joinColumns = @JoinColumn( name="feature_ac"),
            inverseJoinColumns = @JoinColumn( name="linkedfeature_ac")
    )
    @Target(IntactFeatureEvidence.class)
    public Collection<FeatureEvidence> getLinkedFeatures() {
        return super.getLinkedFeatures();
    }

    @Override
    @ManyToOne(targetEntity = IntactFeatureEvidence.class)
    @JoinColumn( name = "linkedfeature_ac", referencedColumnName = "ac" )
    @Target(IntactFeatureEvidence.class)
    public FeatureEvidence getBinds() {
        return super.getBinds();
    }

    @Override
    public void setBinds(FeatureEvidence binds) {
        super.setBinds(binds);
    }

    @Transient
    public boolean areDetectionMethodsInitialized(){
        return Hibernate.isInitialized(getDetectionMethods());
    }

    protected void initialiseDetectionMethods(){
        this.detectionMethods = new ArrayList<CvTerm>();
    }

    @Override
    protected void setLinkedFeatures(Collection<FeatureEvidence> linkedFeatures) {
        super.setLinkedFeatures(linkedFeatures);
    }

    private void setDetectionMethods(Collection<CvTerm> detectionMethods) {
        this.detectionMethods = detectionMethods;
    }
}
