package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Intact implementation of interactor pool
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@DiscriminatorValue( "interactor_pool" )
public class IntactInteractorPool extends IntactInteractor implements InteractorPool {

    private Collection<Interactor> interactors;

    protected IntactInteractorPool(){
        super();
    }

    public IntactInteractorPool(String name, CvTerm type) {
        super(name, type);
    }

    public IntactInteractorPool(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactInteractorPool(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactInteractorPool(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactInteractorPool(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactInteractorPool(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactInteractorPool(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactInteractorPool(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);
    }

    public IntactInteractorPool(String name) {
        super(name);
    }

    public IntactInteractorPool(String name, String fullName) {
        super(name, fullName);
    }

    public IntactInteractorPool(String name, Organism organism) {
        super(name, organism);
    }

    public IntactInteractorPool(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactInteractorPool(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactInteractorPool(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactInteractorPool(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactInteractorPool(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }

    @Transient
    public boolean areInteractorsInitialized(){
        return Hibernate.isInitialized(getInteractors());
    }

    public int size() {
        return interactors.size();
    }

    @Transient
    public boolean isEmpty() {
        return interactors.isEmpty();
    }

    public boolean contains(Object o) {
        return interactors.contains(o);
    }

    public Iterator<Interactor> iterator() {
        return interactors.iterator();
    }

    public Object[] toArray() {
        return interactors.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return interactors.toArray(ts);
    }

    public boolean add(Interactor interactor) {
        return interactors.add(interactor);
    }

    public boolean remove(Object o) {
        return interactors.remove(o);
    }

    public boolean containsAll(Collection<?> objects) {
        return interactors.containsAll(objects);
    }

    public boolean addAll(Collection<? extends Interactor> interactors) {
        return this.interactors.addAll(interactors);
    }

    public boolean retainAll(Collection<?> objects) {
        return interactors.retainAll(objects);
    }

    public boolean removeAll(Collection<?> objects) {
        return interactors.removeAll(objects);
    }

    public void clear() {
        interactors.clear();
    }

    @ManyToMany(targetEntity=IntactInteractor.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="interactor_pool2interactor",
            joinColumns=@JoinColumn(name="pool_ac"),
            inverseJoinColumns=@JoinColumn(name="interactor_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactInteractor.class)
    private Collection<Interactor> getInteractors() {
        if (interactors == null){
            interactors = new ArrayList<Interactor>();
        }
        return interactors;
    }

    private void setInteractors(Collection<Interactor> interactors) {
        this.interactors = interactors;
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(InteractorPool.MOLECULE_SET, InteractorPool.MOLECULE_SET_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }
}
