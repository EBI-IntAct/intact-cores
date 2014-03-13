package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
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
 * Intact implementation of Experimental entity pool
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

    public boolean add(ParticipantEvidence interactor) {
        if (getComponents().add(interactor)){
            interactor.setChangeListener(this);
            getInteractor().add(interactor.getInteractor());
            interactor.setInteraction(getInteraction());
            return true;
        }
        return false;
    }

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

    public boolean addAll(Collection<? extends ParticipantEvidence> participants) {
        boolean added = false;
        for (ParticipantEvidence entity : participants){
            if (add(entity)){
                added = true;
            }
        }
        return added;
    }

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

    public boolean removeAll(Collection<?> objects) {
        boolean removed = false;
        for (Object entity : objects){
            if (remove(entity)){
                removed = true;
            }
        }
        return removed;
    }

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
    private Collection<ParticipantEvidence> getComponents() {
        if (components == null){
            components = new ArrayList<ParticipantEvidence>();
        }
        return components;
    }

    private void setComponents(Collection<ParticipantEvidence> components) {
        this.components = components;
    }

}
