package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.listener.ParticipantInteractorChangeListener;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Intact implementation of ModelledEntitySet
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@DiscriminatorValue("modelled_entity_pool")
public class IntactModelledEntityPool extends IntactModelledParticipant implements ModelledEntityPool, ParticipantInteractorChangeListener {
    private Collection<ModelledEntity> components;
    private CvTerm type;

    protected IntactModelledEntityPool() {
        super(new IntactInteractorPool("auto_generated_pool"));
    }

    public IntactModelledEntityPool(String name) {
        super(new IntactInteractorPool(name));
        setShortLabel(name);
    }

    public IntactModelledEntityPool(String name, CvTerm bioRole) {
        super(new IntactInteractorPool(name), bioRole);
        setShortLabel(name);
    }

    public IntactModelledEntityPool(String name, Stoichiometry stoichiometry) {
        super(new IntactInteractorPool(name), stoichiometry);
        setShortLabel(name);
    }

    @Override
    public void setShortLabel(String shortName) {
        super.setShortLabel(shortName);
        getInteractor().setShortName(shortName);
    }

    @Override
    @OneToMany( mappedBy = "participant", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactModelledFeature.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledFeature.class)
    public Collection<ModelledFeature> getFeatures() {
        return super.getFeatures();
    }

    @Override
    @Transient
    public InteractorPool getInteractor() {
        return (InteractorPool) super.getInteractor();
    }

    @Override
    public void setInteractor(Interactor interactor) {
        throw new UnsupportedOperationException("Cannot set the interactor of an EntityPool as it is an interactorSet that is related to the interactors in the set of entities");
    }

    @ManyToOne(targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "entitytype_ac" )
    @Target(IntactCvTerm.class)
    @NotNull
    public CvTerm getType() {
        return getInteractor().getInteractorType();
    }

    /**
     * Sets the component set type.
     * Sets the type to molecule set (MI:1304) if the given type is null
     */
    public void setType(CvTerm type) {
        if (type == null){
            this.type = IntactUtils.createMIInteractorType(InteractorPool.MOLECULE_SET, InteractorPool.MOLECULE_SET_MI);
        }
        else {
            this.type = type;
        }
        getInteractor().setInteractorType(this.type);
    }

    public int size() {
        return getComponents().size();
    }

    @Transient
    public boolean isEmpty() {
        return getComponents().isEmpty();
    }

    public boolean contains(Object o) {
        return getComponents().contains(o);
    }

    public Iterator<ModelledEntity> iterator() {
        return getComponents().iterator();
    }

    public Object[] toArray() {
        return getComponents().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return getComponents().toArray(a);
    }

    public boolean add(ModelledEntity interactor) {
        if (getComponents().add(interactor)){
            interactor.setChangeListener(this);
            getInteractor().add(interactor.getInteractor());
            return true;
        }
        return false;
    }

    public boolean remove(Object o) {
        if (getComponents().remove(o)){
            psidev.psi.mi.jami.model.Entity entity = (psidev.psi.mi.jami.model.Entity)o;
            entity.setChangeListener(null);
            getInteractor().remove(entity.getInteractor());
            return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> objects) {
        return getComponents().containsAll(objects);
    }

    public boolean addAll(Collection<? extends ModelledEntity> interactors) {
        boolean added = getComponents().addAll(interactors);
        if (added){
            for (ModelledEntity entity : this){
                entity.setChangeListener(this);
                getInteractor().add(entity.getInteractor());
            }
        }
        return added;
    }

    public boolean retainAll(Collection<?> objects) {
        boolean retain = getComponents().retainAll(objects);
        if (retain){
            Collection<Interactor> interactors = new ArrayList<Interactor>(objects.size());
            for (Object o : objects){
                interactors.add(((psidev.psi.mi.jami.model.Entity)o).getInteractor());
            }
            getInteractor().retainAll(interactors);
        }
        return retain;
    }

    public boolean removeAll(Collection<?> objects) {
        boolean remove = getComponents().removeAll(objects);
        if (remove){
            Collection<Interactor> interactors = new ArrayList<Interactor>(objects.size());
            for (Object o : objects){
                psidev.psi.mi.jami.model.Entity entity = (psidev.psi.mi.jami.model.Entity)o;
                entity.setChangeListener(null);
                interactors.add(entity.getInteractor());
            }
            // check if an interactor is not in another entity that is kept.
            // remove any interactors that are kept with other entities
            for (ModelledEntity entity : this){
                interactors.remove(entity.getInteractor());
            }
            getInteractor().removeAll(interactors);
        }
        return remove;
    }

    public void clear() {
        for (ModelledEntity entity : this){
            entity.setChangeListener(null);
        }
        getComponents().clear();
        getInteractor().clear();
    }

    public void onInteractorUpdate(psidev.psi.mi.jami.model.Entity entity, Interactor oldInteractor) {
        // check that the listener still makes sensr
        if (contains(entity)){
            boolean needsToRemoveOldInteractor = true;
            // check if an interactor is not in another entity that is kept.
            // remove any interactors that are kept with other entities
            for (ModelledEntity e : this){
                // we want to check if an interactor is the same as old interactor in another entry
                if (e != entity){
                    if (oldInteractor.equals(e.getInteractor())){
                        needsToRemoveOldInteractor = false;
                    }
                }
            }
            if (!needsToRemoveOldInteractor){
                getInteractor().remove(oldInteractor);
            }
            getInteractor().add(entity.getInteractor());
        }
    }

    @Transient
    public boolean areEntitiesInitialized(){
        return Hibernate.isInitialized(getComponents());
    }

    @Override
    protected void setFeatures(Collection<ModelledFeature> features) {
        super.setFeatures(features);
    }

    @ManyToMany(targetEntity=IntactModelledEntity.class)
    @JoinTable(
            name="entity_pool2entity",
            joinColumns=@JoinColumn(name="entity_pool_ac"),
            inverseJoinColumns=@JoinColumn(name="entity_ac")
    )
    @Target(IntactModelledEntity.class)
    private Collection<ModelledEntity> getComponents() {
        if (this.components == null){
            this.components = new ArrayList<ModelledEntity>();
        }
        return this.components;
    }

    private void setComponents(Collection<ModelledEntity> components) {
        this.components = components;
    }
}
