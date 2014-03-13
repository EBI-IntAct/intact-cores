package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
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
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_component")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("modelled_participant")
@Where(clause = "category = 'modelled_participant' or category = 'modelled_participant_pool'")
public class IntactModelledParticipant extends AbstractIntactParticipant<ModelledInteraction, ModelledFeature, ModelledParticipantPool> implements ModelledParticipant{

    private Collection<CvTerm> experimentalRoles;

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

    @Override
    @OneToMany( mappedBy = "participant", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactModelledFeature.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getFeatures() {
        return super.getFeatures();
    }

    @Transient
    public boolean areExperimentalRolesInitialized(){
        return Hibernate.isInitialized(getExperimentalRoles());
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
    public Collection<CvTerm> getExperimentalRoles() {
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
    protected ModelledParticipantPool getDbParentPool() {
        return super.getDbParentPool();
    }

    @ManyToOne( targetEntity = IntactComplex.class )
    @JoinColumn( name = "interaction_ac" )
    @Target(IntactComplex.class)
    @Override
    protected ModelledInteraction getDbParentInteraction() {
        return super.getDbParentInteraction();
    }

    protected void setExperimentalRoles(Collection<CvTerm> experimentalRoles) {
        this.experimentalRoles = experimentalRoles;
    }
}
