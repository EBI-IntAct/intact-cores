package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Intact implementation of modelled entity
 *
 * NOTE: for backward compatibility, modelled entities and experimental entities are in the same table ia_component.
 * In the future, we will update that so we have two different tables : one for modelled entities and one for experimental entities.
 * When this is done, we would be able to remove the where clause attached to this entity.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@javax.persistence.Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_component")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("modelled_entity")
@Where(clause = "category = 'modelled_entity' or category = 'modelled_participant' or category = 'modelled_entity_pool'")
public class IntactModelledEntity extends AbstractIntactEntity<ModelledFeature> implements ModelledEntity{

    protected IntactModelledEntity() {
        super();
    }

    public IntactModelledEntity(Interactor interactor) {
        super(interactor);
    }

    public IntactModelledEntity(Interactor interactor, CvTerm bioRole) {
        super(interactor, bioRole);
    }

    public IntactModelledEntity(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    public IntactModelledEntity(Interactor interactor, CvTerm bioRole, Stoichiometry stoichiometry) {
        super(interactor, bioRole, stoichiometry);
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledEntityXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ModelledEntityXref.class)
    @Override
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledEntityAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinTable(
            name="ia_component2annot",
            joinColumns = @JoinColumn( name="component_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Target(ModelledEntityAnnotation.class)
    @Override
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledEntityAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ModelledEntityAlias.class)
    @Override
    public Collection<Alias> getAliases() {
        return super.getAliases();
    }

    @Override
    @OneToMany( mappedBy = "participant", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactModelledFeature.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getFeatures() {
        return super.getFeatures();
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
    protected void setFeatures(Collection<ModelledFeature> features) {
        super.setFeatures(features);
    }
}
