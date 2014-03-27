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
 * Intact implementation of  participantEvidencePool
 *
 * NOTE: if the participant is not a direct participant of an interaction but is part of a participantSet,
 * the interaction back reference will not be persistent. Only getDbParentPool will be persisted and getDbParentInteraction will return null
 * even if the participant has a back reference to the interaction.
 * NOTE: For backward compatibility with intact-core, a method getDbExperimentalRoles (deprecated) is present but only protected as getExperimentalRole should always be used instead.
 * This method should never be used in any applications.
 * NOTE: getIdentificationMethods is not persistent and getDbIdentificationMethods should be used in HQL queries when we want to check identification methods in the participant which
 * override the identification method in the experiment. The method getDbIdentificationMethods only contain the identification methods that override the one in the experiment if any.
 * NOTE: the methods add and remove will automatically set/reset the dbParentPool property of the sub participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@javax.persistence.Entity
@DiscriminatorValue("participant_evidence_pool")
@Where(clause = "category = 'participant_evidence_pool'")
public class IntactParticipantEvidencePool extends IntactParticipantEvidence implements ParticipantEvidencePool, ParticipantInteractorChangeListener{

    private Collection<ParticipantEvidence> components;
    private CvTerm type;

    protected IntactParticipantEvidencePool() {
        super(new IntactInteractorPool("auto_generated_pool"));
    }

    public IntactParticipantEvidencePool(String name) {
        super(new IntactInteractorPool(name));
    }

    public IntactParticipantEvidencePool(String name, CvTerm bioRole) {
        super(new IntactInteractorPool(name), bioRole);
    }

    public IntactParticipantEvidencePool(String name, Stoichiometry stoichiometry) {
        super(new IntactInteractorPool(name), stoichiometry);
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

    public Iterator<ParticipantEvidence> iterator() {
        return getComponents().iterator();
    }

    public Object[] toArray() {
        return getComponents().toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return getComponents().toArray(ts);
    }

    /**
     * NOTE: This method will automatically set/reset the dbParentPool property of the sub participants to this current participant pool object
     * @param participant
     * @return
     */
    public boolean add(ParticipantEvidence participant) {
        if (getComponents().add(participant)){
            participant.setChangeListener(this);
            getInteractor().add(participant.getInteractor());
            participant.setInteraction(getInteraction());
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
            ParticipantEvidence entity = (ParticipantEvidence)o;
            entity.setChangeListener(null);
            getInteractor().remove(entity.getInteractor());
            entity.setInteraction(null);
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
    public boolean addAll(Collection<? extends ParticipantEvidence> participants) {
        boolean added = false;
        for (ParticipantEvidence entity : participants){
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
        for (ParticipantEvidence entity : this){
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
        for (ParticipantEvidence entity : this){
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
            for (ParticipantEvidence e : this){
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

    @ManyToMany(targetEntity=IntactParticipantEvidence.class)
    @JoinTable(
            name="participant_evidence_pool2participant",
            joinColumns=@JoinColumn(name="evidence_pool_ac"),
            inverseJoinColumns=@JoinColumn(name="participant_ac")
    )
    @Target(IntactParticipantEvidence.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    protected Collection<ParticipantEvidence> getComponents() {
        if (components == null){
            components = new ArrayList<ParticipantEvidence>();
        }
        return components;
    }

    private void setComponents(Collection<ParticipantEvidence> components) {
        this.components = components;
    }

}
