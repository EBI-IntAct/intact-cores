package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.listener.ParticipantInteractorChangeListener;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Intact implementation of Experimental entity pool
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@javax.persistence.Entity
@DiscriminatorValue("experimental_entity_pool")
public class IntactExperimentalEntityPool extends IntactParticipantEvidence implements ExperimentalEntityPool, ParticipantInteractorChangeListener{

    private Collection<ExperimentalEntity> components;

    protected IntactExperimentalEntityPool() {
        super();
    }

    public IntactExperimentalEntityPool(InteractorPool interactor) {
        super(interactor);
    }

    public IntactExperimentalEntityPool(InteractorPool interactor, CvTerm bioRole) {
        super(interactor, bioRole);
    }

    public IntactExperimentalEntityPool(InteractorPool interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    @Override
    @ManyToOne( targetEntity = IntactInteractorPool.class, optional = false)
    @JoinColumn( name = "interactor_ac", referencedColumnName = "ac")
    @Target(IntactInteractorPool.class)
    @NotNull
    public InteractorPool getInteractor() {
        return (InteractorPool) super.getInteractor();
    }

    @Override
    public void setInteractor(Interactor interactor) {
        if (!(interactor instanceof InteractorPool)){
            throw new UnsupportedOperationException("Cannot set the interactor of an EntityPool as it is an interactorPool that is related to the interactors in the set of entities");
        }
        super.setInteractor(interactor);
    }

    public void setInteractor(InteractorPool interactor) {
        super.setInteractor(interactor);
    }

    @Transient
    @NotNull
    public CvTerm getType() {
        return getInteractor().getInteractorType();
    }

    /**
     * Sets the component set type.
     * Sets the type to molecule set (MI:1304) if the given type is null
     */
    public void setType(CvTerm type) {
        getInteractor().setInteractorType(type);
    }

    public int size() {
        return getComponents().size();
    }

    public boolean isEmpty() {
        return getComponents().isEmpty();
    }

    public boolean contains(Object o) {
        return getComponents().contains(o);
    }

    public Iterator<ExperimentalEntity> iterator() {
        return getComponents().iterator();
    }

    public Object[] toArray() {
        return getComponents().toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return getComponents().toArray(ts);
    }

    public boolean add(ExperimentalEntity interactor) {
        if (getComponents().add(interactor)){
            interactor.setChangeListener(this);
            getInteractor().add(interactor.getInteractor());
            return true;
        }
        return false;
    }

    public boolean remove(Object o) {
        if (getComponents().remove(o)){
            Entity entity = (Entity)o;
            entity.setChangeListener(null);
            getInteractor().remove(entity.getInteractor());
            return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> objects) {
        return getComponents().containsAll(objects);
    }

    public boolean addAll(Collection<? extends ExperimentalEntity> interactors) {
        boolean added = getComponents().addAll(interactors);
        if (added){
            for (ExperimentalEntity entity : this){
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
                interactors.add(((Entity)o).getInteractor());
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
                Entity entity = (Entity)o;
                entity.setChangeListener(null);
                interactors.add(entity.getInteractor());
            }
            // check if an interactor is not in another entity that is kept.
            // remove any interactors that are kept with other entities
            for (ExperimentalEntity entity : this){
                interactors.remove(entity.getInteractor());
            }
            getInteractor().removeAll(interactors);
        }
        return remove;
    }

    public void clear() {
        for (ExperimentalEntity entity : this){
            entity.setChangeListener(null);
        }
        getComponents().clear();
        getInteractor().clear();
    }

    public void onInteractorUpdate(Entity entity, Interactor oldInteractor) {
        // check that the listener still makes sensr
        if (contains(entity)){
            boolean needsToRemoveOldInteractor = true;
            // check if an interactor is not in another entity that is kept.
            // remove any interactors that are kept with other entities
            for (ExperimentalEntity e : this){
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

    @ManyToMany(targetEntity=IntactExperimentalEntity.class)
    @JoinTable(
            name="entity_pool2entity",
            joinColumns=@JoinColumn(name="entity_pool_ac"),
            inverseJoinColumns=@JoinColumn(name="entity_ac")
    )
    @Target(IntactExperimentalEntity.class)
    private Collection<ExperimentalEntity> getComponents() {
        if (components == null){
            components = new ArrayList<ExperimentalEntity>();
        }
        return components;
    }

    private void setComponents(Collection<ExperimentalEntity> components) {
        this.components = components;
    }

}
