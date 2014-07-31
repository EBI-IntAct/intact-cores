package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Intact implementation of modelled feature
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@Table(name = "ia_feature")
@Where(clause = "category = 'modelled'")
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

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ModelledRange.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name = "feature_ac", referencedColumnName = "ac")
    @Target(ModelledRange.class)
    @Override
    public Collection<Range> getRanges() {
        return super.getRanges();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledFeatureXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
    @Target(ModelledFeatureXref.class)
    @Override
    /**
     * This method give direct access to the persistent collection of xrefs (identifiers and xrefs all together) for this object.
     * WARNING: It should not be used to add/remove objects as it may mess up with the state of the object (only used this way by the synchronizers).
     */
    public Collection<Xref> getDbXrefs() {
        return super.getDbXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledFeatureAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
    @Target(ModelledFeatureAnnotation.class)
    @Override
    /**
    * WARNING: The join table is for backward compatibility with intact-core.
    * When intact-core will be removed, the join table would disappear wnd the relation would become
    * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     * **/
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledFeatureAlias.class)
    @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ModelledFeatureAlias.class)
    @Override
    public Collection<Alias> getAliases() {
        return super.getAliases();
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
            name="ia_modfeature2feature",
            joinColumns = @JoinColumn( name="modelled_feature_ac"),
            inverseJoinColumns = @JoinColumn( name="linked_feature_ac")
    )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getDbLinkedFeatures() {
        return super.getDbLinkedFeatures();
    }

    @Override
    @ManyToOne(targetEntity = IntactModelledFeature.class)
    @JoinColumn( name = "linkedfeature_ac", referencedColumnName = "ac" )
    @Target(IntactModelledFeature.class)
    public ModelledFeature getBinds() {
        return super.getBinds();
    }

    @ManyToMany( mappedBy = "dbLinkedFeatures", targetEntity = IntactModelledFeature.class)
    @Target(IntactModelledFeature.class)
    @Override
    /**
     * The collection of features that have this feature in their dbLinkedFeatures collection
     */
    public Collection<ModelledFeature> getRelatedLinkedFeatures() {
        return super.getRelatedLinkedFeatures();
    }

    @OneToMany( mappedBy = "binds", targetEntity = IntactModelledFeature.class)
    @Target(IntactModelledFeature.class)
    @Override
    /**
     * The collection of features that have this feature in their binds property
     */
    public Collection<ModelledFeature> getRelatedBindings() {
        return super.getRelatedBindings();
    }

    @Override
    public void setBinds(ModelledFeature binds) {
        super.setBinds(binds);
    }

    @Override
    protected void initialiseDefaultType() {
        super.setType(IntactUtils.createMIFeatureType(Feature.BIOLOGICAL_FEATURE, Feature.BIOLOGICAL_FEATURE_MI));
    }

    @Column(name = "category", nullable = false, updatable = false)
    @NotNull
    protected String getCategory() {
        return "modelled";
    }

    private void setCategory(String value){
        // nothing to do
    }
}
