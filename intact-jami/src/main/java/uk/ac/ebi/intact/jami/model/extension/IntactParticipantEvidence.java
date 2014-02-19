package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of participant evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@javax.persistence.Entity
@DiscriminatorValue("participant_evidence")
public class IntactParticipantEvidence extends IntactExperimentalEntity implements ParticipantEvidence{

    private InteractionEvidence interaction;
    private String interactionAc;

    private Collection<CvTerm> persistentIdentificationMethods;

    protected IntactParticipantEvidence() {
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm participantIdentificationMethod) {
        super(interactor, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, stoichiometry, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, stoichiometry, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, expressedIn, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, stoichiometry, expressedIn, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor) {
        super(interactor);
    }

    public IntactParticipantEvidence(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    public void setInteractionAndAddParticipant(InteractionEvidence interaction) {

        if (this.interaction != null){
            this.interaction.removeParticipant(this);
        }

        if (interaction != null){
            interaction.addParticipant(this);
        }

        if (interaction instanceof IntactPrimaryObject){
            this.interactionAc = ((IntactPrimaryObject)interaction).getAc();
        }
        initialiseIdentificationMethods();
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "interaction_evidence_ac", referencedColumnName = "ac")
    @Target(IntactInteractionEvidence.class)
    public InteractionEvidence getInteraction() {
        return this.interaction;
    }

    public void setInteraction(InteractionEvidence interaction) {
        this.interaction = interaction;
        if (interaction instanceof IntactPrimaryObject){
            this.interactionAc = ((IntactPrimaryObject)interaction).getAc();
        }
        initialiseIdentificationMethods();
    }

    @Transient
    public Collection<CvTerm> getIdentificationMethods() {
        return super.getIdentificationMethods();
    }

    @Override
    protected void initialiseIdentificationMethods(){
        setIdentificationMethods(new IdentificationMethodList());
        ((IdentificationMethodList)getIdentificationMethods()).addAllOnly(getPersistentIdentificationMethods());
        if (getInteraction() != null && getInteraction().getExperiment() instanceof IntactExperiment){
            IntactExperiment intactExperiment = (IntactExperiment) getInteraction().getExperiment();
            if (intactExperiment.getParticipantIdentificationMethod() != null){
                ((IdentificationMethodList)getIdentificationMethods()).addOnly(intactExperiment.getParticipantIdentificationMethod());
            }
        }
    }

    /**
     *
     * @return
     * @deprecated for intact backward compatibility only
     */
    @Column(name = "interaction_ac")
    @Deprecated
    protected String getInteractionAc(){
        return this.interactionAc;
    }

    /**
     *
     * @param ac
     * @deprecated for intact backward compatibility only
     */
    @Deprecated
    protected void setInteractionAc(String ac){
        this.interactionAc = ac;
    }

    protected void setPersistentIdentificationMethods(Collection<CvTerm> persistentIdentificationMethods) {
        this.persistentIdentificationMethods = persistentIdentificationMethods;
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_component2part_detect",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    @Target(IntactCvTerm.class)
    protected Collection<CvTerm> getPersistentIdentificationMethods() {
        if (persistentIdentificationMethods == null){
            persistentIdentificationMethods = new ArrayList<CvTerm>(getIdentificationMethods());
            if (getInteraction() != null && getInteraction().getExperiment() instanceof IntactExperiment){
                IntactExperiment intactExperiment = (IntactExperiment) getInteraction().getExperiment();
                persistentIdentificationMethods.remove(intactExperiment.getParticipantIdentificationMethod());
            }
        }
        return persistentIdentificationMethods;
    }

    private class IdentificationMethodList extends AbstractListHavingProperties<CvTerm> {
        public IdentificationMethodList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(CvTerm added) {
            if (getInteraction() != null && getInteraction().getExperiment() instanceof IntactExperiment){
                IntactExperiment intactExperiment = (IntactExperiment) getInteraction().getExperiment();
                if (intactExperiment.getParticipantIdentificationMethod() != null && !added.equals(intactExperiment.getParticipantIdentificationMethod())){
                    getPersistentIdentificationMethods().add(added);
                }
            }
        }

        @Override
        protected void processRemovedObjectEvent(CvTerm removed) {
            getPersistentIdentificationMethods().remove(removed);
        }

        @Override
        protected void clearProperties() {
            getPersistentIdentificationMethods().clear();
        }
    }
}
