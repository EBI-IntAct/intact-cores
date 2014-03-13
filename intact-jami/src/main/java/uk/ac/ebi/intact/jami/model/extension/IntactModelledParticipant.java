package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForceDiscriminator;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of modelled participant
 *
 * NOTE: if the participant is not a direct participant of an interaction but is part of a participantSet,
 * the interaction back reference will not be persistent. Only getDbParentPool will be persisted and getDbParentInteraction will return null
 * even if the participant has a back reference to the interaction.
 * NOTE: For backward compatibility with intact-core, a method getDbExperimentalRoles (deprecated) is present so the synchronizers can fill up a
 * 'neutral component' role for all modelled participants. This method should never be used in any applications and is public only so the synchronizers can
 * synchronize this property.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_component")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("modelled_participant")
@ForceDiscriminator
@Where(clause = "category = 'modelled_participant' or category = 'modelled_participant_pool'")
public class IntactModelledParticipant extends AbstractIntactParticipant<ModelledInteraction, ModelledFeature, ModelledParticipantPool> implements ModelledParticipant{

    private Collection<CvTerm> experimentalRoles;
    private Collection<CausalRelationship> relatedCausalRelationships;

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

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledParticipantXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ModelledParticipantXref.class)
    @Override
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledParticipantAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinTable(
            name="ia_component2annot",
            joinColumns = @JoinColumn( name="component_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Target(ModelledParticipantAnnotation.class)
    @Override
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledParticipantAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ModelledParticipantAlias.class)
    @Override
    public Collection<Alias> getAliases() {
        return super.getAliases();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledCausalRelationship.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="modelled_source_ac", referencedColumnName="ac")
    @Target(ModelledCausalRelationship.class)
    @Override
    public Collection<CausalRelationship> getCausalRelationships() {
        return super.getCausalRelationships();
    }

    @OneToMany( mappedBy = "target", targetEntity = ModelledCausalRelationship.class)
    @Target(ModelledCausalRelationship.class)
    /**
     * List of experimental causal relationships having this participant as target
     */
    public Collection<CausalRelationship> getRelatedCausalRelationships(){
        if (this.relatedCausalRelationships == null){
            this.relatedCausalRelationships = new ArrayList<CausalRelationship>();
        }
        return this.relatedCausalRelationships;
    }

    @Override
    @OneToMany( mappedBy = "participant", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactModelledFeature.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getFeatures() {
        return super.getFeatures();
    }

    @Transient
    public boolean areExperimentalRolesInitialized(){
        return Hibernate.isInitialized(getDbExperimentalRoles());
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_component2exprole",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experimentalrole_ac" )}
    )
    @Target(IntactCvTerm.class)
    /**
     * @deprecated this method is for backward compatibility only. Modelled participant DO NOT have experimental roles so
     * this method should never be called unless we need it for backward compatibility with intact-core and by the synchronizers.
     */
    @Deprecated
    public Collection<CvTerm> getDbExperimentalRoles() {
        if (this.experimentalRoles == null){
            this.experimentalRoles =  new ArrayList<CvTerm>();
        }
        if (this.experimentalRoles.isEmpty()){
            this.experimentalRoles.add(IntactUtils.createMIExperimentalRole(Participant.NEUTRAL, Participant.NEUTRAL_MI));
        }

        return experimentalRoles;
    }

    @ManyToOne( targetEntity = IntactModelledParticipantPool.class )
    @JoinColumn( name = "modelled_parent_pool_ac" )
    @Target(IntactModelledParticipantPool.class)
    @Override
    /**
     * The parent pool is not null if this participant is part of a participant pool and not a direct participant of an interaction.
     * The parent pool is important as it is used to decide if we persist the interaction_ac or not.
     * We only persist the interaction_ac if the participant is not part of a participant pool
     */
    protected ModelledParticipantPool getDbParentPool() {
        return super.getDbParentPool();
    }

    @ManyToOne( targetEntity = IntactComplex.class )
    @JoinColumn( name = "interaction_ac" )
    @Target(IntactComplex.class)
    @Override
    /**
     * The parent interaction is not null if this participant is not part of a participant pool and is a direct participant of an interaction.
     * The parent pool is important as it is used to decide if we persist the interaction_ac or not.
     * We only persist the interaction_ac if the participant is not part of a participant pool
     */
    protected ModelledInteraction getDbParentInteraction() {
        return super.getDbParentInteraction();
    }

    protected void setDbExperimentalRoles(Collection<CvTerm> experimentalRoles) {
        this.experimentalRoles = experimentalRoles;
    }

    protected void setRelatedCausalRelationships(Collection<CausalRelationship> relatedCausalRelationships) {
        this.relatedCausalRelationships = relatedCausalRelationships;
    }
}
