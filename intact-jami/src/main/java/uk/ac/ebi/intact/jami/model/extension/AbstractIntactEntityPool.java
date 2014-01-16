package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.listener.ParticipantInteractorChangeListener;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.utils.CvTermUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Abstract class for entity set
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@MappedSuperclass
public class AbstractIntactEntityPool<I extends Interaction, F extends Feature, C extends Entity> extends AbstractIntactParticipant<I,F> implements EntityPool<I,F,C>, ParticipantInteractorChangeListener {

    private Collection<C> components;

    protected AbstractIntactEntityPool() {
        super();
    }

    public AbstractIntactEntityPool(InteractorPool interactor) {
        super(interactor);
    }

    public AbstractIntactEntityPool(InteractorPool interactor, CvTerm bioRole) {
        super(interactor, bioRole);
    }

    public AbstractIntactEntityPool(InteractorPool interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    public AbstractIntactEntityPool(InteractorPool interactor, CvTerm bioRole, Stoichiometry stoichiometry) {
        super(interactor, bioRole, stoichiometry);
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

    public Iterator<C> iterator() {
        return getComponents().iterator();
    }

    public Object[] toArray() {
        return getComponents().toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return getComponents().toArray(ts);
    }

    public boolean add(C interactor) {
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

    public boolean addAll(Collection<? extends C> interactors) {
        boolean added = getComponents().addAll(interactors);
        if (added){
            for (C entity : this){
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
            for (C entity : this){
                interactors.remove(entity.getInteractor());
            }
            getInteractor().removeAll(interactors);
        }
        return remove;
    }

    public void clear() {
        for (C entity : this){
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
            for (C e : this){
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

    protected void initialiseComponentCandidatesSet(){
        this.components = new HashSet<C>();
    }

    @Transient
    protected Collection<C> getComponents() {
        if (components == null){
           components = new ArrayList<C>();
        }
        return components;
    }

    protected void setComponents(Collection<C> components) {
        this.components = components;
    }
}
