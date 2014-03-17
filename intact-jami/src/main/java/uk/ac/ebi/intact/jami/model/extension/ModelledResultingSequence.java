package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.ResultingSequence;
import psidev.psi.mi.jami.model.Xref;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Intact implementation of range resulting sequence for ranges attached to modelledFeatures
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/01/14</pre>
 */
@Embeddable
public class ModelledResultingSequence extends AbstractIntactResultingSequence implements ResultingSequence{

    public ModelledResultingSequence() {
        super();
    }

    public ModelledResultingSequence(String oldSequence, String newSequence) {
        super(oldSequence, newSequence);
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ModelledResultingSequenceXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ModelledResultingSequenceXref.class)
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }
}
