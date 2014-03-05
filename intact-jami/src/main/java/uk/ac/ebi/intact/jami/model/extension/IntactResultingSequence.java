package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.ResultingSequence;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.comparator.range.ResultingSequenceComparator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of range resulting sequence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/01/14</pre>
 */
@Embeddable
public class IntactResultingSequence implements ResultingSequence{

    private String originalSequence;
    private String newSequence;
    private Collection<Xref> xrefs;

    public IntactResultingSequence(){
        this.originalSequence = null;
        this.newSequence = null;
    }

    public IntactResultingSequence(String oldSequence, String newSequence){
        this.originalSequence = oldSequence;
        this.newSequence = newSequence;
    }

    @Lob
    @Column(name = "resulting_sequence")
    public String getNewSequence() {
        return newSequence;
    }

    @Lob
    @Column(name = "full_sequence")
    public String getOriginalSequence() {
        return originalSequence;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ResultingSequenceXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ResultingSequenceXref.class)
    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return xrefs;
    }

    public void setNewSequence(String sequence) {
        this.newSequence = sequence;
    }

    public void setOriginalSequence(String sequence) {
        this.originalSequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof ResultingSequence)){
            return false;
        }

        return ResultingSequenceComparator.areEquals(this, (ResultingSequence) o);
    }

    @Override
    public int hashCode() {
        return ResultingSequenceComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return (originalSequence != null ? "original sequence: "+originalSequence : "") +
                (newSequence != null ? "new sequence: "+newSequence : "");
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getXrefs());
    }

    protected void initialiseXrefs(){
        this.xrefs = new ArrayList<Xref>();
    }

    private void setXrefs(Collection<Xref> xrefs) {
        this.xrefs = xrefs;
    }
}
