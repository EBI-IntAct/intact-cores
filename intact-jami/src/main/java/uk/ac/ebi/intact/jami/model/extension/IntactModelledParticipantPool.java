package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.listener.ParticipantInteractorChangeListener;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Intact implementation of ModelledParticipantPool
 *
 * NOTE: if the participant is not a direct participant of an interaction but is part of a participantSet,
 * the interaction back reference will not be persistent. Only getDbParentPool will be persisted and getDbParentInteraction will return null
 * even if the participant has a back reference to the interaction.
 * NOTE: For backward compatibility with intact-core, a method getDbExperimentalRoles (deprecated) is present so the synchronizers can fill up a
 * 'neutral component' role for all modelled participants. This method should never be used in any applications and is public only so the synchronizers can
 * synchronize this property.
 * NOTE: the methods add and remove will automatically set/reset the dbParentPool property of the sub participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@DiscriminatorValue("modelled_participant_pool")
@Where(clause = "category = 'modelled_participant_pool'")
public class IntactModelledParticipantPool extends IntactModelledParticipant implements ModelledParticipantPool, ParticipantInteractorChangeListener {
    private Collection<ModelledParticipant> components;
    private CvTerm type;

    protected IntactModelledParticipantPool() {
        super(new IntactInteractorPool("auto_generated_pool"));
    }

    public IntactModelledParticipantPool(String name) {
        super(new IntactInteractorPool(name));
        setShortLabel(name);
    }

    public IntactModelledParticipantPool(String name, CvTerm bioRole) {
        super(new IntactInteractorPool(name), bioRole);
        setShortLabel(name);
    }

    public IntactModelledParticipantPool(String name, Stoichiometry stoichiometry) {
        super(new IntactInteractorPool(name), stoichiometry);
        setShortLabel(name);
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
    @JoinColumn( name = "participant_type_ac" )
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

    public Iterator<ModelledParticipant> iterator() {
        return getComponents().iterator();
    }

    public Object[] toArray() {
        return getComponents().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return getComponents().toArray(a);
    }

    /**
     * NOTE: This method will automatically set/reset the dbParentPool property of the sub participants to this current participant pool object
     * @param participant
     * @return
     */
    public boolean add(ModelledParticipant participant) {
        if (getComponents().add(participant)){
            // update listener
            participant.setChangeListener(this);
            // add the new interactor in the dynamic interactor set of this participant
            getInteractor().add(participant.getInteractor());
            // set the interaction parent
            participant.setInteraction(getInteraction());
            // set the participant set parent if intact participant
            if (participant instanceof IntactModelledParticipant){
                ((IntactModelledParticipant)participant).setDbParentPool(this);
            }
            return true;
        }
        return false;
    }

    /**
     * NOTE: This method will automatically set the dbParentPool propert of the sub participants to null
     * @param o
     * @return
     */
    public boolean remove(Object o) {
        if (getComponents().remove(o)){
            // update listener
            Participant entity = (Participant)o;
            entity.setChangeListener(null);
            // remove interactor from interactor set
            getInteractor().remove(entity.getInteractor());
            // reset interaction of removed participant
            entity.setInteraction(null);
            // reset the participant set parent if intact participant
            if (entity instanceof IntactModelledParticipant){
                ((IntactModelledParticipant)entity).setDbParentPool(null);
            }
            return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> objects) {
        return getComponents().containsAll(objects);
    }

    /**
     * NOTE: This method will automatically set/reset the dbParentPool property of the sub participants to this current participant pool object
     * @param participants
     * @return
     */
    public boolean addAll(Collection<? extends ModelledParticipant> participants) {
        boolean added = false;
        for (ModelledParticipant entity : participants){
            if (add(entity)){
               added = true;
            }
        }
        return added;
    }

    /**
     * NOTE: This method will automatically set the dbParentPool propert of the sub participants to null if the participant is removed
     * @param objects
     * @return
     */
    public boolean retainAll(Collection<?> objects) {
        for (ModelledParticipant entity : this){
            if (!objects.contains(entity)){
                entity.setChangeListener(null);
                getInteractor().remove(entity.getInteractor());
                entity.setInteraction(null);
            }
        }
        return components.retainAll(objects);
    }

    /**
     * NOTE: This method will automatically set the dbParentPool propert of the sub participants to null
     * @param objects
     * @return
     */
    public boolean removeAll(Collection<?> objects) {
        boolean removed = false;
        for (Object entity : objects){
            if (remove(entity)){
                removed = true;
            }
        }
        return removed;
    }

    /**
     * NOTE: This method will automatically set the dbParentPool propert of the sub participants to null before clearing the collection of
     * sub participants
     */
    public void clear() {
        for (ModelledParticipant entity : this){
            entity.setChangeListener(null);
            entity.setInteraction(null);
        }
        getComponents().clear();
        getInteractor().clear();
    }

    public void onInteractorUpdate(Participant entity, Interactor oldInteractor) {
        // check that the listener still makes sensr
        if (contains(entity)){
            boolean needsToRemoveOldInteractor = true;
            // check if an interactor is not in another entity that is kept.
            // remove any interactors that are kept with other entities
            for (ModelledParticipant e : this){
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
    protected void setShortLabel(String shortName) {
        super.setShortLabel(shortName);
        getInteractor().setShortName(shortName != null ? shortName : "auto_generated_pool");
    }

    @ManyToMany(targetEntity=IntactModelledParticipant.class)
    @JoinTable(
            name="modelled_participant_pool2participant",
            joinColumns=@JoinColumn(name="modelled_pool_ac"),
            inverseJoinColumns=@JoinColumn(name="participant_ac")
    )
    @Target(IntactModelledParticipant.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<ModelledParticipant> getComponents() {
        if (this.components == null){
            this.components = new ArrayList<ModelledParticipant>();
        }
        return this.components;
    }

    private void setComponents(Collection<ModelledParticipant> components) {
        this.components = components;
    }
}
