package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Collection;

/**
 * Intact implementation of modelled participant
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@DiscriminatorValue("modelled_participant")
@Where(clause = "category = 'modelled_participant' or category = 'modelled_entity_pool'")
public class IntactModelledParticipant extends IntactModelledEntity implements ModelledParticipant{

    private String interactionAc;
    private ModelledInteraction interaction;

    protected IntactModelledParticipant() {
        super();
    }

    public IntactModelledParticipant(Interactor interactor) {
        super(interactor);
    }

    public IntactModelledParticipant(Interactor interactor, CvTerm bioRole) {
        super(interactor, bioRole);
    }

    public IntactModelledParticipant(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    public IntactModelledParticipant(Interactor interactor, CvTerm bioRole, Stoichiometry stoichiometry) {
        super(interactor, bioRole, stoichiometry);
    }

    @ManyToOne( targetEntity = IntactComplex.class )
    @JoinColumn( name = "complex_ac" )
    @Target(IntactComplex.class)
    public ModelledInteraction getInteraction() {
        return this.interaction;
    }

    public void setInteraction(ModelledInteraction interaction) {
        this.interaction = interaction;
        if (interaction instanceof IntactPrimaryObject){
            this.interactionAc = ((IntactPrimaryObject)interaction).getAc();
        }
    }

    public void setInteractionAndAddParticipant(ModelledInteraction interaction) {
        if (this.interaction != null){
            this.interaction.removeParticipant(this);
        }

        if (interaction != null){
            interaction.addParticipant(this);
        }
        if (interaction instanceof IntactPrimaryObject){
            this.interactionAc = ((IntactPrimaryObject)interaction).getAc();
        }
    }

    @Override
    @OneToMany( mappedBy = "participant", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactModelledFeature.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getFeatures() {
        return super.getFeatures();
    }

    @Override
    protected void setFeatures(Collection<ModelledFeature> features) {
        super.setFeatures(features);
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
}
