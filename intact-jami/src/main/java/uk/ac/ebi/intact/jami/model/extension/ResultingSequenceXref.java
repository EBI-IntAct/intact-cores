package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ResultingSequence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of xref for resulting sequences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_resulting_sequence_xref" )
public class ResultingSequenceXref extends AbstractIntactXref{

    private ResultingSequence parent;

    public ResultingSequenceXref() {
    }

    public ResultingSequenceXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public ResultingSequenceXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public ResultingSequenceXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public ResultingSequenceXref(CvTerm database, String id) {
        super(database, id);
    }

    @ManyToOne( targetEntity = IntactResultingSequence.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactResultingSequence.class)
    public ResultingSequence getParent() {
        return parent;
    }

    public void setParent(ResultingSequence parent) {
        this.parent = parent;
    }
}
